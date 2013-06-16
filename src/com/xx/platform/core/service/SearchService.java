package com.xx.platform.core.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.analysis.XdNutchDocumentAnalyzer;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Summarizer;

import com.xx.platform.core.PYContext;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.search.SearchImpl;
import com.xx.platform.core.search.SearchList;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;
import com.xx.platform.web.actions.search.SearchReportExe;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
@WebService(serviceName = "search", endpointInterface = "com.xx.platform.core.service.ServiceInterface")
public class SearchService implements ServiceInterface {
	List<com.xx.platform.web.actions.search.SearchResult> searchResultList = null;
	private static final Analyzer ANALYZER = new NutchDocumentAnalyzer();
	private static final Analyzer XDANALYZER = new XdNutchDocumentAnalyzer();

	private String cansort = "boost,docNo,docSource,url,dataSource,docType,segment,contentLength,createDate,updataDate,site,type,subType";

	@WebResult(name = "search")
	public List<WebServiceSearchResult> search(String keyword, int start,
			int hitsPerPage) throws Exception {
		if (!CheckIPUtil.checkIpFacadeWebService()) {
			return null;
		}
		if (keyword == null || keyword.trim().equals(""))
			return null;
		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		Query query = new org.apache.nutch.searcher.Query();
		Map<String, String[]> QueryMap = new HashMap();// 定义一个map来存或者的搜索条件-胡俊
		for (IndexField fd : SearchContext.getIndexFieldSet()) {
			QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
					fd.getCode(), keyword, "true" });
		}
		QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
				"content", keyword, "true" });
		String[][] requery = new String[QueryMap.size()][3];
		Iterator it = QueryMap.entrySet().iterator();
		int keynum = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object value = entry.getValue();
			requery[keynum] = (String[]) value;
			keynum++;
		}
		UserQuery u = new UserQuery();
		u.setRequireOneMoreKey(requery);
		String queryString = "";
		UserQueryConvert uc = new UserQueryConvert();
		queryString = uc.ConvertUserQueryToString(new UserQuery[] { u });
		query.setXdht(queryString);

		searchResultList = searchImpl.search(nutchBean, keyword, query,
				start > 0 ? start : 0, hitsPerPage > 0 ? hitsPerPage
						: SearchImpl.HITS_PER_PAGE);
		/*
		 * 把搜索记录记入数据库中
		 */
		String userName = null;
		// if(user!=null){
		// userName=user.getName();
		// }
		SearchReportExe sre = new SearchReportExe(keyword, queryString,
				searchResultList.size(), userName, 0, SearchContext
						.getDataHandler());
		sre.saveRecord();

		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			// webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
					.getIndexFieldSet().size() + 2]);
			int i = 0;
			StringBuffer summaryvalues = new StringBuffer();
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
					summaryvalues.append(ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode()))
							+ " ");
				}
			}
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 2] = new XDIndexValue("docType",
					searchResult.getDetails().getValue("docType"));
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 1] = new XDIndexValue("content",
					ZfilterSome.doAll(searchResult.getContent()));
			summaryvalues.append(ZfilterSome.doAll(searchResult.getContent()));

			// Summarizer s=new Summarizer();
			// Query q = new org.apache.nutch.searcher.Query();
			// Token[] t = simple_getTokens(keyword);
			// for (Token token : t) {
			// q.addRequiredTerm(token.termText());
			// }
			// webServiceSearchResult.setSummaries(s.getSummary(summaryvalues.toString(),q).toString());
			//      
			webServiceSearchResult.setSummaries(Summarizer.getsummary(
					"+content:" + strfilter(keyword), summaryvalues.toString(),
					SearchContext.getAnalyzer(1)));

			webServiceResult.add(webServiceSearchResult);
		}

		return webServiceResult;
	}

	private String strfilter(String str) {
		if (str == null || (str != null && str.length() == 0))
			return "";
		String res = "";
		for (int i = 0; i < str.length(); i++) {
			res += "\\" + str.charAt(i);
		}
		return res;
	}

	@WebResult(name = "search")
	public List<WebServiceSearchResult> search(String keyword,
			String[][] field, int start, int hitsPerPage) throws Exception {
		if (!CheckIPUtil.checkIpFacadeWebService()) {
			return null;
		}
		if (keyword == null || keyword.trim().equals(""))
			return null;
		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();

		Query query = new org.apache.nutch.searcher.Query();
		Map<String, String[]> QueryMap = new HashMap();
		for (IndexField fd : SearchContext.getIndexFieldSet()) {
			QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
					fd.getCode(), keyword, "true" });
		}
		QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
				"content", keyword, "true" });
		String[][] requery = new String[QueryMap.size()][3];
		Iterator it = QueryMap.entrySet().iterator();
		int keynum = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object value = entry.getValue();
			requery[keynum] = (String[]) value;
			keynum++;
		}
		UserQuery u = new UserQuery();
		u.setRequireOneMoreKey(requery);
		String queryString = "";
		UserQueryConvert uc = new UserQueryConvert();
		queryString = uc.ConvertUserQueryToString(new UserQuery[] { u });
		query.setXdht(queryString);
		searchResultList = searchImpl.search(nutchBean, keyword, query,
				start > 0 ? start : 0, hitsPerPage > 0 ? hitsPerPage
						: SearchImpl.HITS_PER_PAGE);
		/*
		 * 把搜索记录记入数据库中
		 */
		String userName = null;
		// if(user!=null){
		// userName=user.getName();
		// }
		SearchReportExe sre = new SearchReportExe(keyword, queryString,
				searchResultList.size(), userName, 0, SearchContext
						.getDataHandler());
		sre.saveRecord();

		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			// webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			int num = 0;
			int hascontent = 0;// 是否包括包括content
			boolean sumContent = false;// content是否摘要
			if (field != null) {
				for (String s[] : field) {
					if ("content".equals(s[0])
							&& "file".equals(webServiceSearchResult
									.getDocSource())) {
						hascontent = 1;
						if (s[1].equals("true"))
							sumContent = true;
					}
				}
			} else
				hascontent = 0;
			for (IndexField fd : SearchContext.getIndexFieldSet()) {
				if (field == null)
					break;
				for (String s[] : field) {
					if (s[0].equals(fd.getCode()))
						num++;
				}
			}// num记录用户输入的去除索引字段和实际索引字段重复的个数
			webServiceSearchResult.setIndexMap(new XDIndexValue[hascontent
					+ num]);
			int i = 0;
			StringBuffer summaryvalues = new StringBuffer();
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (field == null) {
					num = SearchContext.getIndexFieldSet().size();
					break;
				}
				boolean cansummary = false;
				boolean hasfield = false;
				if (field != null)
					for (String s[] : field) {
						if (s[0].equals(indexField.getCode())) {
							hasfield = true;
							if (s[1].equals("true"))
								cansummary = true;
						}
					}
				if (!hasfield)
					continue;// 如果用户输入没有这个字段，则继续判断下一个字段
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
					if (cansummary)
						summaryvalues.append(ZfilterSome.doAll(searchResult
								.getDetails().getValue(indexField.getCode()))
								+ " ");
				}
			}
			// Summarizer s=new Summarizer();
			// Query q = new org.apache.nutch.searcher.Query();
			// Token[] t = simple_getTokens(keyword);
			// for (Token token : t) {
			// q.addRequiredTerm(token.termText());
			// }
			// webServiceSearchResult.getIndexMap()[webServiceSearchResult.
			// getIndexMap().length -
			// 2] = new XDIndexValue("docType",
			// searchResult.getDetails().
			// getValue("docType"));
			if (hascontent == 1
					&& webServiceSearchResult.getDocSource().equals("file")) {
				webServiceSearchResult.getIndexMap()[webServiceSearchResult
						.getIndexMap().length - 1] = new XDIndexValue(
						"content", ZfilterSome.doAll(searchResult.getContent()));
				if (sumContent) {
					summaryvalues.setLength(0);
					summaryvalues.append(ZfilterSome.doAll(searchResult
							.getContent()));
				}
			}
			// webServiceSearchResult.setSummaries(s.getSummary(summaryvalues.toString(),q).toString());
			webServiceSearchResult.setSummaries(Summarizer.getsummary(
					"+content:" + strfilter(keyword), summaryvalues.toString(),
					SearchContext.getAnalyzer(1)));
			webServiceResult.add(webServiceSearchResult);
		}

		return webServiceResult;
	}

	@WebResult(name = "search")
	public List<WebServiceSearchResult> search(String keyword,
			String[][] field, int start, int hitsPerPage, String[][] sortReg,
			String dedupField) throws Exception {
		if (!CheckIPUtil.checkIpFacadeWebService()) {
			return null;
		}
		if (keyword == null || keyword.trim().equals(""))
			return null;

		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		String sortfiled = "";
		try {
			for (String[] s : sortReg) {

				for (IndexField fd : SearchContext.getIndexFieldSet()) {
					if (s[0].equals(fd.getCode()) && fd.isToken() == false
							&& !s[1].equals("score") && !s[1].equals("score")) {
						sortfiled += s[0] + ":" + s[1] + ",";
					}
					if (isSystemSortfield(s[0], cansort)
							&& !s[1].equals("score") && !s[1].equals("score")) {
						sortfiled += s[0] + ":" + s[1] + ",";
						continue;
					}
					if (s[1].equals("score")) {
						sortfiled += s[0] + ":" + s[1] + ",";
						continue;
					}
				}
			}
		} catch (Exception e) {
			sortfiled = "";
		}
		if (sortfiled.equals("")) {
			sortfiled = null;
		}
		Query query = new org.apache.nutch.searcher.Query();
		Map<String, String[]> QueryMap = new HashMap();
		for (IndexField fd : SearchContext.getIndexFieldSet()) {
			QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
					fd.getCode(), keyword, "true" });
		}
		QueryMap.put("或条件" + String.valueOf(QueryMap.size()), new String[] {
				"content", keyword, "true" });
		String[][] requery = new String[QueryMap.size()][3];
		Iterator it = QueryMap.entrySet().iterator();
		int keynum = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object value = entry.getValue();
			requery[keynum] = (String[]) value;
			keynum++;
		}
		UserQuery u = new UserQuery();
		u.setRequireOneMoreKey(requery);
		String queryString = "";
		UserQueryConvert uc = new UserQueryConvert();
		queryString = uc.ConvertUserQueryToString(new UserQuery[] { u });
		query.setXdht(queryString);
		if (dedupField == null || (dedupField != null && dedupField.equals("")))
			dedupField = "docNo";
		searchResultList = searchImpl
				.search(nutchBean, keyword, query, start > 0 ? start : 0,
						hitsPerPage > 0 ? hitsPerPage
								: SearchImpl.HITS_PER_PAGE, sortfiled, true,
						dedupField);
		
		/*
		 * 把搜索记录记入数据库中
		 */
		String userName = null;
		// if(user!=null){
		// userName=user.getName();
		// }
		SearchReportExe sre = new SearchReportExe(keyword, queryString,
				searchResultList.size(), userName, 0, SearchContext
						.getDataHandler());
		sre.saveRecord();
		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			// webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));
			int num = 0;
			int hascontent = 0;// 是否包括包括content
			boolean sumContent = false;// content是否摘要
			if (field != null) {
				for (String s[] : field) {
					if (s[0].equals("content")
							&& webServiceSearchResult.getDocSource().equals(
									"file")) {
						hascontent = 1;
						if (s[1].equals("true"))
							sumContent = true;
					}
				}
			} else
				hascontent = 0;
			for (IndexField fd : SearchContext.getIndexFieldSet()) {
				if (field == null)
					break;
				for (String s[] : field) {
					if (s[0].equals(fd.getCode()))
						num++;
				}
			}// num记录用户输入的去除索引字段和实际索引字段重复的个数
			webServiceSearchResult.setIndexMap(new XDIndexValue[hascontent
					+ num]);
			int i = 0;
			StringBuffer summaryvalues = new StringBuffer();
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (field == null) {
					num = SearchContext.getIndexFieldSet().size();
					break;
				}
				boolean cansummary = false;
				boolean hasfield = false;
				if (field != null)
					for (String s[] : field) {
						if (s[0].equals(indexField.getCode())) {
							hasfield = true;
							if (s[1].equals("true"))
								cansummary = true;
						}
					}
				if (!hasfield)
					continue;// 如果用户输入没有这个字段，则继续判断下一个字段
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
					if (cansummary)
						summaryvalues.append(ZfilterSome.doAll(searchResult
								.getDetails().getValue(indexField.getCode()))
								+ " ");
				}
			}

			long l2=System.currentTimeMillis();
			if (hascontent == 1
					&& webServiceSearchResult.getDocSource().equals("file")) {
				webServiceSearchResult.getIndexMap()[webServiceSearchResult
						.getIndexMap().length - 1] = new XDIndexValue(
						"content", ZfilterSome.doAll(searchResult.getContent()));
				if (sumContent) {
					summaryvalues.setLength(0);
					summaryvalues.append(ZfilterSome.doAll(searchResult
							.getContent()));
				}
			}
			// webServiceSearchResult.setSummaries(s.getSummary(summaryvalues.toString(),q).toString());
			webServiceSearchResult.setSummaries(Summarizer.getsummary(
					"+content:" + strfilter(keyword), summaryvalues.toString(),
					SearchContext.getAnalyzer(1)));
			
			webServiceResult.add(webServiceSearchResult);
		}
		return webServiceResult;
	}
	
	@WebResult(name = "search")
	public List<WebServiceSearchResult> search(String summary_key,
			UserQuery[] userquery, String[][] field, int start,
			int hitsPerPage, String[][] sortReg, String dedupField)
			throws Exception {
		if (!CheckIPUtil.checkIpFacadeWebService()) {
			return null;
		}
		String queryString = "";
		try {
			UserQueryConvert uc = new UserQueryConvert();
			queryString = uc.ConvertUserQueryToString(userquery);// 复杂表达式转换
		} catch (Exception e) {
			return null;
		}
		if (queryString == null || queryString.trim().equals(""))
			return null;

		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();

		String sortfiled = "";
		try {
			for (String[] s : sortReg) {
				for (IndexField fd : SearchContext.getIndexFieldSet()) {
					if (s[0].equals(fd.getCode()) && fd.isToken() == false
							&& !s[1].equals("score")) {
						sortfiled += s[0] + ":" + s[1] + ",";
					}
				}
				if (isSystemSortfield(s[0], cansort) && !s[1].equals("score")) {
					sortfiled += s[0] + ":" + s[1] + ",";
					continue;
				}
				if (s[1].equals("score")) {
					sortfiled += s[0] + ":" + s[1] + ",";
					continue;
				}
			}
		} catch (Exception e) {
			sortfiled = "";
		}
		if (sortfiled.equals(""))
			sortfiled = null;
		Query query = new org.apache.nutch.searcher.Query();

		query.setXdht(queryString);

		if (dedupField == null || (dedupField != null && dedupField.equals("")))
			dedupField = "docNo";
		searchResultList = searchImpl
				.search(nutchBean, summary_key, query, start > 0 ? start : 0,
						hitsPerPage > 0 ? hitsPerPage
								: SearchImpl.HITS_PER_PAGE, sortfiled, true,
						dedupField);
		/*
		 * 把搜索记录记入数据库中
		 */
		String userName = null;
		// if(user!=null){
		// userName=user.getName();
		// }
		SearchReportExe sre = new SearchReportExe(summary_key, queryString,
				searchResultList.size(), userName, 0, SearchContext
						.getDataHandler());
		sre.saveRecord();
		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			// webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			int num = 0;
			int hascontent = 0;// 是否包括包括content
			boolean sumContent = false;// content是否摘要
			if (field != null) {
				for (String s[] : field) {
					if ("content".equals(s[0])
							&& "file".equals(webServiceSearchResult
									.getDocSource())) {
						hascontent = 1;
						if (s[1].equals("true"))
							sumContent = true;
					}
				}
			} else
				hascontent = 0;
			for (IndexField fd : SearchContext.getIndexFieldSet()) {
				if (field == null) {
					num = SearchContext.getIndexFieldSet().size();
					break;
				}
				for (String s[] : field) {
					if (s[0].equals(fd.getCode()))
						num++;
				}
			}// num记录用户输入的去除索引字段和实际索引字段重复的个数

			webServiceSearchResult.setIndexMap(new XDIndexValue[hascontent
					+ num]);
			int i = 0;
			StringBuffer summaryvalues = new StringBuffer();
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (field == null)
					break;
				boolean cansummary = false;
				boolean hasfield = false;
				if (field != null)
					for (String s[] : field) {
						if (s[0].equals(indexField.getCode())) {
							hasfield = true;
							if (s[1].equals("true"))
								cansummary = true;
						}
					}
				if (!hasfield)
					continue;// 如果用户输入没有这个字段，则继续判断下一个字段
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
					if (cansummary)
						summaryvalues.append(ZfilterSome.doAll(searchResult
								.getDetails().getValue(indexField.getCode()))
								+ " ");
				}
			}
			// Summarizer s=new Summarizer();
			// Query q = new org.apache.nutch.searcher.Query();
			// Token[] t = simple_getTokens(summary_key);
			// for (Token token : t) {
			// q.addRequiredTerm(token.termText());
			// }

			if (hascontent == 1
					&& webServiceSearchResult.getDocSource().equals("file")) {
				webServiceSearchResult.getIndexMap()[webServiceSearchResult
						.getIndexMap().length - 1] = new XDIndexValue(
						"content", ZfilterSome.doAll(searchResult.getContent()));
				if (sumContent) {
					summaryvalues.setLength(0);
					summaryvalues.append(ZfilterSome.doAll(searchResult
							.getContent()));
				}
			}
			// webServiceSearchResult.setSummaries(s.getSummary(summaryvalues.toString(),q).toString());
			webServiceSearchResult.setSummaries(Summarizer.getsummary(
					"+content:" + strfilter(summary_key), summaryvalues
							.toString(), SearchContext.getAnalyzer(1)));
			webServiceResult.add(webServiceSearchResult);
		}

		return webServiceResult;
	}

	@WebResult(name = "search")
	public List<WebServiceSearchResult> search(String key, String[] field,
			XDClause[] xdClauses, int start, int hitsPerPage,
			String[][] sortReg, String dedupField) throws Exception {

		boolean hasdedupField = false;
		for (IndexField fd : SearchContext.getIndexFieldSet()) {
			if (dedupField != null && dedupField.equals(fd.getCode())) {
				hasdedupField = true;
			}
		}
		if (hasdedupField == false)
			dedupField = null;

		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		Query query = new org.apache.nutch.searcher.Query();
		if (xdClauses != null) {
			for (XDClause xdClause : xdClauses) {

				if (xdClause.isRange()) {
					query.addRangeTerm(xdClause.getBeginRange(), xdClause
							.getEndRange(), xdClause.getField(), xdClause
							.isIncludeRange(), xdClause.isRequired(), xdClause
							.isProhibited());
				} else {
					if (xdClause.isRequired() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addRequiredTerm(token.termText(), xdClause
									.getField());
						}

					} else if (xdClause.isRequired() && xdClause.isBphrase()
							&& xdClause.getPhrase() != null)
						query.addRequiredPhrase(xdClause.getPhrase(), xdClause
								.getField());
					else if (xdClause.isProhibited() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addProhibitedTerm(token.termText(), xdClause
									.getField());
						}
					} else if (xdClause.isProhibited() && xdClause.isBphrase()
							&& xdClause.getPhrase() != null)
						query.addProhibitedPhrase(xdClause.getPhrase(),
								xdClause.getField());
				}
			}
		}

		String sortfiled = "";
		try {
			for (String[] s : sortReg) {

				for (IndexField fd : SearchContext.getIndexFieldSet()) {
					if (s[0].equals(fd.getCode()) && fd.isToken() == false
							&& !s[1].equals("score")) {
						sortfiled += s[0] + ":" + s[1] + ",";
					}
				}
				if (isSystemSortfield(s[0], cansort) && !s[1].equals("score")) {
					sortfiled += s[0] + ":" + s[1] + ",";
					continue;
				}
				if (s[1].equals("score")) {
					sortfiled += s[0] + ":" + s[1] + ",";
					continue;
				}
			}
		} catch (Exception e) {
			sortfiled = "";
		}
		if (sortfiled.equals(""))
			sortfiled = null;
		if (dedupField == null || (dedupField != null && dedupField.equals("")))
			dedupField = "docNo";
		// if(keyword!=null&&!keyword.equals(""))
		// {
		// Token[] tokens = getTokens(keyword);
		// for (Token token : tokens)
		// {
		// query.addRequiredTerm(token.termText(),
		// "content");
		// }
		// }
		query.setXdht(key);
		searchResultList = searchImpl
				.search(nutchBean, key, query, start > 0 ? start : 0,
						hitsPerPage > 0 ? hitsPerPage
								: SearchImpl.HITS_PER_PAGE, sortfiled, true,
						dedupField);
		System.out.println("key:" + key);
		System.out.println("searchResultList:" + searchResultList.size());

		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult
					.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			int num = 0;
			for (IndexField fd : SearchContext.getIndexFieldSet()) {
				if (field == null)
					break;
				for (String s : field) {
					if (s.equals(fd.getCode()))
						num++;
				}
			}// num记录用户输入的去除索引字段和实际索引字段重复的个数
			webServiceSearchResult.setIndexMap(new XDIndexValue[1 + num]);
			int i = 0;
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				boolean hasfield = false;
				if (!hasfield && field != null)
					continue;// 如果用户输入没有这个字段，则继续判断下一个字段
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
				}
			}
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 1] = new XDIndexValue("docType",
					searchResult.getDetails().getValue("docType"));
			// webServiceSearchResult.getIndexMap()[webServiceSearchResult.
			// getIndexMap().length-1] = new XDIndexValue("content",
			// ZfilterSome.doAll(searchResult.getContent()));
			webServiceResult.add(webServiceSearchResult);
		}

		return webServiceResult;
	}

	@WebResult(name = "getWords")
	public String[] getWords(String str) throws Exception {
		String[] word = null;
		Token[] t = simple_getTokens(str);
		word = new String[t.length];
		int i = 0;
		for (Token token : t) {
			word[i] = token.termText();
			i++;
		}
		return word;
	}

	@WebResult(name = "search")
	public WebServiceSearchResult search(String No, int idx) throws Exception {
		NutchBean nutchBean = NutchBean.getBean();
		WebServiceSearchResult webServiceSearchResult = null;
		Hit hit = null;
		// 'id' is hit.indexDocNo
		// 'idx' is hit.indexNo

		long docNo = Long.parseLong(No, 16);
		hit = new Hit(idx, (int) docNo);

		HitDetails details = nutchBean.getDetails(hit);
		webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
		webServiceSearchResult.setLength(1);
		webServiceSearchResult.setDocNo(docNo);
		webServiceSearchResult.setInx(idx);
		webServiceSearchResult.setTitle(ZfilterSome.doAll(details
				.getValue("title")));
		webServiceSearchResult.setUrl(details.getValue("url"));
		webServiceSearchResult.setTotal(1);
		webServiceSearchResult.setCreateDate(details.getValue("createDate"));
		webServiceSearchResult.setUpdataDate(details.getValue("updataDate"));
		webServiceSearchResult.setSite(details.getValue("site"));
		webServiceSearchResult.setType(details.getValue("type"));
		webServiceSearchResult.setSubType(details.getValue("subType"));
		webServiceSearchResult.setDocType(details.getValue("docType"));
		webServiceSearchResult.setDataSource(details.getValue("dataSource"));
		webServiceSearchResult.setDocSource(details.getValue("docSource"));

		webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
				.getIndexFieldSet().size() + 1]);
		int i = 0;
		for (IndexField indexField : SearchContext.getIndexFieldSet()) {
			if (details.getValue(indexField.getCode()) != null) {
				XDIndexValue xdIndexValue = new XDIndexValue(indexField
						.getCode(), ZfilterSome.doAll(details
						.getValue(indexField.getCode())));
				webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
			}
		}
		webServiceSearchResult.getIndexMap()[webServiceSearchResult
				.getIndexMap().length - 1] = new XDIndexValue("docType",
				details.getValue("docType"));

		return webServiceSearchResult;
	}

	@WebResult(name = "search_1")
	public List search_1(String keyword, int start_index, int page_size,
			String sortField, boolean reverse, String dedupField)
			throws Exception {
		if (keyword == null || keyword.trim().equals(""))
			return null;
		List<com.xx.platform.core.service.WebServiceSearchResult> webServiceList = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		if (sortField != null) {
			searchResultList = searchImpl.search(nutchBean, keyword,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE,
					sortField, reverse, dedupField);
		} else {
			searchResultList = searchImpl.search(nutchBean, keyword,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE);

		}

		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult
					.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
					.getIndexFieldSet().size() + 2]);
			int i = 0;
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
				}
			}

			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 2] = new XDIndexValue("docType",
					searchResult.getDetails().getValue("docType"));
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 1] = new XDIndexValue("content",
					searchResult.getContent());
			webServiceList.add(webServiceSearchResult);
		}
		return webServiceList;
	}

	private XDClause[] convert(XDClauseDotNET[] xdClauseDotNets) {
		XDClause[] xdClauses = null;
		if (xdClauseDotNets != null) {
			xdClauses = new XDClause[xdClauseDotNets.length];
			for (int i = 0; i < xdClauseDotNets.length; i++) {
				xdClauses[i] = xdClauseDotNets[i] != null ? xdClauseDotNets[i]
						.toXDClause() : null;
			}
		}
		return xdClauses;
	}

	@WebResult(name = "search_2")
	public List search_2(String keyword, XDClause[] xdClauses, int start_index,
			int page_size, String sortField, boolean reverse, String dedupField)
			throws Exception {
		if (keyword == null)
			return null;
		// XDClause[] xdClauses = convert(xdClausesDotNet);
		List<com.xx.platform.core.service.WebServiceSearchResult> webServiceList = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		Query query = null;
		if (SearchImpl.isDefaultParse != null
				&& SearchImpl.isDefaultParse.equals("xdht")) {
			query = new org.apache.nutch.searcher.Query(); // .parse(queryString);
			query.setXdht(keyword);
		} else {
			query = org.apache.nutch.searcher.Query.parse(keyword); // .parse(queryString);
		}

		if (xdClauses != null) {
			for (XDClause xdClause : xdClauses) {
				if (!isIndexField(xdClause.getField())) {
					xdClause.setField("DEFAULT");
				}
				if (xdClause.isRange()) {
					query.addRangeTerm(xdClause.getBeginRange(), xdClause
							.getEndRange(), xdClause.getField(), xdClause
							.isIncludeRange(), xdClause.isRequired(), xdClause
							.isProhibited());
				} else {
					if (xdClause.isRequired() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addRequiredTerm(token.termText(), xdClause
									.getField());
						}

					} else if (xdClause.isRequired() && xdClause.isBphrase())
						query.addRequiredPhrase(xdClause.getPhrase(), xdClause
								.getField());
					else if (xdClause.isProhibited() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addProhibitedTerm(token.termText(), xdClause
									.getField());
						}

					} else if (xdClause.isProhibited() && xdClause.isBphrase())
						query.addProhibitedPhrase(xdClause.getPhrase(),
								xdClause.getField());
				}
			}
		}
		// System.out.println("dedupField:"+dedupField==null?"null":dedupField);
		if (sortField != null) {
			// System.out.println(1);
			searchResultList = searchImpl.search(nutchBean, keyword, query,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE,
					sortField, reverse, dedupField);
		} else {
			// System.out.println(2);
			searchResultList = searchImpl.search(nutchBean, keyword, query,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE);

		}
		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult
					.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
					.getIndexFieldSet().size() + 2]);
			int i = 0;
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
				}
			}
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 2] = new XDIndexValue("docType",
					searchResult.getDetails().getValue("docType"));
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 1] = new XDIndexValue("content",
					searchResult.getContent());
			webServiceList.add(webServiceSearchResult);
		}
		return webServiceList;
	}

	@WebResult(name = "search_2_dotnet")
	public List search_2_dotNET(String keyword,
			XDClauseDotNET[] xdClausesDotNet, int start_index, int page_size,
			String sortField, boolean reverse, String dedupField)
			throws Exception {
		if (keyword == null)
			return null;
		XDClause[] xdClauses = convert(xdClausesDotNet);
		List<com.xx.platform.core.service.WebServiceSearchResult> webServiceList = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();
		Query query = null;
		if (SearchImpl.isDefaultParse != null
				&& SearchImpl.isDefaultParse.equals("xdht")) {
			query = new org.apache.nutch.searcher.Query(); // .parse(queryString);
			query.setXdht(keyword);
		} else {
			query = org.apache.nutch.searcher.Query.parse(keyword); // .parse(queryString);
		}

		if (xdClauses != null) {

			for (XDClause xdClause : xdClauses) {
				if (!isIndexField(xdClause.getField())) {
					xdClause.setField("DEFAULT");
				}
				if (xdClause.isRange()) {
					query.addRangeTerm(xdClause.getBeginRange(), xdClause
							.getEndRange(), xdClause.getField(), xdClause
							.isIncludeRange(), xdClause.isRequired(), xdClause
							.isProhibited());
				} else {
					if (xdClause.isRequired() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addRequiredTerm(token.termText(), xdClause
									.getField());
						}

					} else if (xdClause.isRequired() && xdClause.isBphrase())
						query.addRequiredPhrase(xdClause.getPhrase(), xdClause
								.getField());
					else if (xdClause.isProhibited() && !xdClause.isBphrase()) {
						Token[] tokens = lower_getTokens(xdClause.getKeyword());
						for (Token token : tokens) {
							query.addProhibitedTerm(token.termText(), xdClause
									.getField());
						}

					} else if (xdClause.isProhibited() && xdClause.isBphrase())
						query.addProhibitedPhrase(xdClause.getPhrase(),
								xdClause.getField());
				}
			}
		}
		// System.out.println("dedupField:"+dedupField==null?"null":dedupField);
		if (sortField != null) {
			// System.out.println(1);
			searchResultList = searchImpl.search(nutchBean, keyword, query,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE,
					sortField, reverse, dedupField);
		} else {
			// System.out.println(2);
			searchResultList = searchImpl.search(nutchBean, keyword, query,
					start_index > 0 ? start_index : 0,
					page_size > 0 ? page_size : SearchImpl.HITS_PER_PAGE);

		}
		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult
					.getTitle()));
			webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult
					.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList)
					.getTotal());
			webServiceSearchResult.setTime(String
					.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails()
					.getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails()
					.getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue(
					"site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue(
					"type"));
			webServiceSearchResult.setSubType(searchResult.getDetails()
					.getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails()
					.getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails()
					.getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails()
					.getValue("docSource"));

			webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
					.getIndexFieldSet().size() + 2]);
			int i = 0;
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField
							.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
				}

			}
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 2] = new XDIndexValue("docType",
					searchResult.getDetails().getValue("docType"));
			webServiceSearchResult.getIndexMap()[webServiceSearchResult
					.getIndexMap().length - 1] = new XDIndexValue("content",
					searchResult.getContent());
			webServiceList.add(webServiceSearchResult);
		}
		return webServiceList;
	}

	@WebResult(name = "search_3")
	public WebServiceSearchResult search_3(long docNo, int idx)
			throws Exception {
		NutchBean nutchBean = NutchBean.getBean();
		WebServiceSearchResult webServiceSearchResult = null;
		Hit hit = null;
		// 'id' is hit.indexDocNo
		// 'idx' is hit.indexNo
		hit = new Hit(idx, (int) docNo);

		HitDetails details = nutchBean.getDetails(hit);
		webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
		webServiceSearchResult.setLength(1);
		webServiceSearchResult.setDocNo(docNo);
		webServiceSearchResult.setInx(idx);
		webServiceSearchResult.setTitle(ZfilterSome.doAll(details
				.getValue("title")));
		webServiceSearchResult.setUrl(details.getValue("url"));
		webServiceSearchResult.setTotal(1);
		webServiceSearchResult.setCreateDate(details.getValue("createDate"));
		webServiceSearchResult.setUpdataDate(details.getValue("updataDate"));
		webServiceSearchResult.setSite(details.getValue("site"));
		webServiceSearchResult.setType(details.getValue("type"));
		webServiceSearchResult.setSubType(details.getValue("subType"));
		webServiceSearchResult.setDocType(details.getValue("docType"));
		webServiceSearchResult.setDataSource(details.getValue("dataSource"));
		webServiceSearchResult.setDocSource(details.getValue("docSource"));

		webServiceSearchResult.setIndexMap(new XDIndexValue[SearchContext
				.getIndexFieldSet().size() + 1]);
		int i = 0;
		for (IndexField indexField : SearchContext.getIndexFieldSet()) {
			if (details.getValue(indexField.getCode()) != null) {
				XDIndexValue xdIndexValue = new XDIndexValue(indexField
						.getCode(), ZfilterSome.doAll(details
						.getValue(indexField.getCode())));
				webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
			}
		}
		webServiceSearchResult.getIndexMap()[webServiceSearchResult
				.getIndexMap().length - 1] = new XDIndexValue("docType",
				details.getValue("docType"));

		return webServiceSearchResult;
	}

	private Token[] lower_getTokens(String text) throws IOException {
		if (text == null || text.trim().equals("")) {
			return new Token[] { new Token("", 0, 0) };
		}
		ArrayList result = new ArrayList();
		TokenStream ts = ANALYZER
				.tokenStream("content", new StringReader(text));
		for (Token token = ts.next(); token != null; token = ts.next()) {
			result.add(token);
		}
		return (Token[]) result.toArray(new Token[result.size()]);
	}

	private Token[] simple_getTokens(String text) throws IOException {
		if (text == null || text.trim().equals("")) {
			return new Token[] { new Token("", 0, 0) };
		}
		ArrayList result = new ArrayList();
		TokenStream ts = XDANALYZER.tokenStream("content", new StringReader(
				text));
		for (Token token = ts.next(); token != null; token = ts.next()) {
			result.add(token);
		}
		return (Token[]) result.toArray(new Token[result.size()]);
	}

	private boolean isSystemSortfield(String field, String str) {
		String sort[] = str.split(",");
		for (String s : sort) {
			if (s.equals(field))
				return true;
		}
		return false;
	}

	public boolean isIndexField(String fieldName) throws Exception {
		if (fieldName.equals("title") || fieldName.equals("docType"))
			return true;
		boolean isField = false;
		String[] fieldsR = fieldName.split(",");
		String[] _field = null;
		for (String f : fieldsR) {
			isField = false;
			_field = f.split(":");
			for (IndexField indexField : SearchContext.getIndexFieldSet()) {
				if (indexField.getCode().equals(_field[0])) {
					isField = true;
					break;
				}
			}
			if (!isField)
				break;
		}
		return isField;
	}

	private String clearSpecChar(String chars) {
		if (chars == null)
			return "";
		StringBuffer content = new StringBuffer(chars);
		for (int i = 0; content != null && i < content.length(); i++) {
			if ((int) content.charAt(i) == 7)
				content.setCharAt(i, ' ');
		}
		return content.toString();
	}

}
