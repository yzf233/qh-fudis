package com.xx.platform.web.actions.search;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.analysis.XdNutchDocumentAnalyzer;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Summarizer;
import org.apache.nutch.util.NutchConf;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.PYContext;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.search.SearchImpl;
import com.xx.platform.core.search.SearchList;
import com.xx.platform.core.service.UserQuery;
import com.xx.platform.core.service.UserQueryConvert;
import com.xx.platform.core.service.WebServiceSearchResult;
import com.xx.platform.core.service.XDIndexValue;
import com.xx.platform.core.service.ZfilterSome;
import com.xx.platform.domain.model.crawl.Synonymy;
import com.xx.platform.domain.model.search.Guide;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.domain.model.system.Relation;
import com.xx.platform.domain.model.system.Sproject;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.SessionAware;
import com.xx.platform.web.actions.system.ProjectFileManager;

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
public class SearchAction extends BaseAction {
	public static final int HITS_PER_PAGE = 10;
	private SearchResult searchResult;
	private SearchBean searchBean;
	private List resultList;
	private List indexList = new ArrayList(SearchContext.getIndexFieldSet());
	private SearchImpl search;
	private String fenye; // ��ҳ
	private int pagesize = HITS_PER_PAGE;
	private int page;
	private int pages[];
	private String message;
	private String cansort = "boost,docNo,docSource,url,dataSource,docType,segment,contentLength,createDate,updataDate,site,type,subType";
	private Map smap = SearchContext.getSynonymyMap(); // ͬ���
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private String queryKeyWord;
	private String isInput;// ��ʾ��ѯ�����Ƿ��Ǵ�������������
	private String defualtUrl = "/search/result.jsp";
	private String projectCode;
	private String p;// ��Ŀ����
	private List<Sproject> publishProject;// �Ѿ���������Ŀ
	private String isTest;// ���ڱ�ʾ���������Ƿ����Բ���ҳ
	private String guideId;// ������ID
	private String projectId;// ��Ŀ��ID
	private String q;// ��ѯ�ؼ���
	private List<String> suggestList;

	public String toSearch() throws Exception {
		publishProject = SearchContext.projectList;
		if (publishProject == null) {
			publishProject = new ArrayList<Sproject>();
		}
		List<Sproject> temp = new ArrayList<Sproject>();
		for (Sproject project : publishProject) {
			long state = project.getState();
			String code = project.getCode();
			if (state != 1l || "default".equals(code)) {
				temp.add(project);
			}
		}

		if (!temp.isEmpty()) {
			publishProject.removeAll(temp);
		}
		return Action.SUCCESS;
	}

	/**
	 * ��ѯ��ҳ����ҳ��
	 * 
	 * @return
	 */
	public String pindextest() {
		isTest = "true";
		StringBuilder sbPath = new StringBuilder();
		sbPath.append("/search/").append(p).append(ProjectFileManager.testString).append("/page/index.jsp");
		defualtUrl = sbPath.toString();
		return Action.SUCCESS;
	}

	public String psearch() throws Exception {
		List<Sproject> sprojectList = SearchContext.projectList;
		Sproject project = null;
		if (sprojectList != null) {
			for (Sproject tempproject : sprojectList) {
				if (tempproject.getCode().equals(p)) {
					project = tempproject;
					break;
				}
			}
		}
		if (project != null) {
			// �Ƿ���Ҫ��¼
			if (project.getVerify() == 1) {
				HttpSession session = request.getSession();
				ProjectUser user = (ProjectUser) session.getAttribute(SessionAware.PROJECT_LOGIN_ID);
				if (user != null && user.getId() != null) {
					int n = service.getCountByCriteria(DetachedCriteria.forClass(Relation.class).add(
							Restrictions.and(Restrictions.eq("userid", user.getId()), Restrictions.eq("projectid",
									project.getId()))));
					if (n > 0) {
						psearchExe();
					}
				} else {
					StringBuilder sbPath = new StringBuilder();
					sbPath.append("/search/").append(p).append("/page/login.jsp");
					defualtUrl = sbPath.toString();
					return "projectlogin";
				}
			} else {
				// ����Ҫ��¼
				psearchExe();
			}
		}
		return Action.SUCCESS;
	}

	public void psearchExe() {
		if (p != null) {
			StringBuilder sbPath = new StringBuilder();
			sbPath.append("/search/").append(p).append("/page/index.jsp");
			defualtUrl = sbPath.toString();
		} else {
			defualtUrl = "/index.jsp";
		}
		publishProject = SearchContext.projectList;
		if (publishProject == null) {
			publishProject = new ArrayList<Sproject>();
		}
	}

	public String search() throws Exception {
		if (projectCode != null && projectCode.length() > 0) {
			StringBuilder sbPath = new StringBuilder();
			if (isTest == null || isTest.trim().length() == 0 || "false".equals(isTest)) {
				sbPath.append("/search/").append(projectCode).append("/page/result.jsp");
			} else {
				sbPath.append("/search/").append(projectCode).append(ProjectFileManager.testString).append(
						"/page/result.jsp");
			}
			defualtUrl = sbPath.toString();
		}
		String returnValue = null;
		if (NutchCommand.getSearch()) {
			NutchBean bean = null;
			HttpSession session = request.getSession();
			try {
				bean = NutchBean.get(session.getServletContext());
			} catch (IOException e) {
				message = "�����Ƿ��нڵ����ӣ�";
				return Action.SUCCESS;
			} catch (Exception e) {
				return Action.SUCCESS;
			}
			search = new SearchImpl();
			int start = 0; // first hit to display
			String startString = request.getParameter("s");
			pagesize = request.getParameter("ps") != null && request.getParameter("ps").matches("[\\d]{1,}") ? Integer
					.parseInt(request.getParameter("ps")) : HITS_PER_PAGE;

			// ���xml��ʾ��ʽ����ʾ��ʽ��������url�����һ��rs�Ĳ�������rs=xmlʱ��ʾxml���
			returnValue = request.getParameter("rs");

			if (startString != null)
				start = Integer.parseInt(startString);
			else
				start = 0;

			// String queryString = request.getParameter("q");
			String queryString = q;
			queryKeyWord = queryString;
			String sr = request.getParameter("sr");
			String df = request.getParameter("df");
			String ty = request.getParameter("ty");
			String squery = request.getParameter("squery");// ������������������������ƶ�ĳЩ�ֶ��������ֶ����ֶΰ��ն��ŷֿ�-����
			String[] squerys = null;
			if (squery != null)
				squerys = squery.split(",");

			if (ty == null || ty.equals("")) {
				ty = "database";
				request.setAttribute("ty", "database");
			}
			if (df == null) {
				df = "docNo";
			}
			if (queryString == null || queryString.trim().equals("")) {
				return Action.INPUT;
			}

			Query query = new org.apache.nutch.searcher.Query();

			QueryParser queryParse = new QueryParser("content", SearchContext.getAnalyzer(1));
			query.setXdht(queryString);
			if (ty.equals("file"))
				query.addRequiredTerm("file", "docSource");
			else if (ty.equals("database"))
				query.addRequiredTerm("database", "docSource");
			try {
				resultList = search.search(bean, queryString, query, start, pagesize, null, true, df);
				User user = (User) session.getAttribute(SessionAware.SESSION_LOGIN_ID);
				/*
				 * ��������¼�������ݿ���
				 */
				String userName = null;
				if (user != null) {
					userName = user.getName();
				}
				if (isInput != null && (isTest == null || isTest.trim().length() == 0 || "false".equals(isTest))) {
					SearchReportExe sre = new SearchReportExe(queryString, "", resultList.size(), userName, 1, service);
					sre.saveRecord();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return Action.SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
				return Action.SUCCESS;
			}
			List rsl = new ArrayList();
			List<IndexField> indexFieldList = SearchContext.getIndexFieldList();// ȡ���û����õ����������ֶ�
			StringBuffer contentStrb = new StringBuffer();
			StringBuffer summaryStrb = new StringBuffer();
			for (int i = 0; resultList != null && i < resultList.size(); i++) {
				if (ty.equals("database")) {
					SearchResult searchResult = (SearchResult) resultList.get(i);
					List indexMessage = new ArrayList();

					summaryStrb.setLength(0);
					if (indexFieldList != null && indexFieldList.size() > 0)
						for (IndexField f : indexFieldList) {
							contentStrb.setLength(0);
							contentStrb.append(f.getCode()).append("��");// ȡ������
							String str = searchResult.getDetails().getValue(f.getCode());
							// if(f.getCode().equals("patentabstract"))
							summaryStrb.append(str).append(" ");
							if (str != null && str.trim().length() > 50)// ����50����
								str = str.trim().substring(0, 49) + "...";
							contentStrb.append(str);// ȡ��������
							indexMessage.add(contentStrb.toString());
						}
					String str = Summarizer.getsummary(queryString, summaryFilter(summaryStrb.toString()),
							SearchContext.getAnalyzer(1));
					summaryStrb.setLength(0);
					summaryStrb.append("&nbsp;&nbsp;&nbsp;").append(str).append("...");
					searchResult.setSummaries(summaryStrb.toString().replaceAll("amp;", "").replaceAll("&amp;", "")
							.replaceAll("#160;", ""));
					searchResult.setUstr(indexMessage);
				} else// ������ļ�
				{
					SearchResult searchResult = (SearchResult) resultList.get(i);
					summaryStrb.setLength(0);
					String content = searchResult.getDetails().getValue("content");
					summaryStrb.append(content);
					
					String docType = searchResult.getDetails().getValue("docType");
					if (docType != null && docType.equals("eml")) {
						String attaContent = searchResult.getDetails().getValue("attaContent");
						if (attaContent != null && attaContent.trim().length() > 0) {
							summaryStrb.append(attaContent);
						}
					}
					
					String str = Summarizer.getsummary(queryString, summaryFilter(summaryStrb.toString()),
							SearchContext.getAnalyzer(1));
					summaryStrb.setLength(0);
					summaryStrb.append("&nbsp;&nbsp;&nbsp;").append(str).append("...");
					searchResult.setSummaries(summaryStrb.toString().replaceAll("amp;", "").replaceAll("&amp;", "")
							.replaceAll("#160;", ""));
				}
			}

			String docSource = request.getParameter("ty"); // ���
			((SearchList) resultList).setQueryString(queryString);
			searchBean = ((SearchList) resultList).getSearchBean();
			page = start * HITS_PER_PAGE + 1;

			// ��ҳ���� qh 2005-05-25 �޸�
			PageChoseUtil pageChose = new PageChoseUtil(start, (int) ((SearchList) resultList).getTotal(), pagesize,
					queryString, docSource);
			String contextPath = request.getContextPath();
			// System.out.println("..........."+contextPath);
			fenye = pageChose.pageChoseStr(contextPath);

		} else {
			return DISABLE_SEARCH;
		}
		request.setAttribute("resultList", this.resultList);
		request.setAttribute("indexList", this.indexList);
		return returnValue != null && returnValue.equals("xml") ? "xml" : Action.SUCCESS;
		// return Action.SUCCESS;
	}

	/**
	 * �����ݰ������Ҽ����ŵĽ��й���
	 * 
	 * @param summaryStrb
	 */
	public String summaryFilter(String summaryStrb) {
		summaryStrb = summaryStrb.replace("<", "&lt;");
		summaryStrb = summaryStrb.replace(">", "&gt;");
		summaryStrb = summaryStrb.replace("\"", "&lt;");
		return summaryStrb;
	}

	public String getword(String queryString) throws Exception {
		Token[] ts = getTokens(queryString);
		String str = "";
		for (Token t : ts) { // ȡͬ��ʷ�������ѯ����
			String termtext = t.termText() == null ? "" : t.termText().toLowerCase();
			if (!"".equals(termtext))
				str += termtext + " ";
		}
		return str;
	}

	/**
	 * �߼�����
	 * 
	 * qh 2007-05-25
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String advance() throws Exception // ����content�ֶα�ȥ������������������ڲ���ʵ��-����
	{
		if (NutchCommand.getSearch()) {
			NutchBean bean = NutchBean.get(request.getSession().getServletContext());
			search = new SearchImpl();
			int start = 0; // first hit to display
			String startString = request.getParameter("s");
			pagesize = request.getParameter("ps") != null && request.getParameter("ps").matches("[\\d]{1,}") ? Integer
					.parseInt(request.getParameter("ps")) : HITS_PER_PAGE;

			if (startString != null)
				start = Integer.parseInt(startString);
			else
				start = 0;

			String q1 = request.getParameter("q");// ����ȫ��
			String q2 = request.getParameter("q2");// ��������
			String q3 = request.getParameter("q3");// ����ȥ��
			String ty = request.getParameter("ty");
			String df = request.getParameter("df");

			if (q1 == null || q1.trim().equals("")) {
				return Action.INPUT;
			}
			if (ty == null || ty.equals("")) {
				ty = "database";
				request.setAttribute("ty", "database");
			}

			String qword = getword(q1);
			q2 = getword(q2);
			q3 = getword(q3);

			String[] s1 = qword.split(" ");// ����ȫ��
			String[] s2 = q2.split(" ");// ��������
			String[] s3 = q3.split(" ");// ����ȥ��

			Query query = new org.apache.nutch.searcher.Query();
			UserQuery u = new UserQuery();

			Map<String, String[]> QueryMap = new HashMap();
			Map<String, String[]> QueryMap2 = new HashMap();
			Map<String, String[]> QueryMap3 = new HashMap();

			for (IndexField fd : SearchContext.getIndexFieldSet()) {
				for (String s : s1)// ����ȫ���������ؼ���
				{
					if (s.replace(" ", "").equals(""))
						continue;
					QueryMap.put("����ȫ��" + String.valueOf(QueryMap.size()), new String[] { fd.getCode(), s, "false" });
				}
				for (String s : s2)// �������⣬�����ؼ���
				{
					if (s.replace(" ", "").equals(""))
						continue;
					QueryMap2.put("��������" + String.valueOf(QueryMap.size()), new String[] { fd.getCode(), s, "false" });
				}
				for (String s : s3)// ����ȥ���������ؼ���
				{
					if (s.replace(" ", "").equals(""))
						continue;
					QueryMap3.put("����ȥ��" + String.valueOf(QueryMap.size()), new String[] { fd.getCode(), s, "false" });
				}
			}

			if (ty.equals("file")) {
				QueryMap.put("����ȫ��" + String.valueOf(QueryMap.size()), new String[] { "docSource", "file", "false" });
			} else if (ty.equals("database")) {
				QueryMap.put("����ȫ��" + String.valueOf(QueryMap.size()),
						new String[] { "docSource", "database", "false" });
			}

			String[][] requery = new String[QueryMap.size()][3];
			Iterator it = QueryMap.entrySet().iterator();
			int keynum = 0;
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object value = entry.getValue();
				requery[keynum] = (String[]) value;
				keynum++;
			}
			u.setRequireKey(requery);

			String[][] requery2 = new String[QueryMap2.size()][3];
			Iterator it2 = QueryMap2.entrySet().iterator();
			int keynum2 = 0;
			while (it2.hasNext()) {
				Map.Entry entry = (Map.Entry) it2.next();
				Object value = entry.getValue();
				requery2[keynum2] = (String[]) value;
				keynum2++;
			}
			u.setRequireOneMoreKey(requery2);

			String[][] requery3 = new String[QueryMap3.size()][3];
			Iterator it3 = QueryMap3.entrySet().iterator();
			int keynum3 = 0;
			while (it3.hasNext()) {
				Map.Entry entry = (Map.Entry) it3.next();
				Object value = entry.getValue();
				requery3[keynum3] = (String[]) value;
				keynum3++;
			}
			u.setProhibitKey(requery3);

			String queryString = "";
			UserQueryConvert uc = new UserQueryConvert();
			queryString = uc.ConvertUserQueryToString(new UserQuery[] { u });
			query.addRequiredTerm("", "542B22610658085FBA1C1C709072083B");
			query.setHquery(queryString);

			resultList = search.search(bean, q1, query, start, pagesize, null, true, "docNo");

			String docSource = request.getParameter("ty"); // ���
			((SearchList) resultList).setQueryString(q1);
			searchBean = ((SearchList) resultList).getSearchBean();
			page = start * HITS_PER_PAGE + 1;

			// ��ҳ���� qh 2005-05-25 �޸�
			PageChoseUtil pageChose = new PageChoseUtil(start, (int) ((SearchList) resultList).getTotal(), pagesize,
					q1, docSource);
			String contextPath = request.getContextPath();
			// System.out.println("..........."+contextPath);
			fenye = pageChose.pageChoseStr(contextPath);

		} else {
			return DISABLE_SEARCH;
		}

		request.setAttribute("resultList", this.resultList);
		request.setAttribute("indexList", this.indexList);
		return Action.SUCCESS;
	}

	/**
	 * ��ҳ����
	 * 
	 * qh 2007-05-25
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String getCached() throws Exception {
		defualtUrl = "/search/cached.jsp";
		if (p != null && p.trim().length() > 0) {
			List<Sproject> sprojectList = SearchContext.projectList;
			Sproject project = null;
			if (sprojectList != null) {
				for (Sproject tempproject : sprojectList) {
					if (tempproject.getCode().equals(p)) {
						project = tempproject;
						break;
					}
				}
			}
			if (project != null) {
				StringBuilder sbPath = new StringBuilder();
				if (isTest == null || isTest.trim().length() == 0 || "false".equals(isTest)) {
					sbPath.append("/search/").append(p).append("/page/cached.jsp");
				} else {
					sbPath.append("/search/").append(p).append(ProjectFileManager.testString)
							.append("/page/cached.jsp");
				}
				defualtUrl = sbPath.toString();
			}
		}
		int docId = Integer.valueOf(request.getParameter("id"));
		int idx = Integer.valueOf(request.getParameter("idx"));

		NutchBean bean = NutchBean.getBean();
		Hit hit = new Hit(idx, docId);
		HitDetails details = bean.getDetails(hit);
		StringBuilder sbContent=new StringBuilder();
		String content = details.getValue("content");
		sbContent.append(content);
		String docSource=details.getValue("docSource");
		if(docSource!=null&&"file".equals(docSource)){
			String docType=details.getValue("docType");
			if(docType!=null&&docType.equals("eml")){
				String attaContent=details.getValue("attaContent");
				if(attaContent!=null&&attaContent.trim().length()>0){
					sbContent.append(System.getProperty("line.separator", "\r\n"));
					sbContent.append("����:");
					sbContent.append(System.getProperty("line.separator", "\r\n"));
					sbContent.append(attaContent);
				}
			}
		}
		content=sbContent.toString();
		searchResult = new SearchResult();
		String f = request.getParameter("f");// format ��ֵ��ʱ�򲻴�����
		if (f != null) {
			searchResult.setSummaries(content.length() < 5000 ? content : content.substring(0, 5000));
		} else {
			com.xx.platform.web.actions.search.Summarizer summarizer = new com.xx.platform.web.actions.search.Summarizer();
			searchResult.setSummaries(summarizer.getSummary(content, request.getParameter("q")).toString());
		}
		return Action.SUCCESS;
	}

	/**
	 * �û�ͨ��url��ʽ�����������ظ�xmlҳ�� ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String xmlSearch() throws Exception {

		String queryString = request.getParameter("queryString");// ��ѯ�ַ���
		String summary_key = request.getParameter("summary_key");// ժҪ�ؼ���
		String[][] field = null;
		try {
			String fields = request.getParameter("fields");
			String[] fds = fields.split(";");
			field = new String[fds.length][2];// �����ֶ�
			for (int i = 0; i < fds.length; i++) {
				field[i] = fds[i].split(",");
			}
		} catch (Exception e) {
		}

		String[][] sortReg = null;
		try {
			String sort = request.getParameter("sort");
			String[] st = sort.split(";");
			sortReg = new String[st.length][2];// �������
			for (int i = 0; i < st.length; i++) {
				sortReg[i] = st[i].split(",");
			}
		} catch (Exception e) {
		}
		List<UserQuery> UserQueryList = new ArrayList();// ��userquery0~userquery9��10������

		for (int key = 0; key < 10; key++) {
			// ///�����ѯ����ʼ//////
			UserQuery u = new UserQuery();
			String keyString = String.valueOf(key);
			try {
				String rqk = request.getParameter("rqk" + keyString);
				String[] rqks = rqk.split(";");
				String[][] requireKey = new String[rqks.length][3];
				for (int i = 0; i < rqks.length; i++) {
					requireKey[i] = rqks[i].split(",");
				}
				u.setRequireKey(requireKey);
			} catch (Exception e) {
			}// �����������û�д������

			try {
				String phk = request.getParameter("phk" + keyString);
				String[] phks = phk.split(";");
				String[][] prohibitKey = new String[phks.length][3];
				for (int i = 0; i < phks.length; i++) {
					prohibitKey[i] = phks[i].split(",");
				}
				u.setProhibitKey(prohibitKey);
			} catch (Exception e) {
			}// �����������û�д������

			try {
				String rqonek = request.getParameter("rqonek" + keyString);
				String[] rqoneks = rqonek.split(";");
				String[][] requireOneMoreKey = new String[rqoneks.length][3];
				for (int i = 0; i < rqoneks.length; i++) {
					requireOneMoreKey[i] = rqoneks[i].split(",");
				}
				u.setRequireOneMoreKey(requireOneMoreKey);
			} catch (Exception e) {
			}// �����������û�д������

			try {
				String phonek = request.getParameter("phonek" + keyString);
				String[] phoneks = phonek.split(";");
				String[][] prohibitOneMoreKey = new String[phoneks.length][3];
				for (int i = 0; i < phoneks.length; i++) {
					prohibitOneMoreKey[i] = phoneks[i].split(",");
				}
				u.setProhibitOneMoreKey(prohibitOneMoreKey);
			} catch (Exception e) {
			}// �����������û�д������
			if (u.getRequireKey() != null || u.getProhibitKey() != null || u.getRequireOneMoreKey() != null
					|| u.getProhibitOneMoreKey() != null)
				UserQueryList.add(u);// ������userquery���������ò�ѯ������������ѯ�������
		}

		// ///�����ѯ�������/////
		UserQuery[] UserQuerys = new UserQuery[UserQueryList.size()];
		for (int j = 0; j < UserQueryList.size(); j++) {
			UserQuerys[j] = UserQueryList.get(j);// ȡ��ѯ�����������ѯ������������
		}

		if (queryString == null)
			queryString = "";
		UserQueryConvert uc = new UserQueryConvert();
		queryString = queryString + " " + uc.ConvertUserQueryToString(UserQuerys);// ת���ɲ�ѯ���

		int start = Integer.valueOf(request.getParameter("start"));// ��ʼλ��
		int hitsPerPage = Integer.valueOf(request.getParameter("hitsPerPage"));// ÿҳ��С
		String dedupField = request.getParameter("dedupField");// ���ؽ��ȥ��
		if (queryString == null || queryString.trim().equals(""))
			return null;

		List<WebServiceSearchResult> webServiceResult = new ArrayList();
		List<com.xx.platform.web.actions.search.SearchResult> searchResultList = new ArrayList();
		com.xx.platform.core.service.WebServiceSearchResult webServiceSearchResult;
		NutchBean nutchBean = NutchBean.getBean();
		SearchImpl searchImpl = new SearchImpl();

		String sortfiled = "";
		try {
			for (String[] s : sortReg) {
				for (IndexField fd : SearchContext.getIndexFieldSet()) {
					if (s[0].equals(fd.getCode()) && fd.isToken() == false && !s[1].equals("score")) {
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
		System.out.println(query);
		if (dedupField == null || (dedupField != null && dedupField.equals("")))
			dedupField = "docNo";
		searchResultList = searchImpl.search(nutchBean, summary_key, query, start > 0 ? start : 0,
				hitsPerPage > 0 ? hitsPerPage : SearchImpl.HITS_PER_PAGE, sortfiled, true, dedupField);
		/*
		 * ��������¼�������ݿ���
		 */
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute(SessionAware.SESSION_LOGIN_ID);
		String userName = null;
		if (user != null) {
			userName = user.getName();
		}
		SearchReportExe sre = new SearchReportExe(summary_key, queryString, searchResultList.size(), userName, 2,
				service);
		sre.saveRecord();
		for (com.xx.platform.web.actions.search.SearchResult searchResult : searchResultList) {
			webServiceSearchResult = new com.xx.platform.core.service.WebServiceSearchResult();
			webServiceSearchResult.setLength(searchResult.getLength());
			webServiceSearchResult.setDocNo(searchResult.getDocNo());
			webServiceSearchResult.setInx((searchResult.getHit().getIndexNo()));
			webServiceSearchResult.setTitle(ZfilterSome.doAll(searchResult.getTitle()));
			// webServiceSearchResult.setSummaries(ZfilterSome.doAll(searchResult.getSummaries()));
			webServiceSearchResult.setUrl(searchResult.getUrl());
			webServiceSearchResult.setTotal(((SearchList) searchResultList).getTotal());
			webServiceSearchResult.setTime(String.valueOf(((SearchList) searchResultList).getTime()));
			webServiceSearchResult.setCreateDate(searchResult.getDetails().getValue("createDate"));
			webServiceSearchResult.setUpdataDate(searchResult.getDetails().getValue("updataDate"));
			webServiceSearchResult.setSite(searchResult.getDetails().getValue("site"));
			webServiceSearchResult.setType(searchResult.getDetails().getValue("type"));
			webServiceSearchResult.setSubType(searchResult.getDetails().getValue("subType"));
			webServiceSearchResult.setDocType(searchResult.getDetails().getValue("docType"));
			webServiceSearchResult.setDataSource(searchResult.getDetails().getValue("dataSource"));
			webServiceSearchResult.setDocSource(searchResult.getDetails().getValue("docSource"));

			int num = 0;
			int hascontent = 0;// �Ƿ��������content
			if (field != null) {
				for (String s[] : field) {
					if (s[0].equals("content") && webServiceSearchResult.getDocSource().equals("file"))
						hascontent = 1;
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
			}// num��¼�û������ȥ�������ֶκ�ʵ�������ֶ��ظ��ĸ���

			webServiceSearchResult.setIndexMap(new XDIndexValue[hascontent + num]);
			int i = 0;
			String summaryvalues = "";
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
					continue;// ����û�����û������ֶΣ�������ж���һ���ֶ�
				if (searchResult.getDetails().getValue(indexField.getCode()) != null) {
					XDIndexValue xdIndexValue = new XDIndexValue(indexField.getCode(), ZfilterSome.doAll(searchResult
							.getDetails().getValue(indexField.getCode())));
					webServiceSearchResult.getIndexMap()[i++] = xdIndexValue;
					if (cansummary)
						summaryvalues += ZfilterSome.doAll(searchResult.getDetails().getValue(indexField.getCode()))
								+ " ";
				}
			}
			// Summarizer s = new Summarizer();
			// Query q = new org.apache.nutch.searcher.Query();
			// Token[] t = simple_getTokens(summary_key);
			// for (Token token : t) {
			// q.addRequiredTerm(token.termText());
			// }

			if (hascontent == 1 && webServiceSearchResult.getDocSource().equals("file")) {
				webServiceSearchResult.getIndexMap()[webServiceSearchResult.getIndexMap().length - 1] = new XDIndexValue(
						"content", ZfilterSome.doAll(searchResult.getContent()));
				summaryvalues = ZfilterSome.doAll(searchResult.getContent());
			}
			// webServiceSearchResult.setSummaries(s.getSummary(summaryvalues,
			// q)
			// .toString());
			webServiceSearchResult.setSummaries(Summarizer.getsummary("+content:" + strfilter(summary_key),
					summaryvalues.toString(), SearchContext.getAnalyzer(1)));
			webServiceResult.add(webServiceSearchResult);
		}
		request.setAttribute("result", webServiceResult);

		return Action.SUCCESS;
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

	private static final Analyzer ANALYZER = new NutchDocumentAnalyzer();
	private static final Analyzer XDANALYZER = new XdNutchDocumentAnalyzer();

	private Token[] lower_getTokens(String text) throws IOException {
		if (text == null || text.trim().equals("")) {
			return new Token[] { new Token("", 0, 0) };
		}
		ArrayList result = new ArrayList();
		TokenStream ts = ANALYZER.tokenStream("content", new StringReader(text));
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
		TokenStream ts = XDANALYZER.tokenStream("content", new StringReader(text));
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

	public Map getSynonymyWords() {
		Map synonymyMap = new HashMap();
		String[] a;
		List<Synonymy> synonymyList = SearchContext.getDao().findAllByIObjectCType(Synonymy.class);
		for (Synonymy s : synonymyList) {
			a = s.getWords().split("[,�� ��]");
			if (a != null && a.length > 0) {
				for (String word : a) {
					// һ���ʲ������ڶ���
					synonymyMap.put(word.toLowerCase(), a);
				}
			}
		}
		return synonymyMap;
	}

	private Token[] getTokens(String text) throws IOException {
		ArrayList result = new ArrayList();
		com.xx.platform.core.analyzer.XDChineseTokenizer xdToken = new com.xx.platform.core.analyzer.XDChineseTokenizer(
				new java.io.StringReader(text));
		// TokenStream xdToken = ANALYZER.tokenStream("content", new
		// StringReader(text));
		for (Token token = xdToken.next(); token != null; token = xdToken.next()) {
			result.add(token);
		}
		return (Token[]) result.toArray(new Token[result.size()]);
	}

	public String searchByGuideAjax() throws Exception {
		List<Sproject> projectList = SearchContext.projectList;
		if (projectList == null || projectList.isEmpty()) {
			SearchContext.initProject();
		}
		Sproject guideProject = null;
		for (Sproject project : projectList) {
			if (projectCode.equals(project.getCode())) {
				guideProject = project;
				break;
			}
		}
		if (guideProject == null) {
			// ��Ŀ�Ѿ��ر�
			return "projectSearchError";
		} else {
			Map<String, List<Guide>> map = SearchContext.allGuideMap;
			List<Guide> guides = map.get(projectCode);// �����Ŀ�µĵ���
			if (guides == null || guides.isEmpty()) {
				// ��Ŀ�Ѿ��ر�
				return "projectSearchError";
			} else {
				Guide searchGuide = null;
				for (Guide guide : guides) {
					if (guideId.equals(guide.getId())) {
						searchGuide = guide;
						break;
					}
				}
				if (searchGuide == null) {
					// �����Ѿ�ɾ��
					return "projectSearchError";
				} else {
					q = searchGuide.getQuery();
					request.setAttribute("queryQ", q);
					search();
					if (projectCode != null && projectCode.length() > 0) {
						StringBuilder sbPath = new StringBuilder();
						if (isTest == null || isTest.trim().length() == 0 || "false".equals(isTest)) {
							sbPath.append("/search/").append(projectCode).append("/page/content.jsp");
						} else {
							sbPath.append("/search/").append(projectCode).append(ProjectFileManager.testString).append(
									"/page/content.jsp");
						}
						defualtUrl = sbPath.toString();
					}
				}
			}
		}
		return Action.SUCCESS;
	}

	public String suggest() throws Exception {
		boolean isPy = NutchConf.get().getBoolean("search.use.pinYin", false);
		if (isPy) {
			String queryString = request.getParameter("q");
			suggestList = PYContext.find(queryString);
		}
		return Action.SUCCESS;
	}

	public SearchResult getSearchResult() {
		return searchResult;
	}

	public SearchBean getSearchBean() {
		return searchBean;
	}

	public List getResultList() {
		return resultList;
	}

	public int getPage() {
		return page;
	}

	public int[] getPages() {
		return pages;
	}

	public String getFenye() {
		return fenye;
	}

	public List getIndexList() {
		return indexList;
	}

	public void setSearchBean(SearchBean searchBean) {
		this.searchBean = searchBean;
	}

	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPages(int[] pages) {
		this.pages = pages;
	}

	public void setFenye(String fenye) {
		this.fenye = fenye;
	}

	public void setIndexList(List indexList) {
		this.indexList = indexList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getQueryKeyWord() {
		return queryKeyWord;
	}

	public void setQueryKeyWord(String queryKeyWord) {
		this.queryKeyWord = queryKeyWord;
	}

	public String getIsInput() {
		return isInput;
	}

	public void setIsInput(String isInput) {
		this.isInput = isInput;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getDefualtUrl() {
		return defualtUrl;
	}

	public void setDefualtUrl(String defualtUrl) {
		this.defualtUrl = defualtUrl;
	}

	public List<Sproject> getPublishProject() {
		return publishProject;
	}

	public void setPublishProject(List<Sproject> publishProject) {
		this.publishProject = publishProject;
	}

	public String getIsTest() {
		return isTest;
	}

	public void setIsTest(String isTest) {
		this.isTest = isTest;
	}

	public String getGuideId() {
		return guideId;
	}

	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public List<String> getSuggestList() {
		return suggestList;
	}

	public void setSuggestList(List<String> suggestList) {
		this.suggestList = suggestList;
	}
}
