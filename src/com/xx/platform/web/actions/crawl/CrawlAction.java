package com.xx.platform.web.actions.crawl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.nutch.ipc.RPC;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.dao.GeneraDAO;
import com.xx.platform.domain.model.distributed.SynState;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.system.ControlAction;

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
public class CrawlAction extends BaseAction{
	public String start()throws Exception{
		return start(true);
	}
    public String start(boolean command) throws Exception
    {
        if(NutchCommand.CRAWL_COMMAND_FILECRAWLER)
        {
         return Action.SUCCESS ;
        }
        if(RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Running")||RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Stopping")||RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Idle"))
        {//有任务正在运行
        	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
        	 NutchCommand.setCrawl(true);
             NutchCommand.CRAWL_COMMAND_CRAWLER = true;
        	  return Action.SUCCESS ;
        }
        
        
        if(SearchContext.getDao()==null)
        {
            SearchContext.setDao((GeneraDAO)wac.getBean(IbeaProperty.DAO_NAME_SPACE));
        }
        
 
        System.out.println("开始执行爬虫任务！");
        RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
        RuntimeDataCollect.setCrawl_page_num(0);
        RuntimeDataCollect.setCrawl_parse_num(0);
        RuntimeDataCollect.setCrawl_speed(0);
        RuntimeDataCollect.setCrawl_times(0);
//        if(SearchContext.getXdtechsite().getSudis()){
//        	  ControlAction.addXdmessage("开始数据库采集服务\r\n");
//              NutchCommand.setCrawl(true);
//              NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//            //SearchContext.startCrawl();暂时先注释掉
//            //NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//        }else{
        
        if(RuntimeDataCollect.diserver!=0)//如果启动分布式
        ControlAction.addXdmessage("开始通知所有节点启动分布式数据库采集。\r\n");
        else
        ControlAction.addXdmessage("开始数据库采集服务\r\n");
        	  FetchListTool.pernum=SearchContext.getXdtechsite().getDbnumber();
        	  RuntimeDataCollect.setCrawl_database_start_time(new Date());
            NutchCommand.setCrawl(true);
            NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//        }
     
            if(command){//通知集群中的其他节点开始采集
          	  List<Synchro> synchro = SearchContext.getSynchroList();
          		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
          				&& synchro.size() > 0){
          			StringBuilder info=new StringBuilder();
          			for (Synchro s : synchro) {// 遍历每个节点
          				try {
          					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
          					SynState state=imInterface.getServerState();
          					boolean siFileCrawl=false;
          					siFileCrawl=state.isFileCrawl();
          					if(siFileCrawl){
          						//节点有任务在运行
          						info.setLength(0);
          						info.append("节点").append(s.getDname()).append("有任务在运行，运行任务为：");
          						info.append("文件爬行任务....");
          					}else{
          						imInterface.startCrawl(ArraysObjectTool.ObjectToArrays("1"));
          						System.out.println("节点"+s.getDname()+"已经开始采集");
          					}
          				} catch (IOException e) {
          					e.printStackTrace();
          				}
          			}
          		}
            }
        Thread.sleep(3000);
        return Action.SUCCESS ;
    }
    /**
     * 停止爬虫
     * @return String
     * @throws Exception
     */
    public String stop() throws Exception{
    	return stop(true);
    }
    public String stop(boolean command) throws Exception
    {
    	if(SearchContext.getXdtechsite().getSudis())
    	{
    		SearchContext.stopCrawl();
    	}
    	else
    	{
            if(NutchCommand.CRAWL_STATUS_RUNNING.equals(RuntimeDataCollect.getCrawl_status()))
            {
                RuntimeDataCollect.setCrawl_status(NutchCommand.
                                                   CRAWL_STATUS_STOPPING);
            }
    	}
          NutchCommand.setCrawl(false);
          if(command){
        	  List<Synchro> synchro = SearchContext.getSynchroList();
        		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
        				&& synchro.size() > 0){
        			for (Synchro s : synchro) {// 遍历每个节点
        				try {
        					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
        					imInterface.stopCrawl(ArraysObjectTool.ObjectToArrays("1"));
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        		}
          }
        return Action.SUCCESS ;
    }
    public String startFileCrawler() throws Exception{
    	return startFileCrawler(true);
    }
    public String startFileCrawler(boolean command) throws Exception{
    	
        if(NutchCommand.CRAWL_COMMAND_CRAWLER)
        {
            return Action.SUCCESS ;
        }
      
    if(SearchContext.getDao()==null)
    {
        SearchContext.setDao((GeneraDAO)wac.getBean(IbeaProperty.DAO_NAME_SPACE));
    }
    RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
    RuntimeDataCollect.setCrawl_page_num(0);
    RuntimeDataCollect.setCrawl_parse_num(0);
    RuntimeDataCollect.setCrawl_speed(0);
    RuntimeDataCollect.setCrawl_times(0);
//    if(SearchContext.getXdtechsite().getSudis()){
//    	  ControlAction.addXdmessage("开始文件采集服务\r\n");
//          NutchCommand.setCrawl(true);
//          NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//     //SearchContext.startCrawl();
//      //NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//    }else{
          if(RuntimeDataCollect.diserver!=0)//如果启动分布式
              ControlAction.addXdmessage("开始通知所有节点启动分布式文件采集。\r\n");
              else
              ControlAction.addXdmessage("开始文件采集服务\r\n");
    	  RuntimeDataCollect.setCrawl_file_start_time(new Date());
        NutchCommand.setCrawl(true);
        NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//    }
        
        if(command){//通知集群中的其他节点开始采集
        	  List<Synchro> synchro = SearchContext.getSynchroList();
        		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
        				&& synchro.size() > 0){
        			StringBuilder info=new StringBuilder();
        			for (Synchro s : synchro) {// 遍历每个节点
        				try {
        					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
        					SynState state=imInterface.getServerState();
        					if(state.isCrawl()||state.isFileCrawl()){
          						//节点有任务在运行
          						info.setLength(0);
          						info.append("节点").append(s.getDname()).append("有任务在运行，运行任务为：");
          						if(state.isCrawl()){
          							info.append("数据库爬行任务....");
          						}else if(state.isFileCrawl()){
          							info.append("文件爬行任务....");
          						}
          					}else{
          						imInterface.startCrawl(ArraysObjectTool.ObjectToArrays("2"));
          						System.out.println("其他节点开始文件采集！");
          					}
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        		}
          }
    Thread.sleep(3000);
//    System.out.println("状态："+RuntimeDataCollect.getCrawl_status());
    return Action.SUCCESS ;
}
/**
 * 停止爬虫
 * @return String
 * @throws Exception
 */
public String stopFileCrawler() throws Exception{
	return stopFileCrawler(true);
}
public String stopFileCrawler(boolean command) throws Exception{

    if(NutchCommand.CRAWL_STATUS_RUNNING.equals(RuntimeDataCollect.getCrawl_status()))
    {
        RuntimeDataCollect.setCrawl_status(NutchCommand.
                                           CRAWL_STATUS_STOPPING);
    }
//    if(SearchContext.getXdtechsite().getSudis())
//      SearchContext.stopCrawl();
//    else{
        NutchCommand.setCrawl(false);
        NutchCommand.CRAWL_COMMAND_FILECRAWLER = false;
//    }
        if(command){
        	List<Synchro> synchro = SearchContext.getSynchroList();
    		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
    				&& synchro.size() > 0){
    			for (Synchro s : synchro) {// 遍历每个节点
    				try {
    					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
    					imInterface.stopCrawl(ArraysObjectTool.ObjectToArrays("2"));
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
        }
    return Action.SUCCESS ;
}


}
