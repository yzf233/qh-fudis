package com.xx.platform.web.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.nutch.analysis.NutchDocumentAnalyzer;
import org.apache.nutch.fetcher.KeepLastIndexDeletionPolicy;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.PYContext;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.analyzer.XDChineseTokenizer;
import com.xx.platform.core.db.BerkeleyDB;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.DistributedTool;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.StartPortListener;
import com.xx.platform.core.task.CheckHasNode;
import com.xx.platform.core.task.TempDataTask;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.system.CrontabTaskAuto;
import com.xx.platform.util.tools.zhconverter.ZHConverter;

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
public class WebApplicationContextListener implements HttpSessionListener,
        ServletContextListener, ServletContextAttributeListener {
	public static final Logger LOG = LogFormatter
	.getLogger("com.xx.platform.web.listener.WebApplicationContextListener");
    public void sessionCreated(HttpSessionEvent se) {
    }

    public void sessionDestroyed(HttpSessionEvent se) {
    }

    public void contextInitialized(ServletContextEvent sce) {

        if(!SearchContext.logFileDir.exists())
        {
            SearchContext.logFileDir.mkdir() ;
        }
        ServletContext  context = sce.getServletContext() ;
        SearchContext.setDao(SearchContext.getDataHandler(context));
        List<CrontabTaskAuto> tasklist=SearchContext.getDao().findAllByIObjectCType(CrontabTaskAuto.class);
        for(CrontabTaskAuto task:tasklist)
        {
        	task.setState("0");
        	SearchContext.getDao().updateIObject(task);
        }
        List<Diserver> diservers =SearchContext.getDao().findAllByIObjectCType(Diserver.class);
        SearchContext.initProject();//��ʼ����Ŀ
        SearchContext.reloadGuide();//��ʼ������
        for(Diserver dis:diservers)
        {
        	dis.setStatus(false);
        	dis.setServerstatus(false);
        	SearchContext.getDao().updateIObject(dis);
        	SearchContext.reloadDiserverList();
        }
        SearchContext.getSynonymyMap();
        System.setProperty("XDTECHLicensePath", sce.getServletContext().getRealPath("/WEB-INF/classes/")) ;
        com.xdtech.platform.util.tools.a a = new com.xdtech.platform.util.tools.a() ;
        DistributedTool disTool = new DistributedTool();//�����ڵ��������ж�
        
//        SearchContext.CONTROL.setDisLicense(a.a(false, "1"));
//        SearchContext.CONTROL.setCluster(a.a(false,"1","1"));
//        SearchContext.CONTROL.setClusterNumer(a.a(false, "1","1","1","1"));
//        if(SearchContext.CONTROL.isDisLicense()){
//            SearchContext.CONTROL.setDisNumer(a.a(false, "1","1","1"));
//            SearchContext.CONTROL.setTogether(a.a(false, "1","1","1","1","1"));
//            SearchContext.CONTROL.setMaxKnot(a.a(false, "1","1","1","1","1","1"));
//        }else{
//            SearchContext.CONTROL.setDisNumer(0);
//            SearchContext.CONTROL.setTogether(false);
//            SearchContext.CONTROL.setMaxKnot(1);
//        }
//        SearchContext.CONTROL.setKind(a.a(false, "1","1","1","1","1","1","1"));
        XDChineseTokenizer token = new XDChineseTokenizer(null);
        if(SearchContext.getXdtechsite().getSudis())
        {
          SearchContext.IS_SEARCH_SERVER = true ;
          DistributedTool.startServer();
        }
        String realPath=context.getRealPath("/");
        SearchContext.realPath=realPath;
        SearchContext.contextPath=realPath+"WEB-INF"+File.separator;
        SearchContext.tempDataPath=SearchContext.contextPath.concat("tempdata");
        System.out.println(SearchContext.CONTROL);
        //�鿴��Ȩ�ļ������Ƿ���ȷ
        checkKind();
        //����Ƿ�ͬһ̨�����Ͽ����нڵ�����
        boolean isTogether=SearchContext.CONTROL.isTogether();
        if(!isTogether){
        	checkHasNode();
            Timer timer=new Timer(true);
            timer.scheduleAtFixedRate(new CheckHasNode(), 6000,6000);
       }
        //�����÷ֲ�ʽ��Ȩ��������,����˿ڿ����ڽڵ��жϵ����Ƿ���
        if(SearchContext.CONTROL.isDisLicense()){
        	Thread thread=new Thread(new StartPortListener());
            thread.setDaemon(true);
            thread.start();
        }
        
        boolean isPy=NutchConf.get().getBoolean("search.use.pinYin",false);
        if(isPy){
        	PYContext py=new PYContext();
        }
        
      //��������
        doFlushDelDoc();
        if("1".equals(SearchContext.getXdtechsite().getIssyn())){
        	ImDistributedTool.startServer();//������Ⱥ����
        	RuntimeDataCollect.setSynchroIsRunning(true);
        }
        //�ѽڵ�ϵ�֮������ݷ��͵��ڵ�ȥ
        Timer pushDateTimer=new Timer("not send data Timer",true);
        pushDateTimer.scheduleAtFixedRate(new TempDataTask(), 6000, 6000);
        
        /*
         * ���شʿ�
         */
        ZHConverter ZHConverter=new ZHConverter();
    }
    public void checkHasNode(){
		PrintWriter writer=null;
    	Socket socket=null;
    	try {
			socket=new Socket("127.0.0.1",SearchContext.CHECK_LOCALE_COUNT_PORT);
			socket.setSoTimeout(3000);
			BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream());
			writer.println("XDCHECK");
			writer.flush();
			String back=in.readLine();
			if(back!=null&&back.startsWith(SearchContext.socketInfoPre)){
				boolean hasNode=socket.isConnected();
				boolean isTogether=SearchContext.CONTROL.isTogether();
				if(!isTogether&&hasNode){
					LOG.info("�ڵ�����ȳ�������ͬһ̨�������ϣ�ϵͳ����10���رգ�");
					try {
						for(int i=0;i<10;i++){
							System.out.print("........"+(10-i));
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
					}
					if(SearchContext.server8432!=null){
						if(!SearchContext.server8432.isClosed()){
							SearchContext.server8432.close();
						}
					}
					System.exit(0);
				}
			}
    	} catch (UnknownHostException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}finally{
			if(writer!=null){
				writer.close();
			}
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
    }
    /**
     * ����������
     * @throws IOException 
     */
    private void checkKind(){
    	if(SearchContext.CONTROL.getKind()==2){
        	LOG.info("���ȳ���ʹ���˽ڵ�����License��ϵͳ����10���رգ�");
			try {
				for(int i=0;i<10;i++){
					System.out.print("........"+(10-i));
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
			}
			if(SearchContext.server8432!=null){
				if(!SearchContext.server8432.isClosed()){
					try {
						SearchContext.server8432.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			System.exit(0);
		}
    }
	private void doFlushDelDoc() {
		// TODO Auto-generated method stub  
    	LOG.info("��ʼ������������......");
    	
    	IndexReader reader = null ;
        try {
        	List<String> delList = BerkeleyDB.getDelDocQuery();
        	File file = new File(SearchContext.search_dir + File.separator
					+ "index");
			if (!file.exists())
			{
				LOG.info("û����������");
				SearchContext.isInit=true;
				return;
			}
			Directory directory = FSDirectory.getDirectory(file, false);
			reader = IndexReader.open(directory);	
			if (reader.isLocked(SearchContext.search_dir + File.separator
					+ "index")) {
				LOG.info("����Ŀ¼��������������JVM�쳣�˳����µģ���ʼ���Խ����");
				reader.unlock(FSDirectory.getDirectory(SearchContext.search_dir + File.separator
						+ "index"));
				if (!reader.isLocked(SearchContext.search_dir + File.separator
						+ "index"))
					LOG.info("����Ŀ¼�����ɹ�");
				else {
					LOG.info("����Ŀ¼����ʧ��,��Ҫ�ֶ�ɾ�����ļ�");
					NutchCommand.setCrawl(false) ;
					throw new IOException("����Ŀ¼����ʧ��,���ֶ�ɾ�����ļ�");
				}
			}
        	if(delList!=null){  
				for(String ls:delList)
				{
					reader.deleteDocument(Integer.valueOf(ls));
				}			
				LOG.info("���������������");
        	}
        } catch (Exception e) {
        	LOG.info("������������ʧ�ܣ�"+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(reader!=null)
				{
					boolean isOp = !reader.isOptimized() ;
					reader.close() ;
					if(isOp){
						optimize();
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SearchContext.isInit=true;
	}
	/**
	 * 
	 * @throws Exception
	 */
	public static void optimize() throws Exception {
			IndexWriter iw = null;
			File file = new File(SearchContext.search_dir + File.separator
					+ "index");
			Directory directory = FSDirectory.getDirectory(file, false);
			IndexDeletionPolicy policy=new KeepLastIndexDeletionPolicy();
	          iw = new IndexWriter(directory, new NutchDocumentAnalyzer(),
	                  false, policy, MaxFieldLength.UNLIMITED);
	          iw.setUseCompoundFile(true);
	          iw.setTermIndexInterval(128);
	          iw.optimize(false);
	          iw.close();
	}
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			BerkeleyDB.close();
		} catch (IOException e1) {
		}
		if(SearchContext.server8432!=null&&!SearchContext.server8432.isClosed()){
			try {
				SearchContext.server8432.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			SearchContext.server8432=null;
		}
    }

    public void attributeAdded(ServletContextAttributeEvent scab) {
    }

    public void attributeRemoved(ServletContextAttributeEvent scab) {
    }

    public void attributeReplaced(ServletContextAttributeEvent scab) {
    }

}
