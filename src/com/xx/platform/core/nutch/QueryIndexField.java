package com.xx.platform.core.nutch;

import org.apache.nutch.searcher.RawFieldQueryFilter;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class QueryIndexField  extends RawFieldQueryFilter {
    public QueryIndexField(String indexField) {
     super(indexField);
   }
}
