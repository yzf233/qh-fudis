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

import org.apache.nutch.plugin.*;
import org.apache.nutch.searcher.Query.Clause;
import org.apache.nutch.searcher.xdht.MQueryFilter;
import org.apache.nutch.util.LogFormatter;
import java.util.logging.Logger;
import java.util.*;

import org.apache.lucene.search.BooleanQuery;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.nutch.QueryIndexField;

/** Creates and caches {@link QueryFilter} implementing plugins.  QueryFilter
 * implementations should define either the "fields" or "raw-fields" attributes
 * for any fields that they process, otherwise these will be ignored by the
 * query parser.  Raw fields are parsed as a single Query.Term, including
 * internal punctuation, while non-raw fields are parsed containing punctuation
 * are parsed as multi-token Query.Phrase's.
 */
public class QueryFilters {
  private static final Logger LOG =
    LogFormatter.getLogger("org.apache.nutch.searcher.QueryFilters");

  private static final QueryFilter[] CACHE;
  private static final HashSet FIELD_NAMES = new HashSet();
  private static final HashSet RAW_FIELD_NAMES = new HashSet();

  static {
    try {
      ExtensionPoint point = PluginRepository.getInstance()
        .getExtensionPoint(QueryFilter.X_POINT_ID);
      if (point == null)
        throw new RuntimeException(QueryFilter.X_POINT_ID+" not found.");
      Extension[] extensions = point.getExtensions();
      CACHE = new QueryFilter[extensions.length+SearchContext.getIndexFieldSet().size()+12];
      for (int i = 0; i < extensions.length; i++) {
        Extension extension = extensions[i];
        ArrayList fieldNames = parseFieldNames(extension, "fields");
        ArrayList rawFieldNames = parseFieldNames(extension, "raw-fields");
        if (fieldNames.size() == 0 && rawFieldNames.size() == 0) {
          LOG.warning("QueryFilter: "+extension.getId()+" names no fields.");
          continue;
        }
        CACHE[i] = (QueryFilter)extension.getExtensionInstance();
        FIELD_NAMES.addAll(fieldNames);
        FIELD_NAMES.addAll(rawFieldNames);
        RAW_FIELD_NAMES.addAll(rawFieldNames);
      }
      QueryIndexField categoryQueryIndex = new QueryIndexField("docType");
      QueryIndexField docSourceQueryIndex = new QueryIndexField("docSource");
      QueryIndexField contentQueryIndex = new QueryIndexField("content");
      
      QueryIndexField urlQueryIndex = new QueryIndexField("url");
      QueryIndexField dataSourceQueryIndex = new QueryIndexField("dataSource");
      QueryIndexField contentLengthQueryIndex = new QueryIndexField("contentLength");
      QueryIndexField createDateQueryIndex = new QueryIndexField("createDate");
      QueryIndexField updataDateQueryIndex = new QueryIndexField("updataDate");
      QueryIndexField siteQueryIndex = new QueryIndexField("site");
      QueryIndexField typeQueryIndex = new QueryIndexField("type");
      QueryIndexField subTypeQueryIndex = new QueryIndexField("subType");
      QueryIndexField docNoQueryIndex = new QueryIndexField("docNo");
      
      int i = extensions.length ;
      for (IndexField indexField : SearchContext.getIndexFieldSet()) {
          FIELD_NAMES.add(indexField.getCode());
          CACHE[i++] = new QueryIndexField(indexField.getCode()) ;
        }
    
    	FIELD_NAMES.add("docNo");
      	CACHE[CACHE.length-12] = docNoQueryIndex ;
      
      	FIELD_NAMES.add("url");
      	CACHE[CACHE.length-11] = urlQueryIndex ;
      	FIELD_NAMES.add("dataSource");
      	CACHE[CACHE.length-10] = dataSourceQueryIndex ;
      	
      	FIELD_NAMES.add("contentLength");
      	CACHE[CACHE.length-9] = contentLengthQueryIndex ;
      	FIELD_NAMES.add("createDate");
      	CACHE[CACHE.length-8] = createDateQueryIndex ;
      	FIELD_NAMES.add("updataDate");
      	CACHE[CACHE.length-7] = updataDateQueryIndex ;
      	FIELD_NAMES.add("site");
      	CACHE[CACHE.length-6] = siteQueryIndex ;
      	FIELD_NAMES.add("type");
      	CACHE[CACHE.length-5] = typeQueryIndex ;
      	FIELD_NAMES.add("subType");
      	CACHE[CACHE.length-4] = subTypeQueryIndex ;

        FIELD_NAMES.add("content");
        CACHE[CACHE.length-3] = contentQueryIndex ;
        FIELD_NAMES.add("docType");
        CACHE[CACHE.length-2] = categoryQueryIndex ;
        FIELD_NAMES.add("docSource");
        CACHE[CACHE.length-1] = docSourceQueryIndex ;
        
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static ArrayList parseFieldNames(Extension extension,
                                           String attribute) {
    String fields = extension.getAttribute(attribute);
    if (fields == null) fields = "";
    return Collections.list(new StringTokenizer(fields, " ,\t\n\r"));
  }

  private  QueryFilters() {}                  // no public ctor

  /** Run all defined filters. */
  public static BooleanQuery filter(Query input) throws QueryException {
    // first check that all field names are claimed by some plugin
    Clause[] clauses = input.getClauses();
    for (int i = 0; i < clauses.length; i++) {
      Clause c = clauses[i];
      if (!isField(c.getField()))
          FIELD_NAMES.add(c.getField());
        //throw new QueryException("Not a known field name:"+c.getField());
    }

    // then run each plugin
    BooleanQuery output = new BooleanQuery();
    for (int i = 0 ; i < CACHE.length; i++) {
      output = CACHE[i].filter(input, output);
    }
    return output;
  }

  public static boolean isField(String name) {
    return FIELD_NAMES.contains(name);
  }
  public static boolean isRawField(String name) {
    return RAW_FIELD_NAMES.contains(name);
  }
}
