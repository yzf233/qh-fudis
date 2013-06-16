package org.apache.nutch.searcher.xdht;

import org.apache.lucene.queryParser.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.nutch.searcher.*;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.QueryFilter;
import org.apache.nutch.searcher.Query.Clause;
import org.apache.nutch.util.*;

/**
 * <p>Title: 片短语查询</p>
 *
 * <p>Description: 会导致其他的queryfilter都不可用,content字段范围搜索等都不可用</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class MQueryFilter
    implements QueryFilter {
	  private String field;
	  private boolean lowerCase;
	  private float boost;

	public MQueryFilter(String field) {
		    this.field = field;
		    this.lowerCase = true;
		    this.boost = 1.0f;
		  }
  public BooleanQuery filter(Query input, BooleanQuery output) throws
      QueryException {
	  Clause[] clauses = input.getClauses();

	  if(clauses!=null&&clauses.length==1&&clauses[0].toString().equals("542B22610658085FBA1C1C709072083B:"))
	  {
	      return parse(input,output);
	  }
	  else
    return output;
  }

  /**
   * 解析query
   * @param input Query
   * @return BooleanQuery
   */
  private BooleanQuery parse(Query input,BooleanQuery output) {
	      QueryParser queryParse;
	      queryParse = new QueryParser("content",
	    		  new org.apache.nutch.analysis.NutchDocumentAnalyzer());
	      
	      try 	{
	    	  org.apache.lucene.search.Query q=queryParse.parse(input.getHquery());
	    	  output.add(q,true,false);
	    	  output.getClauses()[0].setOccur(Occur.MUST);
	      }
	      catch (ParseException ex) {
	      }
	  return output;
  }
}
