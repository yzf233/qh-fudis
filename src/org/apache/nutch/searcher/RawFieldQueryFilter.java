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

package org.apache.nutch.searcher;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

import org.apache.nutch.searcher.Query.Clause;
import org.apache.lucene.search.RangeQuery;
import org.apache.nutch.analysis.CommonGrams;
import org.apache.nutch.searcher.Query.Phrase;

/** Translate raw query fields to search the same-named field, as indexed by an
 * IndexingFilter. */
public abstract class RawFieldQueryFilter implements QueryFilter {
  private String field;
  private boolean lowerCase;
  private float boost;

  /** Construct for the named field, lowercasing query values.*/
  protected RawFieldQueryFilter(String field) {
    this(field, true);
  }

  /** Construct for the named field, lowercasing query values.*/
  protected RawFieldQueryFilter(String field, float boost) {
    this(field, true, boost);
  }

  /** Construct for the named field, potentially lowercasing query values.*/
  protected RawFieldQueryFilter(String field, boolean lowerCase) {
    this(field, lowerCase, 1.0f);
  }

  /** Construct for the named field, potentially lowercasing query values.*/
  protected RawFieldQueryFilter(String field, boolean lowerCase, float boost) {
    this.field = field;
    this.lowerCase = lowerCase;
    this.boost = boost;
  }

  public BooleanQuery filter(Query input, BooleanQuery output)
    throws QueryException {

    // examine each clause in the Nutch query
    Clause[] clauses = input.getClauses();
    for (int i = 0; i < clauses.length; i++) {
      Clause c = clauses[i];

      // skip non-matching clauses
      if(c==null || c.getField()==null)
          continue;
      if (!c.getField().equals(field))
        continue;

      // get the field value from the clause
      // raw fields are guaranteed to be Terms, not Phrases
      if (c.isPhrase()) {                         // optimize phrase clauses
          Clause o = c;
          String[] opt = CommonGrams.optimizePhrase(c.getPhrase(), c.getField());
          if (opt.length==1) {
            o = new Clause(new org.apache.nutch.searcher.Query.Term(opt[0]), c.isRequired(), c.isProhibited());
            output.add(termQuery(c.getField(), o.getTerm(), 2.0f),c.isRequired(), c.isProhibited());
          } else {
            o = new Clause(c.getPhrase(), c.isRequired(), c.isProhibited());
            output.add(exactPhrase(o.getPhrase(), c.getField(), 2.0f),
                       c.isRequired(), c.isProhibited());

          }
        }else
        {

            String value = c.getTerm().toString();
            if (c.isRange()) {
                com.xx.platform.core.nutch.RangeQuery range = new com.xx.platform.core.nutch.RangeQuery(new Term(field,
                        c.getBeginRange()),
                        new Term(field, c.getEndRange()),
                        c.isIncludeRange());

                range.setBoost(boost);
                output.add(range, c.isRequired(), c.isProhibited());
            } else {
                if (lowerCase)
                    value = value.toLowerCase();
                // add a Lucene TermQuery for this clause
                TermQuery clause = new TermQuery(new Term(field, value));
                // set boost
                clause.setBoost(boost);
                // add it as specified in query
                output.add(clause, c.isRequired(), c.isProhibited());
            }
        }
    }

    // return the modified Lucene query
    return output;
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

  private static org.apache.lucene.search.Query
        termQuery(String field, org.apache.nutch.searcher.Query.Term term, float boost) {
    TermQuery result = new TermQuery(new org.apache.lucene.index.Term(field , term.toString()));
    result.setBoost(boost);
    return result;
  }

}
