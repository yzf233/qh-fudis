package com.xx.platform.core.service;

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
import java.util.*;

import org.codehaus.xfire.client.*;
import org.codehaus.xfire.service.*;
import org.codehaus.xfire.service.binding.*;

/**
 * 查询管理的WebService JSR181版客户端.
 * 
 * @author calvin
 */
public class SearchServiceJSR181Client {
	private String serviceURL;
	private ServiceInterface service;

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public ServiceInterface getClient() throws Exception {
		if (service == null) {
			Service serviceModel = new ObjectServiceFactory().create(ServiceInterface.class);
			service = (ServiceInterface) new XFireProxyFactory().create(serviceModel, serviceURL);
		}
		return service;
	}

	// public List search(String keyworld) throws Exception {
	// XDClauseDotNET xdClause = new
	// XDClauseDotNET("area","石家庄","1","0","0",null);
	// XDClause xdClausea = new XDClause("area",true,"5","10",true,true,false);
	// return getClient().search_2("",null,0,40,"title",false ,"title");
	// }

	public List<WebServiceSearchResult> search(String keyworld) throws Exception {
		UserQuery u = new UserQuery();
		u.setRequireOneMoreKey(new String[][] { { "aaa", keyworld, "true" } });
		// u.setRequireOneMoreKey(new
		// String[][]{{"strTitle",keyworld,"true"},{"file_content",keyworld,"true"}});
		XDClause xdClausea = new XDClause("createDate", true, "2006-11-18", "2008-11-21", true, true, false);
		XDClause xdClause = new XDClause("vvalue", "百", true, false, false, new String[] { "<", "科技" });
		XDClause[] xdClauses = { xdClause };
		return getClient().search(keyworld, 0, 20);
		// return getClient().search(keyworld,new String[]{"vvallue,vname"},
		// xdClauses, 0, 20, new String [][] {{"boost","desc"}},"docNo");
		// return getClient().search(keyworld,new UserQuery[]{u},new String
		// [][]{{"aaa","true"}}, 0, 20, new String [][]
		// {{"contentLength","desc"}},"docNo");
	}

	public List search_1(String keyworld) throws Exception {
		// return getClient().search_1("000", 0, 100, null,true, "docNo");
		return null;
	}

	public List search_2(String keyworld) throws Exception {
		XDClause xdClause1_1 = new XDClause("bbb", "000", true, false, true, new String[] { "html" });
		XDClause xdClause1_2 = new XDClause("StringTest", "科学", false, true, true, new String[] { "北京", "西藏", "法国",
				"西红柿" });
		XDClause xdClause1_3 = new XDClause("StringTest", "科技", false, true, true, new String[] { "飞翔" });
		XDClause xdClause1_5 = new XDClause("NoDBword", "科技", false, true, true, new String[] { "111" });
		XDClause xdClause1_6 = new XDClause("NoDBword", "科技", false, true, true, new String[] { "qqqqq" });

		// XDClause的构造函数2:xdClause2_1
		// 为当指定field字段:StringTest,true:要求范围查询,且要求开始范围和结束范围分别为"中国"和"奥运",true:要求包含范围,true:要求为必须条件,false:不要求为排除条件.
		XDClause xdClause2_1 = new XDClause("IntTest", true, "1 b", "100 b", true, true, false);
		XDClause xdClause2_2 = new XDClause("bbb", true, "34", "37", true, true, true);

		// XDClause的构造函数3:xdClause3_1
		// password:生活,true:要求为必须条件,false:不要求为排除条件,true:要求是片段,String[]:片段是{"奥运","家乐福","超市"}.
		XDClause xdClause3_1 = new XDClause("StringTest", true, false, true, new String[] { "生活", "医院" });

		XDClause xdClause3_2 = new XDClause("StringTest", false, true, true, new String[] { "生活", "医院" });

		XDClause[] xdClauses = { xdClause2_2 };
		// return getClient().search_2("127",xdClauses, 0, 100, "docNo",false,
		// "docNo");
		return null;
	}

	public List search_3(String keyworld) throws Exception {
		// WebServiceSearchResult wsr = getClient().search_3(12311L, 0);
		List resultList_3 = new ArrayList<WebServiceSearchResult>();
		// resultList_3.add(wsr);
		return resultList_3;
	}

	public List<WebServiceSearchResult> search_4(String keyword) throws Exception {
		return getClient().search(keyword, new String[][] { { "strUrl", "true" }, { "content", "true" } }, 0, 20);
	}

	public List<WebServiceSearchResult> search_5(String keyword) throws Exception {
		return getClient().search(keyword, new String[][] { { "strUrl", "true" }, { "content", "true" } }, 0, 20,
				new String[][] { { "contentLength", "desc" } }, "docNo");
	}

	public List<WebServiceSearchResult> search_6(String keyworld) throws Exception {
		UserQuery u = new UserQuery();
		u.setRequireOneMoreKey(new String[][] { { "content", keyworld, "false" }});
		System.out.println(u);
		return getClient().search(keyworld, new UserQuery[] { u },
				new String[][] { { "strUrl", "true" }, { "content", "true" } }, 0, 20,
				new String[][] { { "contentLength", "desc" } }, "docNo");
	}
	
	public List<WebServiceSearchResult> search_7(String keyworld) throws Exception {
		
		return null;
	}

	public List<WebServiceSearchResult> search_xml(String keyworld) throws Exception {
		return null;
	}
	
	
	public List<WebServiceSearchResult> search_11(String keyworld) throws Exception {
		return getClient().search(keyworld, 0, 1000);
	}

	public List<WebServiceSearchResult> search_12(String keyworld) throws Exception {
		String[][] field={{"dis","true"},{"id","true"}};
		return getClient().search(keyworld,field , 0, 10);
	}
	
	public List<WebServiceSearchResult> search_13(String keyworld) throws Exception {
		String[][] field={{"dis","true"},{"id","true"},{"content","true"}};
		String[][] sortReg={{"createDate","asc"}};
		return getClient().search(keyworld,field , 0, 10,sortReg,"docNo");
	}
	
	public List<WebServiceSearchResult> search_10(String keyworld) throws Exception {
		UserQuery u = new UserQuery();
		u.setRequireKey(new String[][] { { "content", keyworld, "search" }});
		return getClient().search(keyworld, new UserQuery[] { u },
				new String[][] {{ "content", "true" }}, 0, 20,
				null, "docNo");
	}
	public static void main(String[] args) {
		SearchServiceJSR181Client client = new SearchServiceJSR181Client();
		client.setServiceURL("http://127.0.0.1:901/service/search");
		try {
			System.out.println("============");
			List<WebServiceSearchResult> resultList = client.search_10("嫁在三百里外的保定齐家");
			System.out.println("size():" + resultList.size());
			for (WebServiceSearchResult searchResult : resultList) {
				System.out.println(searchResult.getDocNo()+":"+searchResult.getSummaries());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
