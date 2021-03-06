/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.searcher.basic;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;

import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.analysis.CommonGrams;

import org.apache.nutch.searcher.QueryFilter;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Query.*;
import org.apache.nutch.util.NutchConf;

import java.io.IOException;
import java.util.HashSet;

/** The default query filter.  Query terms in the default query field are
 * expanded to search the url, anchor and content document fields.*/
public class BasicQueryFilter implements QueryFilter {

  private static float URL_BOOST =
    NutchConf.get().getFloat("query.url.boost", 4.0f);

  private static float ANCHOR_BOOST =
    NutchConf.get().getFloat("query.anchor.boost", 2.0f);

  private static float TITLE_BOOST =
    NutchConf.get().getFloat("query.title.boost", 1.5f);

    private static float HOST_BOOST =
      NutchConf.get().getFloat("query.host.boost", 2.0f);

  private static int SLOP = Integer.MAX_VALUE;

  private static float PHRASE_BOOST =
    NutchConf.get().getFloat("query.phrase.boost", 1.0f);

  private static final String[] FIELDS =
  {"content"};

  private static final float[] FIELD_BOOSTS =
  {5.0f};

  /**
   * Set the boost factor for url matches, relative to content and anchor
   * matches
   */
  public static void setUrlBoost(float boost) { URL_BOOST = boost; }

  /** Set the boost factor for title/anchor matches, relative to url and
   * content matches. */
  public static void setAnchorBoost(float boost) { ANCHOR_BOOST = boost; }

  /** Set the boost factor for sloppy phrase matches relative to unordered term
   * matches. */
  public static void setPhraseBoost(float boost) { PHRASE_BOOST = boost; }

  /** Set the maximum number of terms permitted between matching terms in a
   * sloppy phrase match. */
  public static void setSlop(int slop) { SLOP = slop; }

  public BooleanQuery filter(Query input, BooleanQuery output) {
    addTerms(input, output);
    //addSloppyPhrases(input, output);
    return output;
  }

  private static void addTerms(Query input, BooleanQuery output) {
    Clause[] clauses = input.getClauses();
    for (int i = 0; i < clauses.length; i++) {
      Clause c = clauses[i];
      if (!c.getField().equals(Clause.DEFAULT_FIELD))
        continue;                                 // skip non-default fields

      BooleanQuery out = new BooleanQuery();
      for (int f = 0; f < FIELDS.length; f++) {

        Clause o = c;
        if (c.isPhrase()) {                         // optimize phrase clauses
          String[] opt = CommonGrams.optimizePhrase(c.getPhrase(), FIELDS[f]);
          if (opt.length==1) {
            o = new Clause(new Term(opt[0]), c.isRequired(), c.isProhibited());
            out.add(termQuery(FIELDS[f], o.getTerm(), FIELD_BOOSTS[f]),
                    false, false);
          } else {
            o = new Clause(c.getPhrase(), c.isRequired(), c.isProhibited());
//            output.add(exactPhrase(o.getPhrase(), c.getField(), 2.0f),
//                       c.isRequired(), c.isProhibited());
            output.add(exactPhrase(o.getPhrase(), FIELDS[f], FIELD_BOOSTS[f]),
                       c.isRequired(), c.isProhibited());
          }
        }else
        {
            out.add(termQuery(FIELDS[f], o.getTerm(), FIELD_BOOSTS[f]),
                    c.isRequired(), c.isProhibited());
        }
      }
      if(out.getClauses().length>0)
          output.add(out, c.isRequired(), c.isProhibited());
    }
  }

  private static void addSloppyPhrases(Query input, BooleanQuery output) {
    Clause[] clauses = input.getClauses();
    for (int f = 0; f < FIELDS.length; f++) {

      PhraseQuery sloppyPhrase = new PhraseQuery();
      sloppyPhrase.setBoost(FIELD_BOOSTS[f] * PHRASE_BOOST);
      sloppyPhrase.setSlop("anchor".equals(FIELDS[f])
                           ? NutchDocumentAnalyzer.INTER_ANCHOR_GAP
                           : SLOP);
      int sloppyTerms = 0;

      for (int i = 0; i < clauses.length; i++) {
        Clause c = clauses[i];

        if (!c.getField().equals(Clause.DEFAULT_FIELD))
          continue;                               // skip non-default fields

        if (c.isPhrase())                         // skip exact phrases
          continue;

        if (c.isProhibited())                     // skip prohibited terms
          continue;

        sloppyPhrase.add(luceneTerm(FIELDS[f], c.getTerm()));
        sloppyTerms++;
      }

      if (sloppyTerms > 1)
        output.add(sloppyPhrase, false, false);
    }
  }


  private static org.apache.lucene.search.Query
        termQuery(String field, Term term, float boost) {
    TermQuery result = new TermQuery(luceneTerm(field, term));
    result.setBoost(boost);
    return result;
  }

  private static org.apache.lucene.search.Query
      exactPhrase(Phrase nutchPhrase,
                  String field, float boost) {
    org.apache.nutch.searcher.Query.Term[] terms = nutchPhrase.getTerms();
    BooleanQuery booleanQuery = new BooleanQuery();
    for (int i = 0; i < terms.length; i++) {
      booleanQuery.add(new TermQuery(luceneTerm(field, terms[i])), false, false);
    }
    booleanQuery.setBoost(boost);
    return booleanQuery;
  }

  /** Utility to construct a Lucene Term given a Nutch query term and field. */
  private static org.apache.lucene.index.Term luceneTerm(String field,
      org.apache.nutch.searcher.Query.Term term) {
    return new org.apache.lucene.index.Term(field, term.toString());
  }

}
