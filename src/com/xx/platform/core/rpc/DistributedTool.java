package com.xx.platform.core.rpc;

import java.io.*;
import java.util.*;

import org.apache.nutch.fetcher.FileFetcher;
import org.apache.nutch.ipc.*;

import java.net.InetSocketAddress;

import com.xx.platform.core.*;
import com.xx.platform.core.db.*;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.database.Tableproperty;
import com.xx.platform.domain.model.distributed.*;
import com.xx.platform.domain.model.system.*;
import com.xx.platform.util.tools.MD5;

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
public class DistributedTool<T> implements ServerInterface, ServiceInterface {
	private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
	private static org.apache.nutch.ipc.Server server;
	private FetchListTool fetchList;
	static {
			 System.setProperty("sun.net.client.defaultConnectTimeout", "3000"); //����HTTP��������ʱʱ��
			 System.setProperty("sun.net.client.defaultReadTimeout", "3000"); //����HTTP�������ݶ�ȡ��ʱʱ��
	}
	public static void startServer() {
		// System.out.println("��ʼ�����ֲ�ʽ....");
		DistributedTool disTool = new DistributedTool();
		try {
			server = RPC.getServer(disTool, SearchContext.MANAGE_SERVER_PORT,
					10, true);
			server.start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	static{
	    Thread hookThread = new Thread(new Runnable() {
	        public void run() {
	        	try
	        	{
	        		stopServer();
	        	}
	        	catch(Exception e)
	        	{}
	      	  }
	      });
	    Runtime.getRuntime().addShutdownHook(hookThread);
		Thread thread = new Thread(){
			public void run()
			{
				while(true){
					if(SearchContext.getDiserverList()!=null && SearchContext.getDiserverList().size()>0&&RuntimeDataCollect.diserver!=0)
						testConnection() ;
					try{
						Thread.sleep(2500) ;
					}catch(Exception ex){}
				}
			}
		};
		thread.setDaemon(true) ;
		thread.start();
	}
	/**
	 * ���ڵ�����״̬
	 */
	public static void testConnection(){
		for(Diserver dis : SearchContext.getDiserverList()){
			boolean fromStatus=dis.getStatus();//����˿�ԭʼ״̬
			boolean fromServerStatus=dis.getServerstatus();//��Ѱ�˿�ԭʼ״̬
			try{
				java.net.URL url = new java.net.URL("http://"+dis.getIpaddress()+":"+dis.getDismport());
				java.net.HttpURLConnection  con=(java.net.HttpURLConnection)url.openConnection();
				con.connect();
				con.disconnect();
				dis.setStatus(true) ;
			}catch(Exception ex){
				dis.setStatus(false) ;
			}
			try{
				java.net.URL url = new java.net.URL("http://"+dis.getIpaddress()+":"+dis.getDisport());
				java.net.HttpURLConnection  con=(java.net.HttpURLConnection)url.openConnection();
				con.connect();
				con.disconnect();
				dis.setServerstatus(true);
			}catch(Exception ex){
				dis.setServerstatus(false) ;
			}
			if(dis.getStatus()!=fromStatus||dis.getServerstatus()!=fromServerStatus)
			{
				SearchContext.getDao().updateIObject(dis);//����ڵ��������Է����仯����ô��������ڵ������״̬
				SearchContext.reloadDiserverList();
			}
		}
	}
	public static boolean testConnection(InetSocketAddress defaultAddresses)
	{
		try{
			java.net.URL url = new java.net.URL("http:/"+defaultAddresses.getAddress()+":"+defaultAddresses.getPort());
			java.net.HttpURLConnection  con=(java.net.HttpURLConnection)url.openConnection();
			con.connect();
			con.disconnect();
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	public static void stopServer() {
		if (server != null)
			server.stop();
	}

	public IndexFieldImpl[] indexFieldList() throws Exception {
		List<IndexFieldImpl> fieldlist = (SearchContext.getDao()
				.findAllByIObjectCType(IndexFieldImpl.class));
		IndexFieldImpl[] fields=fieldlist.toArray(new IndexFieldImpl[fieldlist.size()]);;
		return fields;
	}

	/**
	 * �ӵ��ȷ�����ȡ���ݿ����ݡ�
	 * @author ����
	 */
	public synchronized DBList getFetchList() throws Exception {
		fetchList = new FetchListTool();
		DBList dbls = new DBList(fetchList.getDbList());
		return dbls;
	}
	/**
	 * �ӵ��ȷ�����ȡ�ļ����ݡ�
	 * @author ����
	 */
	public DBList getFileFetchList() throws Exception{
		DBList dbls = new DBList();
//			List<Map<String, String>> rs=new ArrayList<Map<String, String>>();
//			while(FileFetcher.RMdatas.size()!=0)
//			{
//				try
//				{
//				rs.add(FileFetcher.RMdatas.remove(0));
//				}catch(Exception e)//�п���ͬ���ɼ���������Խ�����ֱ�Ӳ���
//				{}
//			}
//			dbls = new DBList(rs);
		return dbls;
	}
	/**
	 * �жϽڵ�ɼ����������Ƿ��ظ�
	 * @author ����
	 * @param str
	 * @return
	 * @throws Exception
	 */
    public boolean addContents(String str) throws Exception{
    if (webDbAdminTool.addContents(MD5.encoding(str)))
    	return true;
    else
    	return false;
    }
	public Proregion[] getProregionList() throws Exception {
		return (Proregion[]) SearchContext.getProregionList().toArray(
				new Proregion[SearchContext.getProregionList().size()]);
	}

	public Xdtechsite getSiteinfo() throws Exception {
		return com.xx.platform.core.SearchContext.getXdtechsite();
	}

	public Urlfilterreg[] getUrlFilterList() throws Exception {
		return (Urlfilterreg[]) com.xx.platform.core.SearchContext
				.getUrlFilterList().toArray(
						new Urlfilterreg[com.xx.platform.core.SearchContext
								.getUrlFilterList().size()]);
	}

	public String[][] fetchURList() throws Exception {
		return com.xx.platform.core.SearchContext.fetchURList();
	}

	public Dbtable getDbtable() throws Exception {
		return com.xx.platform.core.SearchContext.dbtable;
	}

	/**
	 * ��õ�ַ�б���Ϣ
	 * 
	 * @param page_size
	 *            long
	 * @return List
	 * @throws Exception
	 * 
	 * public WebDB[] fetchURList(long start ,long page_size) throws Exception {
	 * WebDB[] webDb = (WebDB[]) webDbAdminTool.getWebDB_A(start, page_size) ;
	 * if(webDb==null || webDb.length==0) { FetchListTool fetcherListTool = new
	 * FetchListTool() ; List<WebDB> list = fetcherListTool.getDbList() ; webDb =
	 * (WebDB[])list.toArray(new WebDB[list.size()]) ; } return webDb; } /**
	 * ��õ�ַ�б���Ϣ
	 * @param page_size
	 *            long
	 * @return List
	 * @throws Exception
	 * 
	 * public WebDB[] fetchURList(long start, long page_size, long time) throws
	 * Exception { return (WebDB[]) webDbAdminTool.getWebDB_B(start,
	 * page_size,time); } /** �����ַ��Ϣ�����������ַ֮�����ݴ��� Server
	 * @param list
	 *            List
	 * @throws Exception
	 */
	public void putParseURL(WebDB[] webDbs) throws Exception {
	}

	/**
	 * ���ؽ����� ��Ҫ�������ݿ�� ����
	 * 
	 * @param list
	 *            List
	 * @throws Exception
	 */
	public void putParseData(Object[] obj) throws Exception {
		// To do
	}

	public String getStatus() throws Exception {
		return RuntimeDataCollect.getCrawl_status();
	}

	public static void getCrawlStatus() {
		InetSocketAddress defaultAddresses = null;
		String status = NutchCommand.CRAWL_STATUS_NOT_RUNNING;
		CrawlInfo crawlInfo = null;
		long crawl_page = 0;
		long proc_page = 0;
		long threads_num = 0;

		try {
			List<Diserver> serverList = SearchContext.getDiserverList();
			for (Diserver server : serverList) {
				if (server.getStatus()) {
					defaultAddresses = new InetSocketAddress(server
							.getIpaddress(), server.getConport());
					crawlInfo = ((ClientInterface) org.apache.nutch.ipc.RPC
							.getProxy(ClientInterface.class, defaultAddresses))
							.getCrawlInfo();
					if (status != null
							&& (crawlInfo.getStatus().equals(
									NutchCommand.CRAWL_STATUS_RUNNING) || crawlInfo
									.getStatus().equals(
											NutchCommand.CRAWL_STATUS_STOPPING))) {
						status = NutchCommand.CRAWL_STATUS_RUNNING;
						crawl_page = crawl_page + crawlInfo.getCrawledNum();
						threads_num = threads_num
								+ ((crawlInfo.getThreads() != null && !crawlInfo
										.getThreads().trim().equals("")) ? Long
										.parseLong(crawlInfo.getThreads()) : 0L);
					}
					if (status != null
							&& crawlInfo.getStatus().equals(
									NutchCommand.CRAWL_STATUS_IDLE))
						proc_page = proc_page + crawlInfo.getProcess_num();

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RuntimeDataCollect.setCrawl_page_num(0);
		RuntimeDataCollect.setCrawl_page_num(crawl_page);
		RuntimeDataCollect.setProcess_num(proc_page);
		RuntimeDataCollect.crawl_thread_num = (int) threads_num;
		RuntimeDataCollect.setCrawl_status(status);
	}

	public static CrawlInfo getDiserverStatus(Diserver diserver)
			throws Exception {
		InetSocketAddress defaultAddresses = null;
		String status = null;
		CrawlInfo crawlInfo = null;
		try {
			{
				if (diserver.getStatus()) {
					defaultAddresses = new InetSocketAddress(diserver
							.getIpaddress(), diserver.getDismport());
					crawlInfo = ((ClientInterface) org.apache.nutch.ipc.RPC
							.getProxy(ClientInterface.class, defaultAddresses))
							.getCrawlInfo();
					if (status != null
							&& crawlInfo.getStatus().equals(
									NutchCommand.CRAWL_STATUS_RUNNING))
						status = NutchCommand.CRAWL_STATUS_RUNNING;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return crawlInfo;
	}

	/**
	 * ���ݽڵ㴫�ݵ�ip�͹���˿ڣ����ص��ȷ����������õ�����ڵ����ϸ��Ϣ��
	 * 
	 * @author ����
	 */
	public Diserver getBindSearchServer(String ip, int port) throws Exception {
		for (Diserver server : com.xx.platform.core.SearchContext
				.getDiserverList()) {
			if (server.getIpaddress() != null
					&& server.getIpaddress().equals(ip))
				if (server.getDismport() == port)// ���ip�͹���˿���ͬ���򷵻�����ڵ�������á�����
					return server;
		}
		return null;
	}

	public String getName() {
		return "�ֲ�ʽ���ݷ���";
	}

	public void updateWebDB(WebDB webDb) throws Exception {
		webDbAdminTool.updateWebDB(webDb);
	}

	public void addWebDB(WebDB webDb) throws Exception {
		webDbAdminTool.addWebDB(webDb);
	}

	public boolean addWebDB(String webDb) throws Exception {
		return webDbAdminTool.addContents(webDb);
	}

	public void reloadWebDB() throws Exception {
		webDbAdminTool.reload();
	}

	public void updateStatus(String ip, int port, boolean status)
			throws Exception {
		SearchContext.updateServerStatus(ip, port, status);
		SearchContext.reloadDiserverList();
	}

	public byte[] getFileContent(String path) {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			FileInputStream input = null;
			try {
				input = new FileInputStream(file);
				byte[] fileC = new byte[(int) file.length()];
				input.read(fileC);
				return fileC;
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				try {
					input.close();
				} catch (IOException ex1) {
				}
			}
		}
		return null;
	}

	public boolean getNutchCommand_CRAWL() {
		if (NutchCommand.CRAWL_COMMAND_CRAWLER) {
//			NutchCommand.CRAWL_COMMAND_CRAWLER = false;
			return true;
		}
		return false;
	}

	public boolean getNutchCommand_FILECRAWL() {
		if (NutchCommand.CRAWL_COMMAND_FILECRAWLER) {
//			NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
			return true;
		}

		return false;
	}

	public String[][] fileFetchURList() {
		return SearchContext.fetchURList(true);
	}

	public int getRMdatasSize() {
		return -1;
//		return FileFetcher.RMdatas.size();
	}

	public boolean removeContents(String str) throws Exception {
	    if (webDbAdminTool.removeContents(str))
	    	return true;
	    else
	    	return false;
	}
	public void removeAllContents( byte[] data) throws Exception {
		java.io.ByteArrayInputStream bi = new java.io.ByteArrayInputStream(data);
		java.io.ObjectInputStream oi = new java.io.ObjectInputStream(bi);
		List<String> ls=(List<String>)oi.readObject();
		if(ls!=null&&ls.size()>0){
			for(String str:ls){
				webDbAdminTool.removeContents(str);
			}
		}
	}
}
