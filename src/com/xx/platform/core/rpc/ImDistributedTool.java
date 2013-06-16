package com.xx.platform.core.rpc;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.nutch.fetcher.FetcherDataPro;
import org.apache.nutch.fetcher.IndexFetcher;
import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.util.NutchConf;
import org.apache.poi.ss.formula.functions.T;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.distributed.SynState;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.domain.model.system.ProjectUser;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.IndexMessage;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;
import com.xx.platform.web.actions.crawl.CrawlAction;
import com.xx.platform.web.actions.system.ProjectFileManager;
/**
 * �ֲ�ʽ�ӿ�ʵ����
 * @author Administrator
 *
 */

public class ImDistributedTool implements ImInterface,ServiceInterface  {
	private static final float scorePower = NutchConf.get().getFloat("indexer.score.power", 0.5f);
	private static final boolean boostByLinkCount = NutchConf.get().getBoolean("indexer.boost.by.link.count", false);
	
	
	public static boolean isRuning=false;//�Ƿ������ֲ�ʽ
	private static Map<String , InetSocketAddress> nodeMap = new HashMap() ;
	public static boolean isReady = false ;
	private static org.apache.nutch.ipc.Server server;
	private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
	 static {
		 System.setProperty("sun.net.client.defaultConnectTimeout", "5000"); //����HTTP��������ʱʱ��
		 System.setProperty("sun.net.client.defaultReadTimeout", "5000"); //����HTTP�������ݶ�ȡ��ʱʱ��
	 }
	/**
	 * ��������˿�
	 */
	public static void startServer() {
		ImDistributedTool disTool = new ImDistributedTool();
		try {
			server = RPC.getServer(disTool, SearchContext.SYNCHRO_SERVER_PORT,10, true);
			//server = RPC.getServer(disTool,8632,10, true);
			server.start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ֹͣ����˿�
	 */
	public static void stopServer() {
		if (server != null)
			server.stop();
	}
	static{
		reloadNode() ;
		Thread thread = new Thread(){
			public void run()
			{
				while(true){
					if(SearchContext.getSynchroList()!=null && SearchContext.getSynchroList().size()>0)
						testConnection() ;
					try{
						Thread.sleep(1000) ;
					}catch(Exception ex){}
				}
			}
		};
		thread.setDaemon(true) ;
		thread.start();
	}
	public static void reloadNode(){
		nodeMap = new HashMap();
		for(int i=0 ; i<SearchContext.getSynchroList().size(); i++){
			InetSocketAddress isa = new InetSocketAddress(SearchContext.getSynchroList().get(i).getIpaddress(),SearchContext.SYNCHRO_SERVER_PORT) ;
			//InetSocketAddress isa = new InetSocketAddress(SearchContext.getSynchroList().get(i).getIpaddress(),8632) ;
			nodeMap.put(SearchContext.getSynchroList().get(i).getIpaddress(), isa) ;
		}
	}
	static long ping = 0 ;
	static boolean endThread = false ;
	public static void testConnection(){
		Synchro synchro = null ;
//			Thread terstt = null ;
		try{
			for(Synchro syn : SearchContext.getSynchroList()){
				endThread = false ;
				synchro = syn ;
				java.net.URL url = new java.net.URL("http:/"+nodeMap.get(synchro.getIpaddress()).getAddress()+":"+nodeMap.get(synchro.getIpaddress()).getPort());
				//java.net.URL url = new java.net.URL("http:/"+nodeMap.get(synchro.getIpaddress()).getAddress()+":"+8632);
				java.net.HttpURLConnection  con=(java.net.HttpURLConnection)url.openConnection();
				con.connect();
				con.disconnect();
				synchro.setState(1) ;
				((IDaoManager)SearchContext.getDao()).updateIObject(synchro,"0");
				
			}
			isReady=true ;
		}catch(Exception ex){
			if(synchro!=null)
			{
				synchro.setState(0) ;
				try
				{
					((IDaoManager)SearchContext.getDao()).updateIObject(synchro,"0");
				}
				catch(Exception e)
				{}
			}
			isReady = false ;
		}finally{
			endThread = true ;
		}
		
	}
	public static InetSocketAddress getNode(String ip){
		return nodeMap.get(ip) ;
	}
	public void getStatus() {
	}

	public void delete(byte[] object) throws IOException {
		((IDaoManager)SearchContext.getDao()).deleteIObject(ArrayToObject(object),"0");
		reloadAll();
	}
	private void reloadAll()
	{
		SearchContext.getDataHandler();
		SearchContext.reloadRules();
		//xdtechsite����
		SearchContext.initXdtechSite();
		CheckIPUtil.init();
	}
	/**
	 * ͬ�����ݿ�������
	 * @param object �����
	 * @throws IOException
	 */
	public void save(byte[] object) throws IOException  {
		Object obj=ArrayToObject(object);
		java.lang.reflect.Method idMethod;
		try {
			idMethod = obj.getClass().getMethod("getId", new Class[] {});

			String oldId = (String) idMethod.invoke(obj, new Object[] {});
			((IDaoManager)SearchContext.getDao()).saveIObject(obj,"0");
			String newId = (String) idMethod.invoke(obj, new Object[] {});
			((IDaoManager)SearchContext.getDao()).execByHQL(
					"update " + obj.getClass().getName() + " set id='"
							+ oldId + "' where id='" + newId + "'","0");
			reloadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ͬ�����ݿ���²���
	 * @param object �����
	 * @throws IOException
	 */
	public void saveOrUpdate(byte[] object)  throws IOException {
		Object obj=ArrayToObject(object);
		java.lang.reflect.Method idMethod;
		try {
			idMethod = obj.getClass().getMethod("getId", new Class[] {});

			String oldId = (String) idMethod.invoke(obj, new Object[] {});
			((IDaoManager)SearchContext.getDao()).saveOrUpdateIObject(obj,"0");
			String newId = (String) idMethod.invoke(obj, new Object[] {});
			((IDaoManager)SearchContext.getDao()).execByHQL(
					"update " + obj.getClass().getName() + " set id='"
							+ oldId + "' where id='" + newId + "'","0");
			reloadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ͬ�����ݿ���²���
	 * @param object �����
	 * @throws IOException
	 */
	public void update(byte[] object)  throws IOException {
		((IDaoManager)SearchContext.getDao()).updateIObject(ArrayToObject(object),"0");
		reloadAll();
	}

	public String getName() {
		return "insitese";
	}
	public Object ArrayToObject(byte [] in) throws IOException
	{
		Object o=null;
		java.io.ByteArrayInputStream bi = new java.io.ByteArrayInputStream(in);
		java.io.ObjectInputStream oi = new java.io.ObjectInputStream(bi);
		try {
			o=oi.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally
		{
			oi.close();
			bi.close();
		}
		return o;

	}
	/**
	 * �ڽڵ㱾�ؽ����ļ�
	 * @param filepath �ļ�·��
	 * @param in �ļ�file����
	 * @throws IOException
	 */
	public void makeFile(String filepath,byte [] in) throws IOException {
        FileOutputStream output = new FileOutputStream(SearchContext.contextPath+filepath); 
        output.write(in);
        output.close();
	}

	/**
	 * ��ڵ㷢������List<String,map<String,String>>,�ڽڵ㽨������
	 * @param in ����List<String,map<String,String>>��byte[]����
	 * @throws Exception
	 */
	public void push(byte[] in) throws Exception {
	}
	/**
	 * ִ��hql���
	 */
	public int execByHQL(byte[] stringHql) throws Exception {
		String hql=(String)ArrayToObject(stringHql);
		int count=((IDaoManager)SearchContext.getDao()).execByHQL(hql,"0");
		reloadAll();
		return count;
	}
	/**
	 * ִ��ɾ���û�����
	 */
	public void deleteUser(byte[] projectUser) throws IOException {
		ProjectUser user=(ProjectUser)ArrayToObject(projectUser);
		((IDaoManager)SearchContext.getDao()).deleteUser(user,"0");
	}
	/**
	 * ִ��������Ӳ���
	 */
	public void inserBat(byte[] list) throws IOException {
		List<T> listObject=(List<T>)ArrayToObject(list);
		((IDaoManager)SearchContext.getDao()).inserBat(listObject,"0");
		reloadAll();
	}

	
/****************************************�ļ����������ݿ�����ķָ���*******************************************/
	/**
	 * ������Դ
	 */
	public void publish(byte[] bprojectCode)throws IOException{
		ProjectFileManager fileManager=new ProjectFileManager();
		String projectCode=(String)ArrayToObject(bprojectCode);
		fileManager.publish(projectCode,false);
	}
	/**
	 * ��Ŀ��������
	 */
	public void publicTest(String code) throws IOException{
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.publicTest(code,false);
	} 
	/**
	 * ɾ��������Դ
	 */
	public void deleteTestResource(String code){
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.deleteTestResource(code,false);
	}
	/**
	 * �ջ���Դ
	 */
	public void reback(byte[] bprojectCode) throws IOException{
		ProjectFileManager fileManager=new ProjectFileManager();
		String projectCode=(String)ArrayToObject(bprojectCode);
		fileManager.reback(projectCode,false);
	}
	/**
	 * ������Ŀ�ļ���
	 * @throws IOException 
	 */
	public void createFile(byte[] bprojectCode) throws IOException {
		ProjectFileManager fileManager=new ProjectFileManager();
		String projectCode=(String)ArrayToObject(bprojectCode);
		fileManager.createFile(projectCode,false);
	}
	/**
	 * �ϴ��ļ�
	 */
	public void upload(byte[] bprojectCode, byte[] bfileName, byte[] bdocName,
			byte[] buploadFile) throws IOException {
		String projectCode=(String)ArrayToObject(bprojectCode);
		String fileName=(String)ArrayToObject(bfileName);
		String docName=(String)ArrayToObject(bdocName);
		String tempFile=ProjectFileManager.rootPath.concat(UUID.randomUUID().toString().replace("-",""));
		File uploadFile=getFileFromBytes(buploadFile,tempFile);
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.upload(projectCode, fileName, docName, uploadFile, false);
	}
	/**
	 * ��2���������л���ļ�
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        FileOutputStream fstream=null;
        try {
            file = new File(outputFile);
            fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(fstream!=null){
            	try {
					fstream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        return file;
    }
/**
 * ɾ��һ����Ŀ�е�������Դ
 */
	public void deleteFile(byte[] projectCode) throws IOException {
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.deleteFile((String)ArrayToObject(projectCode), false);
	}
	/**
	 * ������һ���ļ�
	 */
	public boolean rename(byte[] bprojectCode,byte[] bfileName,byte[] bdocName,byte[] bnewName)throws IOException{
		ProjectFileManager fileManager=new ProjectFileManager();
		String projectCode=(String)ArrayToObject(bprojectCode);
		String fileName=(String)ArrayToObject(bfileName);
		String docName=(String)ArrayToObject(bdocName);
		String newName=(String)ArrayToObject(bnewName);
		return fileManager.rename(projectCode, fileName, docName, newName, false);
	}
	/**
	 * �༭��Դ
	 */
	public void editSource(byte[] bprojectCode,byte[] bfileName,byte[] bdocName,byte[] bcontent) throws IOException{
		String projectCode=(String)ArrayToObject(bprojectCode);
		String fileName=(String)ArrayToObject(bfileName);
		String docName=(String)ArrayToObject(bdocName);
		String content=(String)ArrayToObject(bcontent);
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.editSource(projectCode, fileName, docName, content, false);
	}
	/**
	 * �����ļ���·��ɾ��һ���ļ�
	 * @param bpath
	 * @throws IOException
	 */
	public  void deleteFileByPath(byte[] bpath) throws IOException{
		String path=(String)ArrayToObject(bpath);
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.deleteFileByPath(path, false);
	}
	/**
	 * ɾ���ĵ�
	 */
	public void deleteDoc(byte[] bprojectCode,byte[] bfileName,byte[] bdocName) throws IOException{
		String projectCode=(String)ArrayToObject(bprojectCode);
		String fileName=(String)ArrayToObject(bfileName);
		String docName=(String)ArrayToObject(bdocName);
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.deleteDoc(projectCode, fileName, docName, false);
	}
	/**
	 * �����ļ�
	 */
	public void createNewFile(byte[] bprojectCode,byte[] bfileName,byte[] bdocName) throws IOException{
		String projectCode=(String)ArrayToObject(bprojectCode);
		String fileName=(String)ArrayToObject(bfileName);
		String docName=(String)ArrayToObject(bdocName);
		ProjectFileManager fileManager=new ProjectFileManager();
		fileManager.createNewFile(projectCode, fileName, docName, false);
	}
	
	/****************************************�����������ļ������ķָ���*******************************************/
	/**
	 * ��ȡ������״̬
	 */
	public SynState getServerState(){
		SynState state=new SynState();
		state.setIp(SearchContext.getXdtechsite().getLocalip());
		state.setState(RuntimeDataCollect.getCrawl_status());
		state.setFileCrawl(NutchCommand.CRAWL_COMMAND_FILECRAWLER);
		state.setCrawl(NutchCommand.CRAWL_COMMAND_CRAWLER);
		return state;
	}
	/**
	 * ��ʼ�ɼ�
	 * @throws Exception 
	 */
	public void startCrawl(byte[] bflag) throws Exception {
		String flag=(String)ArrayToObject(bflag);
		CrawlAction crawl=new CrawlAction();
		if("1".equals(flag)){//���ݿ�ɼ�
			crawl.start(false);
		}else if("2".equals(flag)){//�ļ��ɼ�
			crawl.startFileCrawler(false);
		}
	}
	/**
	 * ֹͣ�ɼ�
	 * @throws Exception
	 */
	public void stopCrawl(byte[] bflag) throws Exception {
		String flag=(String)ArrayToObject(bflag);
		CrawlAction crawl=new CrawlAction();
		if("1".equals(flag)){//���ݿ�ɼ�ֹͣ
			crawl.stop(false);
		}else if("2".equals(flag)){//�ļ��ɼ�ֹͣ
			crawl.stopFileCrawler(false);
		}
	}
	public void outputPage(byte[] bfo,byte[] bcontent,byte[] btext,byte[] parseData) throws Exception{
		Content content=(Content)ArraysObjectTool.ArrayToObject(bcontent);
		IndexFetcher.indexpage(content);
		RuntimeDataCollect.setCrawl_page_num(1);
	}
	/**
	 * ͬ��������״̬
	 */
	public void setCrawl_status(byte[] bcrawl_status) throws IOException {
		String crawl_status=(String)ArrayToObject(bcrawl_status);
		if(!NutchCommand.CRAWL_COMMAND_CRAWLER){
			if(NutchCommand.CRAWL_STATUS_RUNNING.equals(crawl_status)){
				RuntimeDataCollect.setCrawl_page_num(0);
			    RuntimeDataCollect.setCrawl_parse_num(0);
			    RuntimeDataCollect.setCrawl_speed(0);
			    RuntimeDataCollect.setCrawl_times(0);
			}
        }
		RuntimeDataCollect.setCrawl_status(crawl_status,false);
		
	}
/**
 * ͬ������
 */
//	public void indexpage(byte[] bproperties,byte[] bdoc) throws Exception{
//		Document doc=(Document)ArraysObjectTool.ArrayToObject(bdoc);
//		Properties data=(Properties)ArraysObjectTool.ArrayToObject(bproperties);
//		IndexFetcher.indexpage(null, false, doc, data);
//		RuntimeDataCollect.setCrawl_page_num(1);
//	}
	boolean isMerger=false;
	/**
	 * �ύ
	 */
	public void commit() throws Exception{
		if(isMerger){
		}else{
			isMerger=true;
			Thread thread=new Thread(new Runnable(){
				public void run(){
					try {
						//IndexFetcher.commit(false,false);
						IndexFetcher.commit(false);
						isMerger=false;
					} catch (Exception e) {
						e.printStackTrace();
						isMerger=false;
					}
				}
			});
			thread.start();
		}
	}
	/**
	 * �������͵������ϲ�
	 * @throws Exception
	 */
	public void pushDataMerger() throws Exception{
		if(isMerger){
			
		}else{
			isMerger=true;
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						IndexFetcher.commit(true);
						
						RuntimeDataCollect.has_new_index = true;
						isMerger = false;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						isMerger = false;
					}
				}
			});
			t.start();
		}
	}
	/**
	 * wenserviceɾ������
	 * @throws IOException 
	 */
	public long deleteOneIndexByField(byte[] bfield, byte[] bvalue) throws IOException{
		String field=(String)ArrayToObject(bfield);
		String value=(String)ArrayToObject(bvalue);
		NutchBean bean=new NutchBean();
		return bean.deleteDocuments(field, value,false);
	}
	/**
	 * ���ȥ����Ϣ
	 * @throws Exception 
	 */
	public void addContent(byte[] bmd5Content) throws Exception{
		String md5Content=(String)ArrayToObject(bmd5Content);
		webDbAdminTool.addContents(md5Content);
	}
	/**
	 * �ڵ�ϵ�ʱ��û��
	 * ��������ݼ�������
	 * @param bDocumetnList
	 * @throws Exception
	 */
	public void addDocument(byte[] bDocumetnList) throws Exception{
		List<Document> documents=(List<Document>)ArrayToObject(bDocumetnList);
		if(documents!=null&&documents.size()>0){
			IndexWriter indexWriter=IndexFetcher.initIndexWriter(NutchFileSystem.get(), SearchContext.getXdtechsite().getSearchdir());
			for(Document doc:documents){
				indexWriter.addDocument(doc);
			}
			IndexFetcher.commit(true);
		}
	}

//	public void stop() throws Exception {
//		if(SearchContext.getXdtechsite().getSudis()){
//    		SearchContext.stopCrawl();
//    	}else{
//            if(NutchCommand.CRAWL_STATUS_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
//                RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_STOPPING,false);
//            }
//    	}
//          NutchCommand.setCrawl(false);
//	}
//
//	public void stopFileCrawler() throws Exception {
//	    if(NutchCommand.CRAWL_STATUS_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
//	        RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_STOPPING,false);
//	    }
//        NutchCommand.setCrawl(false);
//        NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
//	}
	public void test(byte[] bfile) throws Exception{
		System.out.println("�ļ���С��:"+bfile.length);
	}

	public void dataPro(IndexMessage im) throws Exception {
		FetcherDataPro fdp=new FetcherDataPro(null,null,null,im.getFlag());
		fdp.dataPro((Map<String,String>)im.getMap(),false);
	}

	public void filePro(IndexMessage im) throws Exception {
	}

	public void outputPage(byte[] bindexMessage) throws Exception {
	}

	public void outputPage(IndexMessage im) throws Exception {
	}
}
