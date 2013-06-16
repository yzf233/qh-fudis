package com.xx.platform.core.nutch;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import java.io.File;
import org.apache.lucene.index.MultiReader;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.db.WebDbAdminTool;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.Hits;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.ResultFilter;

import java.util.BitSet;
import java.util.Properties;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.nutch.searcher.QueryFilters;

/**
 * <p>Title: 索引工具类</p>
 *
 * <p>Description: 删除索引时判断爬虫状态好像多余，暂时还留着</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class IndexImpl implements IndexInterface{
	private static BooleanQuery query = null;
    private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
	
	
	public boolean deleteDocuments(int ino, int docno) throws IOException {
		// TODO Auto-generated method stub
		NutchBean bean = NutchBean.getBean();
		Hit hit = new Hit(ino,docno);
		HitDetails details = bean.getDetails(hit);
		String docNo = String.valueOf(hit.getIndexDocNo());
		String type = details.getValue("type")==null?"":details.getValue("type");
		String subtype = details.getValue("subType")==null?"":details.getValue("subType");
		String docSource = details.getValue("docSource")==null?"":details.getValue("docSource");
		if(docNo!=null && docNo.trim().length()>0){
			Properties property = new Properties();
			property.put("docNo", docNo);
	    	 if(docSource.equals("database"))//删除数据库去重数据
	    	 {
	    		 try {
					webDbAdminTool.removeContents(type);
				} catch (Exception e) {
				}
	    	 }
	         else if(docSource.equals("file"))//删除文件去重数据
	    	 {
	        	 try {
					webDbAdminTool.removeContents(type);
				} catch (Exception e) {
				}
	        	 try {
					webDbAdminTool.removeContents(subtype);
				} catch (Exception e) {
				}
	    	 }
			try {
				SearchContext.addResultFilter(docNo);
				BerkeleyDB.saveDelDoc(docNo, property);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			//do create filter
//			addQuery("docNo",docNo);
			return true;
		}
		return false;
	}

	public int deleteDocuments(String field, String value) throws IOException {
		NutchBean bean = NutchBean.getBean();
		Query query = new Query();
		query.addRequiredTerm(value, field);
		Hits hits = bean.search(query, 100);
		if(hits.getTotal()>100){
			hits = bean.search(query, (int)hits.getTotal());
		}
		int total = 0;
		for(int i=0;i<hits.getLength();i++){
			HitDetails details = bean.getDetails(hits.getHit(i));
			String docNo = String.valueOf(hits.getHit(i).getIndexDocNo());
			String type = details.getValue("type")==null?"":details.getValue("type");
			String subtype = details.getValue("subType")==null?"":details.getValue("subType");
			String docSource = details.getValue("docSource")==null?"":details.getValue("docSource");
			if(docNo!=null && docNo.trim().length()>0){
				Properties property = new Properties();
				property.put("docNo", docNo);
		    	 if(docSource.equals("database"))//删除数据库去重数据
		    	 {
		    		 try {
						webDbAdminTool.removeContents(type);
					} catch (Exception e) {
					}
		    	 }
		         else if(docSource.equals("file"))//删除文件去重数据
		    	 {
		        	 try {
						webDbAdminTool.removeContents(type);
					} catch (Exception e) {
					}
		        	 try {
						webDbAdminTool.removeContents(subtype);
					} catch (Exception e) {
					}
		    	 }
				try {
					SearchContext.addResultFilter(docNo);
					BerkeleyDB.saveDelDoc(docNo, property);
					total++;
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					return -1;
				}
				//do create filter
//				addQuery("docNo",docNo);
			}
		}
		return total;
	}

	public void deleteDocuments(Query query) throws IOException {
		// TODO Auto-generated method stub
		NutchBean bean = NutchBean.getBean();
		Hits hits = bean.search(query, 100);
		if(hits.getTotal()>100){
			hits = bean.search(query, (int)hits.getTotal());
		}
//		int total = 0;
		for(int i=0;i<hits.getLength();i++){
			HitDetails details = bean.getDetails(hits.getHit(i));
			String docNo = String.valueOf(hits.getHit(i).getIndexDocNo());
			String type = details.getValue("type")==null?"":details.getValue("type");
			String subtype = details.getValue("subType")==null?"":details.getValue("subType");
			String docSource = details.getValue("docSource")==null?"":details.getValue("docSource");
			if(docNo!=null && docNo.trim().length()>0){
				Properties property = new Properties();
				property.put("docNo", docNo);
		    	 if(docSource.equals("database"))//删除数据库去重数据
		    	 {
		    		 try {
						webDbAdminTool.removeContents(type);
					} catch (Exception e) {
					}
		    	 }
		         else if(docSource.equals("file"))//删除文件去重数据
		    	 {
		        	 try {
						webDbAdminTool.removeContents(type);
					} catch (Exception e) {
					}
		        	 try {
						webDbAdminTool.removeContents(subtype);
					} catch (Exception e) {
					}
		    	 }
				try {
					SearchContext.addResultFilter(docNo);
					BerkeleyDB.saveDelDoc(docNo, property);
//					total++;
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					return -1;
				}
				//do create filter
//				addQuery("docNo",docNo);
			}
		}
//		return total;
		
	}
	
	private void addQuery(String field,String value){
		if(query==null)
			query = new BooleanQuery();
		query.add(new TermQuery(new Term(field,value)), false, true);
	}

	public static BooleanQuery getQuery() {
		return query;
	}

	public static void setQuery(BooleanQuery query) {
		IndexImpl.query = query;
	}
}
