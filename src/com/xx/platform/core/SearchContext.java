package com.xx.platform.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.IndexSearcher;
import org.apache.nutch.indexer.xdtech.XdtechIndexingFilter;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.xdht.XDQueryFilter;
import org.apache.nutch.util.NutchConf;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.xx.platform.core.analyzer.XDChineseAnalyzer;
import com.xx.platform.core.analyzer.XdAnalyzer;
import com.xx.platform.core.analyzer.XdOneKeyAnalyzer;
import com.xx.platform.core.nutch.CrawlRule;
import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.nutch.IndexFieldImpl;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.rpc.ClientInterface;
import com.xx.platform.core.rpc.DISClient;
import com.xx.platform.core.rpc.DistributedTool;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.dao.IBase;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.dao.URLDao;
import com.xx.platform.domain.model.crawl.Proregion;
import com.xx.platform.domain.model.crawl.Synonymy;
import com.xx.platform.domain.model.crawl.Urlfilterreg;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.domain.model.search.Guide;
import com.xx.platform.domain.model.system.Sproject;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.web.actions.system.ControlAction;

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
public class SearchContext {
	public static String tempDataPath;//用于存放节点断掉之后的数据的文件夹
	public static final int synChroTiomeOut=3000;//集群功能中的超时时间限制
	public static final File logFileDir = new File(".." + File.separator
			+ "logs");
	public static Dbtable dbtable = null;
	public static String isDefaultParse;
	static {
		isDefaultParse = NutchConf.get().get("search.query.parse", "basic");
	}
	public static List<String> wordFileList = new ArrayList();
	/**
	 * 存放数据库SQL语句的Map ，数据结构为： map--//dbconfig //dbtable //map--//属性1 //属性2 //属性3
	 * 
	 */
	public static final int SEARCH_SERVER_PORT = 8032;
	public static final int URL_SERVER_PORT = 8132;
	public static final int MANAGE_SERVER_PORT = 8232;
	public static final int CHECK_LOCALE_COUNT_PORT = 8332;
	public static final int SERVER_FLAG_PORT = 8432;
	public static final int SYNCHRO_SERVER_PORT = 8532;//集群端口
	public static ServerSocket server8432=null;
	public static final Object indexLock = new Object(); // index锁，主要是webservice使用
	public static List<Dbtable> dbValueMap = new ArrayList();
	public static List<Dbtable> dbtableList; // 数据表列表
	public static List<Proregion> proregionList = new ArrayList<Proregion>();
	public static User webServiceAuthUser = null;
	public static Control CONTROL = new Control();
	public static int wordNum = 0;
	public static boolean URL_GENERATOR_RUNNING = false;
	public static boolean IS_SEARCH_SERVER = false;
	public static final String segmentsFilePath = "segments";
	public static boolean dir_change = false;
	public static String search_dir = ".";
	private static List crawlRuleList;
	private static List<IndexFieldImpl> indexFieldList;
	private static List<Synchro> synchroList;//集群类
	private static List<Urlfilterreg> urlFilterList;
	private static List<Diserver> serverList;
	public static List<Sproject> projectList=new ArrayList<Sproject>();//缓存所有的项目
	public static Map<String,List<Guide>> allGuideMap=new HashMap<String,List<Guide>>();//缓存所有的导航，key：项目的代码，value：项目下的所有导航
	private static List regList;
	private static Xdtechsite xdtechSite;
	private static Set indexFieldSet;
	private static GeneraDAO dao;
	public static boolean useValidateCode = NutchConf.get().getBoolean(
			"console.validate.code", false);
	public final static String socketInfoPre="http://www.xd-tech.com:";//server信息返回时会以该字段为前缀
	
	
	public static boolean isInit = false;// 程序启动以后为true
	private static Map synonymyMap; // 同义词 (key:word,value:words)
	public static String contextPath;// 工程的路径
	public static String splitwords;// 工程的路径
	public static String realPath;//工程路径，，无web-info,功能的物理路径
	private static Analyzer STANDARD_ANALYZER = new org.apache.lucene.analysis.standard.StandardAnalyzer();
	public static Analyzer KEYWORD_ANALYZER = new XDChineseAnalyzer();
	private static Analyzer SPLITWORD_ANALYZER = new XdAnalyzer();
	private static Analyzer ONEWORD_ANALYZER = new XdOneKeyAnalyzer();
	
	private static Analyzer DEFAULT_ANALYZER = new Analyzer() {
	@Override
		public TokenStream tokenStream(String fieldName, Reader reader) {
			StringBuffer strb = new StringBuffer();

			int length;
			char[] charb = new char[1024];
			try {
				while ((length = reader.read(charb)) > 0) {
					strb.append(new String(charb, 0, length));
					// charb = new char[1024];
				}
			} catch (Exception ex) {
			}
			final String text = strb.toString();

			// TODO Auto-generated method stub
			return new TokenStream() {
				int i = 0;

				public Token next() throws IOException {
					i++;
					return i <= 1 ? new Token(text.toString(), 0, text.length())
							: null;
				}
			};
		}
	};

	/**
	 * 返回每种分词的Analyzer
	 * 
	 * @param typeNumber
	 * @return Analyzer
	 */
	public static Analyzer getAnalyzer(int typeNumber) {
		Analyzer analyzer = null;
		switch (typeNumber) {
		case 0:
			analyzer = DEFAULT_ANALYZER;
			break;
		case 1:
			analyzer = STANDARD_ANALYZER;
			break;
		case 2:
			analyzer = KEYWORD_ANALYZER;
			break;
		case 3:
			analyzer = SPLITWORD_ANALYZER;
			break;
		case 4:
			analyzer = ONEWORD_ANALYZER;
			break;
		default:
			analyzer = STANDARD_ANALYZER;
		}
		return analyzer;
	}

	public static int mergeNum = NutchConf.get().getInt("index.mergeNum", 20); // 索引文件目录最大segments数量（合并时数量）
	/**
	 * 系统结束标志（web容器关闭） 1 push接口 2 FileFethcer（停止产生新线程） 3 Fethcer（1 停止产生新线程。2
	 * 序列化缓存数据） 4 CrawTool （停止轮循） 5 FetchListTool （结束数据库查询）
	 */
	public static boolean isShutDown = false; // jvm结束
	public static boolean isFethcerShutDown = true;// fetcher类结束完成

	public static void reloadRules() {
		urlFilterList = dao.findAllByIObjectCType(Urlfilterreg.class);
		indexFieldList = dao.findAllByIObjectCType(IndexFieldImpl.class);
		serverList = dao.findAllByIObjectCType(Diserver.class);
		proregionList = dao.findAllByIObjectCType(Proregion.class);
		dbtableList = dao.findAllByIObjectCType(Dbtable.class);
		indexFieldSet = new HashSet(indexFieldList);
		projectList=dao.findAllByIObjectCType(Sproject.class);
		XDQueryFilter.reloadDefaultField() ;   //初始化默认检索字段
		XdtechIndexingFilter.reloadRules();
		getUrlFilterList();
		reloadGuide();//缓存所有导航
	}
public static void initProject(){
	//projectList=dao.findAllByCriteria(DetachedCriteria.forClass(Sproject.class).add(Restrictions.eq("state",1l)));
	projectList=dao.findAllByIObjectCType(Sproject.class);
}
	public static String getSplitwords() {
		if (splitwords == null)
			return initSplitwords();
		else
			return splitwords;
	}

	/**
	 * 通知所有节点，删除某个索引-胡俊
	 * 
	 * @param indexfield
	 * @throws Exception
	 */
	public static void delIndex(IndexFieldImpl indexfield) throws Exception {
		List<Diserver> diservers = getDao().findAllByIObjectCType(
				Diserver.class);
		for (Diserver d : diservers) {
			if (d.getStatus() == true) {
				InetSocketAddress defaultAddresses = new InetSocketAddress(d
						.getIpaddress(), d.getDismport());
				((ClientInterface) RPC.getProxy(ClientInterface.class,
						defaultAddresses)).deleteIndexfield(indexfield);
			}
		}
	}

	/**
	 * 通知所有节点，更新某个索引-胡俊
	 * 
	 * @param indexfield
	 * @throws Exception
	 */
	public static void updateIndex(IndexFieldImpl indexfield) throws Exception {
		List<Diserver> diservers = getDao().findAllByIObjectCType(
				Diserver.class);
		for (Diserver d : diservers) {
			if (d.getStatus() == true) {
				InetSocketAddress defaultAddresses = new InetSocketAddress(d
						.getIpaddress(), d.getDismport());
				((ClientInterface) RPC.getProxy(ClientInterface.class,
						defaultAddresses)).updateIndexfield(indexfield);
			}
		}
	}

	/**
	 * 通知所有节点，增加某个索引-胡俊
	 * 
	 * @param indexfield
	 * @throws Exception
	 */
	public static void addIndex(IndexFieldImpl indexfield) throws Exception {
		List<Diserver> diservers = getDao().findAllByIObjectCType(
				Diserver.class);
		for (Diserver d : diservers) {
			if (d.getStatus() == true) {
				InetSocketAddress defaultAddresses = new InetSocketAddress(d
						.getIpaddress(), d.getDismport());
				((ClientInterface) RPC.getProxy(ClientInterface.class,
						defaultAddresses)).addIndexfield(indexfield);
			}
		}
	}

	public static InetSocketAddress[] testSearchConnection() {
		List<Diserver> diserverList = getDiserverList();
		if (diserverList == null || diserverList.size() == 0)
			return null;
		Vector ipv = new Vector();
		for (Diserver d : diserverList) {
			if (d.getServerstatus() == true) {
				if (ipv.size() < SearchContext.CONTROL.getDisNumer())
					ipv.add(new InetSocketAddress(d.getIpaddress(), d
							.getDisport()));
			}
		}
		return (InetSocketAddress[]) ipv.toArray(new InetSocketAddress[ipv
				.size()]);
	}

	public static String initSplitwords() {
		File rf = new File(SearchContext.contextPath + "splitwords.txt");
		String words = "";
		if (!rf.exists()) {
			return words;
		} else {
			try {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(rf));
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					words += line;
				}
				reader.close();
				read.close();
			} catch (Exception e) {
			}
		}
		splitwords = words;
		return words;
	}

	private static void initSegment() {
		java.io.File segsFile = null;
		if (!(segsFile = new java.io.File(search_dir + File.separator
				+ segmentsFilePath)).exists()) {
			segsFile.mkdirs();
		}
	}

	public static List<Urlfilterreg> getUrlFilterList() {
		if (urlFilterList == null) {
			urlFilterList = dao.findAllByIObjectCType(Urlfilterreg.class);
			for (Urlfilterreg filter : urlFilterList) {
				if (filter.getFiltertype().intValue() == 1)
					filter.setPattern(Pattern.compile(filter.getFilterreg(),
							Pattern.CASE_INSENSITIVE));
			}
		}
		return urlFilterList;
	}

	public static Set<CrawlRule> getCrawlRuleSet() {
		return new HashSet<CrawlRule>(crawlRuleList);
	}

	public static Set<IndexField> getIndexFieldSet() {
		if (indexFieldList == null) {
			indexFieldList = dao.findAllByIObjectCType(IndexFieldImpl.class);
			indexFieldSet = new HashSet(indexFieldList);
		}
		return indexFieldSet;
	}

	public static List getIndexFieldList() {
		if (indexFieldList == null) {
			getIndexFieldSet();
		}
		return indexFieldList;
	}

	private static IndexField CONTENT_FIELD = new IndexFieldImpl("content",
			"content", 1, false);
	private static IndexField TITLE_FIELD = new IndexFieldImpl("title",
			"title", 1, false);
	private static IndexField DATA_SOURCE_FIELD = new IndexFieldImpl("dataSource",
			"dataSource", 1, false);

	public static IndexField getIndexField(String fieldName) {
		IndexField reIndexField = null;
		if (fieldName.equals("content")) {
			return CONTENT_FIELD;
		} else if (fieldName.equals("title")) {
			return TITLE_FIELD;
		} else if (fieldName.equals("dataSource")) {
			return DATA_SOURCE_FIELD;
		} else {
			try {
				if (indexFieldList == null) {
					getIndexFieldSet();
				}
				for (IndexField indexField : indexFieldList) {
					if (indexField.getCode().equals(fieldName)) {
						reIndexField = indexField;
						break;
					}
				}
			} catch (Exception ex) {
			}
			return reIndexField;
		}
	}

	public static List<Dbtable> getDbtableList() {
		if (dbtableList == null) {
			dbtableList = dao.findAllByIObjectCType(Dbtable.class);
		}
		return dbtableList;
	}

	public static Set getRegSet() {
		return new HashSet(regList);
	}

	public static GeneraDAO getDataHandler() {
		if (dao == null) {
			dao = ((GeneraDAO) WebApplicationContextUtils
					.getWebApplicationContext(
							ServletActionContext.getServletContext()).getBean(
							IbeaProperty.DAO_NAME_SPACE));
		}
		return dao;
	}

	public static GeneraDAO getDataHandler(ServletContext context) {
		if (dao == null) {
			dao = ((GeneraDAO) WebApplicationContextUtils
					.getWebApplicationContext(context).getBean(
							IbeaProperty.DAO_NAME_SPACE));
		}
		/**
		 * 获得 检索路径
		 */
		initXdtechSite();
		SearchContext.search_dir = xdtechSite != null ? xdtechSite
				.getSearchdir() : "";
		/**
		 * 初始化索引存放路径
		 */
		initSegment();
		return dao;
	}

	public static void setDao(GeneraDAO dao) {
		SearchContext.dao = dao;
	}

	public static GeneraDAO getDao() {
		return dao;
	}

	public static void setServerList(List serverList) {
		SearchContext.serverList = serverList;
	}

	public void setDbtable(Dbtable dbtable) {
		this.dbtable = dbtable;
	}

	private static IBase ibase;

	public static IBase getIbase() {
		return ibase != null ? ibase : (ibase = new URLDao(dao));
	}

	public static Map getSynonymyMap() {
		if (synonymyMap == null) {
			synonymyMap = new HashMap();
			String[] a;
			List<Synonymy> synonymyList = dao
					.findAllByIObjectCType(Synonymy.class);
			for (Synonymy s : synonymyList) {
				a = s.getWords().split("[,， 　]");
				if (a != null && a.length > 0) {
					for (String word : a) {
						// 一个词不能属于多组
						synonymyMap.put(word.toLowerCase(), a);
					}
				}
			}
		}
		return synonymyMap;
	}

	public static void addNewSynonymy(Synonymy synonymy) {
		getSynonymyMap();
		String[] a = synonymy.getWords().split("[,， 　]");
		if (a != null && a.length > 0) {
			for (String word : a) {
				// 一个词不能属于多组
				synonymyMap.put(word.toLowerCase(), a);
			}
		}
	}

	public static void reloadSynonymyMap() {
		synonymyMap = new HashMap();
		String[] a;
		List<Synonymy> synonymyList = dao.findAllByIObjectCType(Synonymy.class);
		for (Synonymy s : synonymyList) {
			a = s.getWords().split("[,， 　]");
			if (a != null && a.length > 0) {
				for (String word : a) {
					// 一个词不能属于多组
					synonymyMap.put(word.toLowerCase(), a);
				}
			}
		}
	}

	public static String getSegFilePath() {
		return search_dir + File.separator + segmentsFilePath;
	}

	public static void initXdtechSite() {
		if (dao != null) {
			List<Xdtechsite> list = dao.findAllByIObjectCType(Xdtechsite.class);
			if (list.size() > 0) {
				xdtechSite = list.get(0);
			}
		}
	}

	public static void resetXdtechSite(String dir, GeneraDAO service) {
		if (service != null) {
			xdtechSite.setSearchdir(dir);
			service.updateIObject(xdtechSite);
			search_dir = xdtechSite.getSearchdir();
			initSegment();
			dir_change = true;
		}
	}

	public static void resetXdtechSite(Xdtechsite xdtechSite, GeneraDAO service) {
		if (service != null) {
			service.updateIObject(xdtechSite);
			initXdtechSite();
		}
	}

	public static Xdtechsite getXdtechsite() {
		return xdtechSite;
	}

	public static List<Diserver> getDiserverList() {
		if (serverList == null)
			serverList = dao.findAllByIObjectCType(Diserver.class);
		return serverList;
	}

	public static void reloadDiserverList() {
		serverList = null;
		getDiserverList();
	}

	public static List<Proregion> getProregionList() {
		if (proregionList == null)
			proregionList = dao.findAllByIObjectCType(Proregion.class);
		return proregionList;
	}

	public Dbtable getDbtable() {
		return dbtable;
	}

	public static String[] getDiservers() {
		if (serverList == null)
			serverList = getDiserverList();
		String[] servers = new String[serverList.size()];
		for (int i = 0; i < servers.length; i++) {
			servers[i] = serverList.get(i).getIpaddress() + " "
					+ serverList.get(i).getDisport();
		}
		return servers;
	}

	// public static void startCrawl(boolean task) {
	// InetSocketAddress defaultAddresses = null;
	// boolean start = true;
	// try {
	// if (serverList == null) {
	// getDiserverList();
	// }
	// for (Diserver server : serverList) {
	// if(server.getStatus()){
	// if(task){
	// //定时任务启动爬虫
	// if(server.getCrawlmport()==null||server.getCrawlmport()!=1){
	// start = false;
	// }
	// }
	// if(start){
	// defaultAddresses = new InetSocketAddress(server.getIpaddress(),
	// server.getConport());
	// ((ClientInterface) org.apache.nutch.ipc.RPC.getProxy(
	// ClientInterface.class,
	// defaultAddresses)).start();
	// }
	// }
	// }
	// }
	// catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	// public static void startCrawl() {
	// startCrawl(false);
	// }

	public static void stopCrawl() {
		InetSocketAddress defaultAddresses = null;
		try {
			if (serverList == null) {
				getDiserverList();
			}
			for (Diserver d : serverList) {// 遍历每个节点
				if (d.getStatus() == true) {
					ControlAction.addXdmessage("通知节点" + d.getDname() + "停止采集。");

					defaultAddresses = new InetSocketAddress(d.getIpaddress(),
							d.getDismport());
					if (DistributedTool.testConnection(defaultAddresses))// 如果连接成功
					{
						try {
							((ClientInterface) RPC.getProxy(
									ClientInterface.class, defaultAddresses))
									.stop();// 停止所有节点采集
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						ControlAction.addXdmessage("节点" + d.getDname()
								+ "连接失败！");
					}
					ControlAction.addXdmessage("  ");
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void commandCrawlDb(Diserver server, boolean command) {
		InetSocketAddress defaultAddresses = null;
		if (server.getStatus()) {
			defaultAddresses = new InetSocketAddress(server.getIpaddress(),
					server.getDismport());
			ClientInterface client = ((ClientInterface) org.apache.nutch.ipc.RPC
					.getProxy(ClientInterface.class, defaultAddresses));
			try {
				if (command) {
					client.start_Db();
				} else {
					client.stop();
				}
			} catch (Exception ex) {
			}
		}
	}

	public static void commandCrawlFile(Diserver server, boolean command) {
		InetSocketAddress defaultAddresses = null;
		if (server.getStatus()) {
			defaultAddresses = new InetSocketAddress(server.getIpaddress(),
					server.getDismport());
			ClientInterface client = ((ClientInterface) org.apache.nutch.ipc.RPC
					.getProxy(ClientInterface.class, defaultAddresses));
			try {
				if (command) {
//					NutchCommand.setCrawl(true);
//					NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
					client.start_File();
				} else {
					client.stop();
				}
			} catch (Exception ex) {
			}
		}
	}

	public static void updateServerStatus(String ip, int port, boolean upOrDown) {
		if (serverList == null)
			getDiserverList();
		for (Diserver server : serverList) {
			if (server.getIpaddress().equals(ip)
					&& (server.getDismport() == port || server.getDisport() == port)) {
				if (server.getDisport() == port)
					server.setServerstatus(upOrDown);
				else
					server.setStatus(upOrDown);
				getDataHandler().updateIObject(server);
			}
		}
	}

	public static void addNewIndex(String ip, String indexPath) {
		if (serverList == null)
			getDiserverList();
		for (Diserver server : serverList) {
			if (server.getIpaddress().equals(ip)
					&& (server.getIndexpath().equalsIgnoreCase(indexPath))) {
				DISClient.addNewIndex(server);
			}
		}
	}

	/**
	 * fetchURList
	 * 
	 * @return WebDB[]
	 */
	public static String[][] fetchURList() {
		FetchListTool fetchList = new FetchListTool();
		SearchContext.dbtable = fetchList.getDbtable();
		String[][] results = null;
		List<Map<String, String>> list = fetchList.getDbList();
		if (list != null && list.size() > 0 && list.get(0) != null) {
			results = new String[list.size() + 1][list.get(0).size()];
			results[0] = new String[list.get(0).size()];
			int a = 0;
			Set key = list.get(0).keySet();
			for (java.util.Iterator it = key.iterator(); it.hasNext();) {
				results[0][a] = it.next().toString();
				a++;
			}
			// results[0] = (String[]) list.get(0).keySet().toArray()
			for (int i = 1; i <= list.size(); i++) {
				a = 0;
				for (java.util.Iterator it = key.iterator(); it.hasNext();) {
					results[i][a] = list.get(i - 1).get(it.next());
					a++;
				}
				// results[i] = (String[]) list.get(i-1).values().toArray();
			}
		}
		return results;
	}

	// //取文件
	public static String[][] fetchURList(boolean file) {
		FetchListTool fetchList = new FetchListTool(file);
		SearchContext.dbtable = fetchList.getDbtable();
		String[][] results = null;
		List<Map<String, String>> list = fetchList.getDbList();
		if (list != null && list.size() > 0 && list.get(0) != null) {
			results = new String[list.size() + 1][list.get(0).size()];
			results[0] = new String[list.get(0).size()];
			int a = 0;
			Set key = list.get(0).keySet();
			for (java.util.Iterator it = key.iterator(); it.hasNext();) {
				results[0][a] = it.next().toString();
				a++;
			}
			// results[0] = (String[]) list.get(0).keySet().toArray()
			for (int i = 1; i <= list.size(); i++) {
				a = 0;
				for (java.util.Iterator it = key.iterator(); it.hasNext();) {
					results[i][a] = list.get(i - 1).get(it.next());
					a++;
				}
				// results[i] = (String[]) list.get(i-1).values().toArray();
			}
		}
		return results;
	}

	public static void merger() {
		InetSocketAddress defaultAddresses = null;
		try {
			if (serverList == null) {
				getDiserverList();
			}
			for (Diserver server : serverList) {
				defaultAddresses = new InetSocketAddress(server.getIpaddress(),
						server.getDismport());
				((ClientInterface) org.apache.nutch.ipc.RPC.getProxy(
						ClientInterface.class, defaultAddresses)).merger();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * getTaskQurey
	 * 
	 * @return Query
	 */
	public static Query getTaskQurey() {

		return null;
	}

	/**
	 * org.apache.lucene.search.IndexSearcher 新增变量 private static Map delMap
	 * 
	 * 如果delMap不为空，那么对结果进行过滤，如果结果的id在delMap里存在，那么不执行collect while(scorer.next()) {
	 * int scorerDocId = scorer.doc(); if
	 * (getDelMap().get(String.valueOf(scorerDocId))==null) {
	 * results.collect(scorerDocId, scorer.score()); } }
	 * 
	 * @param id
	 * @author hujun
	 */
	public static void addResultFilter(String id) {
		IndexSearcher.getDelMap().put(id, id);
	}
	/**
	 * 该方法获取不包含本机IP的同步集群
	 * @return
	 */
	public static List<Synchro> getSynchroList() {
			List<Synchro> synList=new ArrayList<Synchro>(); 
	        if (synchroList == null){
	        	synchroList = dao.findAllByIObjectCType(Synchro.class);
	        	synList.addAll(synchroList);
	        	if(xdtechSite!=null&&xdtechSite.getLocalip()!=null&&xdtechSite.getLocalip().trim().length()>0){
	        		Synchro temp=null;
	        		String localIp=xdtechSite.getLocalip();
	        		for(Synchro syn:synList){
	        			if(localIp.equals(syn.getIpaddress())){
	        				temp=syn;
	        				continue;
	        			}
	        		}
	        		if(temp!=null){
	        			synList.remove(temp);
	        		}
	        	}
	        }else{
	        	synList.addAll(synchroList);
	        	if(xdtechSite!=null&&xdtechSite.getLocalip()!=null&&xdtechSite.getLocalip().trim().length()>0){
	        		Synchro temp=null;
	        		String localIp=xdtechSite.getLocalip();
	        		for(Synchro syn:synList){
	        			if(localIp.equals(syn.getIpaddress())){
	        				temp=syn;
	        				break;
	        			}
	        		}
	        		if(temp!=null){
	        			synList.remove(temp);
	        		}
	        	}
	        }
	        if(synList==null){
	        	synList=new ArrayList<Synchro>(); 
	        }
	        return synList;
	}
	public static void reloadSynchroList() {
	      	synchroList = dao.findAllByIObjectCType(Synchro.class);
	      	ImDistributedTool.reloadNode();
	}
	/**
	 * 修改文件、网络IP地址
	 * @param oldIp
	 * @param newIp
	 * @param service
	 * @throws Exception 
	 */
	public static void updateLocaip(String oldIp,String newIp,IDaoManager service) throws Exception {
		StringBuilder sbHql=new StringBuilder();
		sbHql.append("update FileDirectory set localip='").append(newIp).append("' where localip='").append(oldIp).append("'");
		service.execByHQL(sbHql.toString(),"0");
		sbHql.setLength(0);
		sbHql.append("update Dbconfig set localip='").append(newIp).append("' where localip='").append(oldIp).append("'");
		service.execByHQL(sbHql.toString(),"0");
	}
	/**
	 * 缓存所有导航
	 */
	public static void reloadGuide(){
		Map<String,List<Guide>> guideMap=new HashMap<String,List<Guide>>();
		List<Guide> guides=dao.findAllByIObjectCType(Guide.class);
		if(guides!=null){
			for(Guide guide:guides){
				String pid=guide.getPid();
				String key=null;
				for(Sproject project:projectList){
					if(pid.equals(project.getId())){
						key=project.getCode();
						break;
					}
				}
				if(key!=null){
					List<Guide> guideList=guideMap.get(key);
					if(guideList==null){
						guideList=new ArrayList<Guide>();
						guideMap.put(key, guideList);
					}
					guideList.add(guide);
				}
			}
		}
		allGuideMap=guideMap;
	}
}
