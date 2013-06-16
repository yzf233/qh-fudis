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

import java.io.IOException;
import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.FieldCache;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;

import org.apache.nutch.io.*;
import org.apache.nutch.util.*;
import org.apache.nutch.db.*;
import org.apache.nutch.fetcher.*;
import org.apache.nutch.linkdb.*;
import org.apache.nutch.indexer.*;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;

import com.xx.platform.core.StringUtils;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.nutch.IndexImpl;

/** Implements {@link Searcher} and {@link HitDetailer} for either a single
 * merged index, or for a set of individual segment indexes. */
public class IndexSearcher implements Searcher, HitDetailer {

	private org.apache.lucene.search.Searcher luceneSearcher;
	private org.apache.lucene.index.IndexReader reader;

	private LuceneQueryOptimizer optimizer = new LuceneQueryOptimizer(NutchConf
			.get().getInt("searcher.filter.cache.size", 16), NutchConf.get()
			.getFloat("searcher.filter.cache.threshold", 0.05f));

	/** Construct given a number of indexed segments. */
	public IndexSearcher(File[] segmentDirs) throws IOException {
		IndexReader[] readers = new IndexReader[segmentDirs.length];
		for (int i = 0; i < segmentDirs.length; i++) {
			readers[i] = IndexReader.open(new File(segmentDirs[i], "index"));
		}
		init(new MultiReader(readers));
	}

	/** Construct given a directory containing fetched segments, and a separate
	 * directory naming their merged index. */
	public IndexSearcher(String index) throws IOException {
		init(IndexReader.open(index));
	}

	public IndexReader getIndexReader() {
		return this.reader;
	}

	private void init(IndexReader reader) throws IOException {
		this.reader = reader;
		this.luceneSearcher = new org.apache.lucene.search.IndexSearcher(reader);
		this.luceneSearcher.setSimilarity(new NutchSimilarity());
	}

	public Hits search(Query query, int numHits, String dedupField,
			String sortField, boolean reverse)

	throws IOException {
		org.apache.lucene.search.BooleanQuery luceneQuery = QueryFilters
				.filter(query);
		//System.out.println(luceneQuery);
		if(IndexImpl.getQuery()!=null){
			for(BooleanClause clause:IndexImpl.getQuery().getClauses()){
				luceneQuery.add(clause);
			}
		}
		return translateHits(optimizer.optimize(luceneQuery, luceneSearcher,
				numHits, sortField, reverse), dedupField, sortField);
	}

	public String getExplanation(Query query, Hit hit) throws IOException {
		return luceneSearcher.explain(QueryFilters.filter(query),
				hit.getIndexDocNo()).toHtml();
	}

	private FieldSelector fieldSelector = new FieldSelector() {
		public FieldSelectorResult accept(String fieldName) {
			if (fieldName != null && fieldName.equals("docNo"))
				return FieldSelectorResult.LOAD;
			else
				return FieldSelectorResult.NO_LOAD;
		}
	};

	public HitDetails getDetails(Hit hit) throws IOException {
		ArrayList fields = new ArrayList();
		ArrayList values = new ArrayList();
		Document doc = luceneSearcher.doc(hit.getIndexDocNo(), fieldSelector);
		//    Document doc = luceneSearcher.doc(hit.getIndexDocNo());
		//
		//    Enumeration e = doc.fields();
		//    while (e.hasMoreElements()) {
		//      Field field = (Field)e.nextElement();
		//      fields.add(field.name());
		//      values.add(field.stringValue());
		//    }
		java.util.Properties properties = BerkeleyDB.getRecord(doc.get("docNo"));
		if (properties != null) {
			java.util.Iterator iterator = properties.keySet().iterator();
			String key;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				fields.add(key);
				if(StringUtils.isEncryptField(key))
					values.add(StringUtils.decrypt(properties.get(key).toString()));
				else
					values.add(properties.get(key));
			}
		}
		return new HitDetails((String[]) fields.toArray(new String[fields
				.size()]), (String[]) values.toArray(new String[values.size()]));
	}

	public HitDetails[] getDetails(Hit[] hits) throws IOException {
		HitDetails[] results = new HitDetails[hits.length];
		for (int i = 0; i < hits.length; i++)
			results[i] = getDetails(hits[i]);
		return results;
	}

	private Hits translateHits(TopDocs topDocs, String dedupField,
			String sortField) throws IOException {

		String[] dedupValues = null;
		if (dedupField != null)
			dedupValues = FieldCache.DEFAULT.getStrings(reader, dedupField);

		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		int length = scoreDocs.length;
		Hit[] hits = new Hit[length];
		for (int i = 0; i < length; i++) {

			int doc = scoreDocs[i].doc;
			float score = scoreDocs[i].score;
			WritableComparable sortValue; // convert value to writable
			if (sortField == null) {
				sortValue = new FloatWritable(scoreDocs[i].score);
			} else {
				Object raw = ((FieldDoc) scoreDocs[i]).fields[0];
				if (raw instanceof Integer) {
					sortValue = new IntWritable(((Integer) raw).intValue());
				} else if (raw instanceof Float) {
					sortValue = new FloatWritable(((Float) raw).floatValue());
				}else if (raw instanceof Long) {
					sortValue = new LongWritable(((Long) raw).longValue());
				} else {
					sortValue = new UTF8(String.valueOf(raw));
				} 
			}

			String dedupValue = dedupValues == null ? null : dedupValues[doc];

			hits[i] = new Hit(doc, sortValue, dedupValue, score);
		}
		return new Hits(topDocs.totalHits, hits);
	}

}
