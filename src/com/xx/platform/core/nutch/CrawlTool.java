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
	private int filefetchercount = //  文件采集每轮最大采集数
	NutchConf.get().getInt("filefetcher.everytime.count", 500);
	private FetchListTool fetchList; // the input

	public CrawlTool() {
		//    	System.out.println("采集线程启动...........");
		Runnable run = new java.lang.Runnable() {
			public void run() {
				if(RuntimeDataCollect.diserver!=0)//如果启动分布式
//				{
//					DetachedCriteria detach=DetachedCriteria.forClass(Diserver.class);
//					detach.add(Expression.eq("status",true));
//					List<Diserver> diservers = SearchContext.getDao()
//							.findAllByCriteria(detach);
//					if(diservers==null||diservers.size()==0)
//					{
//						  ControlAction.addXdmessage("  ");
//						  ControlAction.addXdmessage("没有能够连接的节点");
//						  NutchCommand.CRAWL_COMMAND_CRAWLER = false;
//						  NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
//						  RuntimeDataCollect
//							.setCrawl_status(NutchCommand.CRAWL_STATUS_NOT_RUNNING);
//						  return;
//					}
//					if (NutchCommand.CRAWL_COMMAND_CRAWLER) {
//						diservers = SearchContext.getDao().findAllByCriteria(detach);
//						for (Diserver d : diservers) {//遍历每个节点
//								  ControlAction.addXdmessage("通知节点" + d.getDname()
//											+ "开始数据库采集。");
//								  InetSocketAddress defaultAddresses = new InetSocketAddress(
//											d.getIpaddress(), d.getDismport());
//								  if(DistributedTool.testConnection(defaultAddresses))//如果连接成功
//								  {
//									  try {
//									  ((ClientInterface) RPC.getProxy(ClientInterface.class,defaultAddresses)).start_Db();// 节点数据库启动爬虫
//									  }
//									  catch (Exception e) {
//										e.printStackTrace();
//									  }
//										RuntimeDataCollect
//										.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
//								  }
//								  else
//								  {
//									  ControlAction.addXdmessage("节点" + d.getDname()
//												+ "连接失败！");
//								  }
//								  ControlAction.addXdmessage("  ");
//						}
//					}
//					else
//					{
//						diservers = SearchContext.getDao().findAllByCriteria(detach);
//						for (Diserver d : diservers) {//遍历每个节点
//								  ControlAction.addXdmessage("通知节点" + d.getDname()
//											+ "开始文件采集。");
//								  InetSocketAddress defaultAddresses = new InetSocketAddress(
//											d.getIpaddress(), d.getDismport());
//								  if(DistributedTool.testConnection(defaultAddresses))//如果连接成功
//								  {
//									  try {
//										  ((ClientInterface) RPC.getProxy(ClientInterface.class,defaultAddresses)).start_File();// 节点文件启动爬虫
//									  }
//									  catch (Exception e) {
//										e.printStackTrace();
//									  }
//								  }
//								  else
//								  {
//									  ControlAction.addXdmessage("节点" + d.getDname()
//												+ "连接失败！");
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
//					while (true)// 判断每个节点是否已经停止
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
//								if(DistributedTool.testConnection(defaultAddresses))//如果连接成功
//								{
//									String cawlstats = "";
//									try {
//										cawlstats = ((ClientInterface) RPC
//												.getProxy(ClientInterface.class,
//														defaultAddresses))
//												.getCrawlStatus();// 节点状态
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
//							ControlAction.addXdmessage("文件采集服务已完成");
//							IbeaProperty.log.info("文件采集服务已完成...");
//							taskend();
//						}
						if (NutchCommand.CRAWL_COMMAND_CRAWLER) {
							crawl();
							ControlAction.addXdmessage("数据库采集服务已完成");
							IbeaProperty.log.info("数据库采集服务已完成...");
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
	 * 文件或者数据库任务结束以后，判断一下这个任务是普通任务，还是定时任务，如果是定时任务，需要把当前时间定为任务结束时间。
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
				//如果启动了分布式，且有节点断掉，则不继续执行
				if(!ImDistributedTool.isReady){
					break;
				}
			}
			//            File segmentFile = FetchListTool.createSegment();
			RuntimeDataCollect
					.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
			/**
			 * 爬虫开始爬行
			 * @param <any> getPath
			 */
			Fetcher.main(new String[] {
					"-threads",
					String
							.valueOf(SearchContext.getXdtechsite()
									.getCthreads() < 2000 ? SearchContext
									.getXdtechsite().getCthreads() : 2000),
					SearchContext.search_dir });
			//设置 运行时爬虫状态为 Idle
			RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_IDLE);
			IndexFetcher.commit();
			/**
			 * 合并索引内容
			 * @param <any> getPath
			 */
			//            if((i+1)%20==0||(i==(SearchContext.getXdtechsite().getDepths()-1)))
			//                IndexMerger.main(new String[]{SearchContext.search_dir+File.separator+"index",segmentFile.getPath()}) ;
			//            File doneFile = new File(SearchContext.search_dir, "index.done") ;
			//            if(!doneFile.exists())
			{
				//                doneFile.createNewFile();
				/**
				 * 通知 查询分析器 ， 有新的索引加入，需要清除缓存
				 */
				//                RuntimeDataCollect.has_new_index = true ;
				/**
				 * 爬行页面数量置 0
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
		 * 本地文件系统
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
//		//    	System.out.println("开始采集文件......");
//		//    	取消文件采集轮数
//		//    	按文件数量决定文件采集轮数
//		if (filefetchercount <= 0) {
//			ControlAction.addXdmessage("文件每轮采集数量设置非法，文件采集失败！");
//			return;
//		}
//		fetchList = new FetchListTool(true);
//		int count = fetchList.getDbList().size() + fetchList.getPdfurl().size();
//		int time = count % filefetchercount == 0 ? count / filefetchercount
//				: count / filefetchercount + 1;
//		//System.out.println("pdf:"+fetchList.getPdfurl().size()+" 其它："+fetchList.getDbList().size());
//		//System.out.println("文件数："+count+"  采集轮数："+time+"  每轮采集："+filefetchercount);
//		//    for(int i=0 ; NutchCommand.isCrawl() && ( i<SearchContext.getXdtechsite().getDepths()) ; i++)
//		for (int i = 0; NutchCommand.isCrawl()
//				&& i < time
//				&& (fetchList.getDbList().size() > 0 || fetchList.getPdfurl()
//						.size() > 0); i++) {
//			if (SearchContext.isShutDown) {
//				break;
//			}
//			if(RuntimeDataCollect.isSynchroIsRunning()){
//				//如果启动了分布式，且有节点断掉，则不继续执行
//				if(!ImDistributedTool.isReady){
//					break;
//				}
//			}
//			RuntimeDataCollect
//					.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
//			/**
//			 * 爬虫开始爬行
//			 * @param <any> getPath
//			 */
//			FileFetcher
//					.main(new String[] {
//							"-threads",
//							String
//									.valueOf(BaseAction.fileCrawlerCount < 2000 ? BaseAction.fileCrawlerCount
//											: 2000), SearchContext.search_dir });
//			//设置 运行时爬虫状态为 Idle
//			RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_IDLE);
//			/**
//			 * 合并索引内容
//			 * @param <any> getPath
//			 */
//			//	        IndexMerger.main(new String[]{SearchContext.search_dir+File.separator+"index",segmentFile.getPath()}) ;
//			//            File doneFile = new File(SearchContext.search_dir, "index.done") ;
//			//            if(!doneFile.exists())
//			{
//				//                doneFile.createNewFile();
//				/**
//				 * 通知 查询分析器 ， 有新的索引加入，需要清除缓存
//				 */
//				IndexFetcher.commit();
//				//	            RuntimeDataCollect.has_new_index = true ;
//				/**
//				 * 爬行页面数量置 0
//				 */
//				RuntimeDataCollect.setFileCrawl_page_num(0);
//			}
//		}
//		fetchList.setIsfirst(true);
//		fetchList.getPdfurl().clear();
//		fetchList.getDbList().clear();
//		//    System.out.println("文件采集完成........."+RuntimeDataCollect.getCrawl_status());
//	}

}
