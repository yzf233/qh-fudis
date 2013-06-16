package com.xx.platform.core.nutch;

import org.apache.nutch.db.IWebDBWriter;
import org.apache.nutch.tools.UpdateDatabaseTool;
import org.apache.nutch.util.NutchConf;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import org.apache.nutch.fs.NutchFileSystem;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.fetcher.IndexFetcher;
import org.apache.nutch.indexer.IndexMerger;
import org.apache.nutch.ipc.RPC;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.rpc.ClientInterface;
import com.xx.platform.core.rpc.DistributedTool;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.task.CrontabTimerTask;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.system.CrontabTaskAuto;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.system.ControlAction;
import com.xx.platform.web.listener.WebApplicationContextListener;

import org.apache.nutch.fetcher.FileFetcher;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

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
public class CrawlTool {
	boolean additionsAllowed = true;
	private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
	int max = -1;
	private int filefetchercount = //  �ļ��ɼ�ÿ�����ɼ���
	NutchConf.get().getInt("filefetcher.everytime.count", 500);
	private FetchListTool fetchList; // the input

	public CrawlTool() {
		//    	System.out.println("�ɼ��߳�����...........");
		Runnable run = new java.lang.Runnable() {
			public void run() {
				if(RuntimeDataCollect.diserver!=0)//��������ֲ�ʽ
//				{
//					DetachedCriteria detach=DetachedCriteria.forClass(Diserver.class);
//					detach.add(Expression.eq("status",true));
//					List<Diserver> diservers = SearchContext.getDao()
//							.findAllByCriteria(detach);
//					if(diservers==null||diservers.size()==0)
//					{
//						  ControlAction.addXdmessage("  ");
//						  ControlAction.addXdmessage("û���ܹ����ӵĽڵ�");
//						  NutchCommand.CRAWL_COMMAND_CRAWLER = false;
//						  NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
//						  RuntimeDataCollect
//							.setCrawl_status(NutchCommand.CRAWL_STATUS_NOT_RUNNING);
//						  return;
//					}
//					if (NutchCommand.CRAWL_COMMAND_CRAWLER) {
//						diservers = SearchContext.getDao().findAllByCriteria(detach);
//						for (Diserver d : diservers) {//����ÿ���ڵ�
//								  ControlAction.addXdmessage("֪ͨ�ڵ�" + d.getDname()
//											+ "��ʼ���ݿ�ɼ���");
//								  InetSocketAddress defaultAddresses = new InetSocketAddress(
//											d.getIpaddress(), d.getDismport());
//								  if(DistributedTool.testConnection(defaultAddresses))//������ӳɹ�
//								  {
//									  try {
//									  ((ClientInterface) RPC.getProxy(ClientInterface.class,defaultAddresses)).start_Db();// �ڵ����ݿ���������
//									  }
//									  catch (Exception e) {
//										e.printStackTrace();
//									  }
//										RuntimeDataCollect
//										.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
//								  }
//								  else
//								  {
//									  ControlAction.addXdmessage("�ڵ�" + d.getDname()
//												+ "����ʧ�ܣ�");
//								  }
//								  ControlAction.addXdmessage("  ");
//						}
//					}
//					else
//					{
//						diservers = SearchContext.getDao().findAllByCriteria(detach);
//						for (Diserver d : diservers) {//����ÿ���ڵ�
//								  ControlAction.addXdmessage("֪ͨ�ڵ�" + d.getDname()
//											+ "��ʼ�ļ��ɼ���");
//								  InetSocketAddress defaultAddresses = new InetSocketAddress(
//											d.getIpaddress(), d.getDismport());
//								  if(DistributedTool.testConnection(defaultAddresses))//������ӳɹ�
//								  {
//									  try {
//										  ((ClientInterface) RPC.getProxy(ClientInterface.class,defaultAddresses)).start_File();// �ڵ��ļ���������
//									  }
//									  catch (Exception e) {
//										e.printStackTrace();
//									  }
//								  }
//								  else
//								  {
//									  ControlAction.addXdmessage("�ڵ�" + d.getDname()
//												+ "����ʧ�ܣ�");
//								  }
//								  ControlAction.addXdmessage("  ");
//						}
//						  try {
//							  fileCrawl();
//							  RuntimeDataCollect.setCrawl_status(NutchCommand.
//			                            CRAWL_STATUS_STOPPING);
//							  }
//							  catch (Exception e) {
//									e.printStackTrace();
//							 }
//					}
//					while (true)// �ж�ÿ���ڵ��Ƿ��Ѿ�ֹͣ
//					{
//						if(!NutchCommand.isCrawl())
//						{
//							RuntimeDataCollect.setCrawl_status(NutchCommand.
//		                            CRAWL_STATUS_STOPPING);
//						}
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e1) {
//							// TODO Auto-generated catch block
////							e1.printStackTrace();
//						}
//						boolean hasrun = false;
//						diservers = SearchContext.getDao().findAllByCriteria(detach);
//						for (Diserver d : diservers) {
//								InetSocketAddress defaultAddresses = new InetSocketAddress(
//										d.getIpaddress(), d.getDismport());
//								if(DistributedTool.testConnection(defaultAddresses))//������ӳɹ�
//								{
//									String cawlstats = "";
//									try {
//										cawlstats = ((ClientInterface) RPC
//												.getProxy(ClientInterface.class,
//														defaultAddresses))
//												.getCrawlStatus();// �ڵ�״̬
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//									if (!cawlstats.equals(NutchCommand.CRAWL_STATUS_NOT_RUNNING))
//										hasrun = true;
//								}
//						}
//						if (!hasrun)
//							break;
//					}
//					taskend();
//					RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_NOT_RUNNING);
//				}
//				 else {
					try {
//						if (NutchCommand.CRAWL_COMMAND_FILECRAWLER) {
//							fileCrawl();
//							ControlAction.addXdmessage("�ļ��ɼ����������");
//							IbeaProperty.log.info("�ļ��ɼ����������...");
//							taskend();
//						}
						if (NutchCommand.CRAWL_COMMAND_CRAWLER) {
							crawl();
							ControlAction.addXdmessage("���ݿ�ɼ����������");
							IbeaProperty.log.info("���ݿ�ɼ����������...");
							RuntimeDataCollect.setCrawl_speed(0);
							taskend();
						}

						RuntimeDataCollect
								.setCrawl_status(NutchCommand.CRAWL_STATUS_NOT_RUNNING);
					} catch (Exception ex) {
						RuntimeDataCollect
						.setCrawl_status(NutchCommand.CRAWL_STATUS_NOT_RUNNING);
						ex.printStackTrace();
					}
				}
//			}
		};
		run.run();
		NutchCommand.CRAWL_COMMAND_CRAWLER = false;
		NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
	}
	/**
	 * @author hujun
	 * �ļ��������ݿ���������Ժ��ж�һ�������������ͨ���񣬻��Ƕ�ʱ��������Ƕ�ʱ������Ҫ�ѵ�ǰʱ�䶨Ϊ�������ʱ�䡣
	 */
    private void taskend()
    {
		if (CrontabTimerTask.runningtask != null
				&& CrontabTimerTask.runningtask.getTimes()
						.equals("more")
				&& CrontabTimerTask.runningtask
						.getRunning().equals("run")) {
			CrontabTimerTask.setTaskLastTimeById(
					CrontabTimerTask.runningtask.getId(),
					new Date());
			CrontabTimerTask.runningtask.setRunning("stop");
		}

		else if (CrontabTimerTask.runningtask != null
				&& CrontabTimerTask.runningtask.getTimes()
						.equals("one")
				&& CrontabTimerTask.runningtask
						.getRunning().equals("run")) {
			CrontabTimerTask.setTaskLastTimeById(
					CrontabTimerTask.runningtask.getId(),
					null);
			CrontabTimerTask.runningtask.setLasttime(null);
			CrontabTimerTask.runningtask = null;
		}
    }
	public void crawl() throws Exception {
		for (int i = 0; NutchCommand.isCrawl()
				&& (i < SearchContext.getXdtechsite().getDepths()); i++) {
			if (SearchContext.isShutDown) {
				break;
			}
			if(RuntimeDataCollect.isSynchroIsRunning()){
				//��������˷ֲ�ʽ�����нڵ�ϵ����򲻼���ִ��
				if(!ImDistributedTool.isReady){
					break;
				}
			}
			//            File segmentFile = FetchListTool.createSegment();
			RuntimeDataCollect
					.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
			/**
			 * ���濪ʼ����
			 * @param <any> getPath
			 */
			Fetcher.main(new String[] {
					"-threads",
					String
							.valueOf(SearchContext.getXdtechsite()
									.getCthreads() < 2000 ? SearchContext
									.getXdtechsite().getCthreads() : 2000),
					SearchContext.search_dir });
			//���� ����ʱ����״̬Ϊ Idle
			RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_IDLE);
			IndexFetcher.commit();
			/**
			 * �ϲ���������
			 * @param <any> getPath
			 */
			//            if((i+1)%20==0||(i==(SearchContext.getXdtechsite().getDepths()-1)))
			//                IndexMerger.main(new String[]{SearchContext.search_dir+File.separator+"index",segmentFile.getPath()}) ;
			//            File doneFile = new File(SearchContext.search_dir, "index.done") ;
			//            if(!doneFile.exists())
			{
				//                doneFile.createNewFile();
				/**
				 * ֪ͨ ��ѯ������ �� ���µ��������룬��Ҫ�������
				 */
				//                RuntimeDataCollect.has_new_index = true ;
				/**
				 * ����ҳ�������� 0
				 */
				RuntimeDataCollect.setCrawl_page_num(0);
			}
			//            b=i;
		}
	}

	public void updateWebDB(String segment) throws IOException {
		IWebDBWriter webdb = new WebDBWriter();
		UpdateDatabaseTool tool = new UpdateDatabaseTool(webdb,
				additionsAllowed, max);
		/**
		 * �����ļ�ϵͳ
		 */
		NutchFileSystem nfs = NutchFileSystem.parseArgs(
				new String[] { "-local" }, 0);
		tool.updateForSegment(nfs, segment);
		try {
			webDbAdminTool.close();
			webDbAdminTool.reload();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	public void fileCrawl() throws Exception {
//		//    	System.out.println("��ʼ�ɼ��ļ�......");
//		//    	ȡ���ļ��ɼ�����
//		//    	���ļ����������ļ��ɼ�����
//		if (filefetchercount <= 0) {
//			ControlAction.addXdmessage("�ļ�ÿ�ֲɼ��������÷Ƿ����ļ��ɼ�ʧ�ܣ�");
//			return;
//		}
//		fetchList = new FetchListTool(true);
//		int count = fetchList.getDbList().size() + fetchList.getPdfurl().size();
//		int time = count % filefetchercount == 0 ? count / filefetchercount
//				: count / filefetchercount + 1;
//		//System.out.println("pdf:"+fetchList.getPdfurl().size()+" ������"+fetchList.getDbList().size());
//		//System.out.println("�ļ�����"+count+"  �ɼ�������"+time+"  ÿ�ֲɼ���"+filefetchercount);
//		//    for(int i=0 ; NutchCommand.isCrawl() && ( i<SearchContext.getXdtechsite().getDepths()) ; i++)
//		for (int i = 0; NutchCommand.isCrawl()
//				&& i < time
//				&& (fetchList.getDbList().size() > 0 || fetchList.getPdfurl()
//						.size() > 0); i++) {
//			if (SearchContext.isShutDown) {
//				break;
//			}
//			if(RuntimeDataCollect.isSynchroIsRunning()){
//				//��������˷ֲ�ʽ�����нڵ�ϵ����򲻼���ִ��
//				if(!ImDistributedTool.isReady){
//					break;
//				}
//			}
//			RuntimeDataCollect
//					.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
//			/**
//			 * ���濪ʼ����
//			 * @param <any> getPath
//			 */
//			FileFetcher
//					.main(new String[] {
//							"-threads",
//							String
//									.valueOf(BaseAction.fileCrawlerCount < 2000 ? BaseAction.fileCrawlerCount
//											: 2000), SearchContext.search_dir });
//			//���� ����ʱ����״̬Ϊ Idle
//			RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_IDLE);
//			/**
//			 * �ϲ���������
//			 * @param <any> getPath
//			 */
//			//	        IndexMerger.main(new String[]{SearchContext.search_dir+File.separator+"index",segmentFile.getPath()}) ;
//			//            File doneFile = new File(SearchContext.search_dir, "index.done") ;
//			//            if(!doneFile.exists())
//			{
//				//                doneFile.createNewFile();
//				/**
//				 * ֪ͨ ��ѯ������ �� ���µ��������룬��Ҫ�������
//				 */
//				IndexFetcher.commit();
//				//	            RuntimeDataCollect.has_new_index = true ;
//				/**
//				 * ����ҳ�������� 0
//				 */
//				RuntimeDataCollect.setFileCrawl_page_num(0);
//			}
//		}
//		fetchList.setIsfirst(true);
//		fetchList.getPdfurl().clear();
//		fetchList.getDbList().clear();
//		//    System.out.println("�ļ��ɼ����........."+RuntimeDataCollect.getCrawl_status());
//	}

}
