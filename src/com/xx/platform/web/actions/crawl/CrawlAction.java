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
        {//��������������
        	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
        	 NutchCommand.setCrawl(true);
             NutchCommand.CRAWL_COMMAND_CRAWLER = true;
        	  return Action.SUCCESS ;
        }
        
        
        if(SearchContext.getDao()==null)
        {
            SearchContext.setDao((GeneraDAO)wac.getBean(IbeaProperty.DAO_NAME_SPACE));
        }
        
 
        System.out.println("��ʼִ����������");
        RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
        RuntimeDataCollect.setCrawl_page_num(0);
        RuntimeDataCollect.setCrawl_parse_num(0);
        RuntimeDataCollect.setCrawl_speed(0);
        RuntimeDataCollect.setCrawl_times(0);
//        if(SearchContext.getXdtechsite().getSudis()){
//        	  ControlAction.addXdmessage("��ʼ���ݿ�ɼ�����\r\n");
//              NutchCommand.setCrawl(true);
//              NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//            //SearchContext.startCrawl();��ʱ��ע�͵�
//            //NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//        }else{
        
        if(RuntimeDataCollect.diserver!=0)//��������ֲ�ʽ
        ControlAction.addXdmessage("��ʼ֪ͨ���нڵ������ֲ�ʽ���ݿ�ɼ���\r\n");
        else
        ControlAction.addXdmessage("��ʼ���ݿ�ɼ�����\r\n");
        	  FetchListTool.pernum=SearchContext.getXdtechsite().getDbnumber();
        	  RuntimeDataCollect.setCrawl_database_start_time(new Date());
            NutchCommand.setCrawl(true);
            NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//        }
     
            if(command){//֪ͨ��Ⱥ�е������ڵ㿪ʼ�ɼ�
          	  List<Synchro> synchro = SearchContext.getSynchroList();
          		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
          				&& synchro.size() > 0){
          			StringBuilder info=new StringBuilder();
          			for (Synchro s : synchro) {// ����ÿ���ڵ�
          				try {
          					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
          					SynState state=imInterface.getServerState();
          					boolean siFileCrawl=false;
          					siFileCrawl=state.isFileCrawl();
          					if(siFileCrawl){
          						//�ڵ�������������
          						info.setLength(0);
          						info.append("�ڵ�").append(s.getDname()).append("�����������У���������Ϊ��");
          						info.append("�ļ���������....");
          					}else{
          						imInterface.startCrawl(ArraysObjectTool.ObjectToArrays("1"));
          						System.out.println("�ڵ�"+s.getDname()+"�Ѿ���ʼ�ɼ�");
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
     * ֹͣ����
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
        			for (Synchro s : synchro) {// ����ÿ���ڵ�
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
//    	  ControlAction.addXdmessage("��ʼ�ļ��ɼ�����\r\n");
//          NutchCommand.setCrawl(true);
//          NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//     //SearchContext.startCrawl();
//      //NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//    }else{
          if(RuntimeDataCollect.diserver!=0)//��������ֲ�ʽ
              ControlAction.addXdmessage("��ʼ֪ͨ���нڵ������ֲ�ʽ�ļ��ɼ���\r\n");
              else
              ControlAction.addXdmessage("��ʼ�ļ��ɼ�����\r\n");
    	  RuntimeDataCollect.setCrawl_file_start_time(new Date());
        NutchCommand.setCrawl(true);
        NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//    }
        
        if(command){//֪ͨ��Ⱥ�е������ڵ㿪ʼ�ɼ�
        	  List<Synchro> synchro = SearchContext.getSynchroList();
        		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
        				&& synchro.size() > 0){
        			StringBuilder info=new StringBuilder();
        			for (Synchro s : synchro) {// ����ÿ���ڵ�
        				try {
        					ImInterface imInterface=(ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut);
        					SynState state=imInterface.getServerState();
        					if(state.isCrawl()||state.isFileCrawl()){
          						//�ڵ�������������
          						info.setLength(0);
          						info.append("�ڵ�").append(s.getDname()).append("�����������У���������Ϊ��");
          						if(state.isCrawl()){
          							info.append("���ݿ���������....");
          						}else if(state.isFileCrawl()){
          							info.append("�ļ���������....");
          						}
          					}else{
          						imInterface.startCrawl(ArraysObjectTool.ObjectToArrays("2"));
          						System.out.println("�����ڵ㿪ʼ�ļ��ɼ���");
          					}
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        			}
        		}
          }
    Thread.sleep(3000);
//    System.out.println("״̬��"+RuntimeDataCollect.getCrawl_status());
    return Action.SUCCESS ;
}
/**
 * ֹͣ����
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
    			for (Synchro s : synchro) {// ����ÿ���ڵ�
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
