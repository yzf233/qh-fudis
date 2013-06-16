package com.xx.platform.web.actions.system;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.nutch.ipc.RPC;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.Control;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.ClientInterface;
import com.xx.platform.core.rpc.DistributedTool;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.util.tools.SLOG;
import com.xx.platform.util.tools.XDLOG;
import com.xx.platform.web.actions.BaseAction;

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
public class ControlAction extends BaseAction{
    private RuntimeDataCollect runtime ;
    private String dir ;
    private String message ;
   // public static String xdmessage="";
    public static ArrayList xdmessagelist=new ArrayList();
    public static String xdmessage="";
    private String indexmessage="";
    
    public static synchronized void addXdmessage(String str)
    {
    	if(RuntimeDataCollect.diserver!=0)//如果启动分布式
		{
    		str="【分布式信息】"+str;
		}
    	    xdmessage="";
    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		xdmessagelist.add(0, dateFormat.format(new Date())+"："+str+"\r\n");
    		SLOG.addMessage(str);
    		while(xdmessagelist.size()>100)
    		{
    			xdmessagelist.remove(xdmessagelist.size()-1);
    		}
    		
    		for(int i=0;i<xdmessagelist.size();i++)
    		{
    			xdmessage+=xdmessagelist.get(i);
    		}
    		
    		XDLOG.out(dateFormat.format(new Date())+"："+str);
    }
    public String console()
    {
        runtime = new RuntimeDataCollect() ;
//        if(SearchContext.getXdtechsite().getSudis()!=null && SearchContext.getXdtechsite().getSudis())
//          DistributedTool.getCrawlStatus();
        runtime.setCrawl_times(new Date().getTime()-runtime.getCrawl_start_time().getTime());
        if(runtime.getCrawl_times()<1000*30)
        {
            runtime.setCrawl_times(1000*30);
        }
        if (RuntimeDataCollect.diserver != 0)// 如果启动分布式，则统计节点的page数据，然后根据节点数据计算速度，索引文件总数，索引文件总体大小。
		{
        	try
        	{
			List<Diserver> diservers = service
					.findAllByIObjectCType(Diserver.class);
			long maxCrawlpage = 0;
			long maxFileCrawlpage = 0;
			long indexnum=0;
			long indexfilesize=0;
			for (Diserver d : diservers) {
				if(!d.getStatus())
					continue;
				InetSocketAddress defaultAddresses = new InetSocketAddress(d.getIpaddress(), d.getDismport());
				  if(DistributedTool.testConnection(defaultAddresses))//如果连接成功
				  {
					maxCrawlpage += ((ClientInterface) RPC.getProxy(
							ClientInterface.class, defaultAddresses))
							.getCrawl_page_num();
					maxFileCrawlpage += ((ClientInterface) RPC.getProxy(
							ClientInterface.class, defaultAddresses))
							.getFileCrawl_page_num();
					indexnum+=((ClientInterface) RPC.getProxy(
							ClientInterface.class, defaultAddresses)).getIndexNum();
					indexfilesize+=((ClientInterface) RPC.getProxy(
							ClientInterface.class, defaultAddresses)).getIndexFileSize();
				  }
			}
			runtime.setIndex_rec_num((int)indexnum);
			runtime.setIndex_file_size(indexfilesize);
			runtime.changeCrawl_page_num(maxCrawlpage);
			runtime.changeFileCrawl_page_num(maxFileCrawlpage);
        	}
        	catch(Exception e)
        	{
        	}
		}
        int speed=0;
        int filespeed=0;
        if(runtime.getCrawl_database_start_time()!=null)
        {
        	Date date=new Date();
        	int timespan=((int)(date.getTime()-runtime.getCrawl_database_start_time().getTime())/(1000*60));
        	timespan=timespan==0?1:timespan;
        	speed = (int)runtime.getCrawl_page_num()/timespan;
        }
        if(runtime.getCrawl_file_start_time()!=null)
        {
        	Date date=new Date();
        	int timespan=((int)(date.getTime()-runtime.getCrawl_file_start_time().getTime())/(1000*60));
        	timespan=timespan==0?1:timespan;
        	filespeed= (int)runtime.getFileCrawl_page_num()/timespan;
        }
        if(speed!=0)
        {
            runtime.setCrawl_speed(speed);
        }
        if(filespeed!=0)
        {
            runtime.setFileCrawl_speed(filespeed);
        }
        if(runtime.getFileCrawl_page_num()==0)
        {
            runtime.setFileCrawl_speed(0);
        }
        runtime.setDiserver((SearchContext.getXdtechsite().getSudis()!=null && SearchContext.getXdtechsite().getSudis())?1:0) ;   //分布式状态
        return Action.SUCCESS;
    }
    public String setIndexDir()
    {
        if(NutchCommand.isCrawl())
        {
        	indexmessage = "数据采集正在运行，请停止数据采集或等待\r\n采集完毕之后再修改采集目录";
        }else
        {
            if (dir != null && !dir.equals("")) {
                File file = new File(dir);
                if (file.exists()) {
                    if (dir.equals(SearchContext.search_dir)) {
                    	indexmessage = "索引目录未改变";
                    } else if (file.listFiles().length > 0) {
                    	indexmessage = "您设置的数据采集文件目录下有文件，数据采集文件\r\n目录必须是一个空的目录";
                    } else {
                        if (file.isDirectory() && file.canRead() && file.canWrite()) {
                            SearchContext.search_dir = dir;
                            RuntimeDataCollect.setIndex_file_dir(dir);
                            SearchContext.resetXdtechSite(dir, service);
                            indexmessage = "数据采集文件目录设置成功，请重启tomcat服务器。";
                        } else if (!file.isDirectory()) {
                        	indexmessage = "数据采集文件目录必须是文件夹";
                        } else if (!file.canRead() || !file.canWrite()) {
                        	indexmessage = "数据采集文件目录必须要有读写权限";
                        }
                    }
                } else {
                    try {
                        if (file.mkdirs()) {
                            SearchContext.search_dir = dir;
                            RuntimeDataCollect.setIndex_file_dir(dir);
                            SearchContext.resetXdtechSite(dir, service);
                            indexmessage = "数据采集文件目录设置成功，请重启tomcat服务器。";
                        } else {
                        	indexmessage = "数据采集文件目录创建失败，请确认读写权限";
                        }
                    } catch (Exception ex) {
                    	indexmessage = "数据采集文件目录创建失败，请确认读写权限：" + ex.getMessage();
                    }
                }
            } else {
            	indexmessage = "数据采集目录未改变，未填写采集目录";
            }
        }
        return Action.INPUT;
    }
    public String startDiserver()
    {
      Control c=SearchContext.CONTROL;
      if(!c.isDisLicense()){
    	  message="您的产品没有获得分布式授权！";
    	  log.info(message);
    	  return Action.SUCCESS;
      }
      List diservers = service.findAllByIObjectCType(Diserver.class);
      if(diservers==null||diservers.size()==0){
    	  return Action.SUCCESS;
      }
      runtime = new RuntimeDataCollect() ;
      SearchContext.getXdtechsite().setSudis(true);
//      SearchContext.IS_SEARCH_SERVER = true;
      service.updateIObject(SearchContext.getXdtechsite());
      runtime.setDiserver((SearchContext.getXdtechsite().getSudis()!=null && SearchContext.getXdtechsite().getSudis())?1:0) ;   //分布式状态
      DistributedTool.startServer();
      return Action.SUCCESS;
    }
    public String stopDiserver()
    {
      runtime = new RuntimeDataCollect() ;
      SearchContext.getXdtechsite().setSudis(false);
      service.updateIObject(SearchContext.getXdtechsite());
      runtime.setDiserver((SearchContext.getXdtechsite().getSudis()!=null && SearchContext.getXdtechsite().getSudis())?1:0) ;   //分布式状态
      DistributedTool.stopServer();
      List<Diserver> diservers =SearchContext.getDao().findAllByIObjectCType(Diserver.class);
      for(Diserver dis:diservers)
      {
      	dis.setStatus(false);
      	dis.setServerstatus(false);
      	SearchContext.getDao().updateIObject(dis);
      	SearchContext.reloadDiserverList();
      }
      return Action.SUCCESS;
    }
    public String startSynchro() throws Exception{
    	if(NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
    		if(runtime==null){
       		 	runtime = new RuntimeDataCollect() ;
    		}
    		runtime.setSynchroIsRunning(true);
    		ImDistributedTool.startServer();
    		String hql="update Xdtechsite set issyn='1'";
    		((IDaoManager)service).execByHQL(hql,"0");
    	}else{
    		message="系统正在运行，不能开启集群服务！";
    	}
    	return Action.SUCCESS;
    }
    public String stopSynchro() throws Exception{
    	if(NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
    		if(runtime==null){
    			runtime = new RuntimeDataCollect() ;
    		}
    		runtime.setSynchroIsRunning(false);
    		String hql="update Xdtechsite set issyn='0'";
    		((IDaoManager)service).execByHQL(hql,"0");
    		SearchContext.initXdtechSite();
    		ImDistributedTool.stopServer();
    		ImDistributedTool.isReady=false;
    	}else{
    		message="系统正在运行，不能停止集群服务！";
    	}
    	return Action.SUCCESS;
    }
    public RuntimeDataCollect getRuntime() {
        return runtime;
    }

    public String getDir() {
        return dir;
    }

    public String getMessage() {
        return message;
    }

    public void setRuntime(RuntimeDataCollect runtime) {
        this.runtime = runtime;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setMessage(String message) {
        this.message = message;
    }
	public static String getXdmessage() {
		return xdmessage;
	}
	public static void setXdmessage(String xdmessage) {
		ControlAction.xdmessage = xdmessage;
	}
	public String getIndexmessage() {
		return indexmessage;
	}
	public void setIndexmessage(String indexmessage) {
		this.indexmessage = indexmessage;
	}


}
