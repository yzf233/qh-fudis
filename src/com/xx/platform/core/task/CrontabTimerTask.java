package com.xx.platform.core.task;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.FetchListTool;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.domain.model.system.CrontabTaskAuto;
import com.xx.platform.domain.model.system.Delaydelindex;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.util.tools.SLOG;
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
public class CrontabTimerTask implements Task {
    public static boolean run_ing = false;
    public static CrontabTaskAuto runningtask = null;
    public List<CrontabTaskAuto> tasklist = null;
    public final static int SNUM=5;
    public final static String SSTR="That Diserver operation state appears is abnormal.";
    private WebDbAdminTool webDbAdminTool;
    public void doTask() throws Exception {
    	if(!SearchContext.isInit)
    		return;
    	if(RuntimeDataCollect.getCrawl_status().equals(NutchCommand.CRAWL_STATUS_NOT_RUNNING))//如果爬虫状态不是在idle状态，那么删除没有删除的数据-胡俊
    	{
    		List<Delaydelindex> delaylist=SearchContext.getDao().findAllByIObjectCType(Delaydelindex.class);
    		File file = new File(SearchContext.search_dir + File.separator +
                    "index"); 
    		if(delaylist!=null&&delaylist.size()>0&&!file.exists())//如果索引文件不存在，清空延迟删除索引信息-胡俊
    		{
    			while(delaylist.size()>0)
    			{
    				Delaydelindex d=delaylist.get(0);
    				SearchContext.getDao().deleteIObject(d);
    				delaylist=SearchContext.getDao().findAllByIObjectCType(Delaydelindex.class);
    			}
    		}
    		if(delaylist!=null&&delaylist.size()>0&&file.exists())
    		{

                Directory directory = FSDirectory.getDirectory(file, false);
                IndexReader reader = IndexReader.open(directory);
            try
            {
              if(IndexReader.isLocked(directory))
              {
            	  //被锁定，不做任何操作
              }
              else
              {
    		    for(Delaydelindex d:delaylist)
    		    {
    			int delDoces = deleteDocuments(new Term(d.getFname(),d.getFvalue()),d.getFdocno(),reader); 
    			if(delDoces==-1)
    				continue;
            	SLOG.addMessage("Webservice删除接口调用 field:"+d.getFname()+"  value:"+d.getFvalue()+"  延迟删除"+String.valueOf(delDoces)+"个。");
    			SearchContext.getDao().deleteIObject(d);
                if (delDoces > 0) {
                	if(RuntimeDataCollect.diserver==1)//启动分布式
                    	SearchContext.addNewIndex("127.0.0.1",SearchContext.search_dir);
                    else
                    	RuntimeDataCollect.has_new_index = true;
                  }
    		    }
              }
        	   reader.close();
        	   directory.close();
            }
            catch(Exception e)
            {
            	reader.close();
          	    directory.close();
            }
    		}
    	}

		DetachedCriteria detach=DetachedCriteria.forClass(CrontabTaskAuto.class);
		detach.addOrder(Order.asc("lasttime"));
    	tasklist=SearchContext.getDao().findAllByCriteria(detach);
    	if(tasklist==null||tasklist.size()==0)//如果任务列表为空
    	return;//返回
    	
    	for(CrontabTaskAuto ts:tasklist)//判断定时任务里面的所有的lasttime
    	{
    		Date d=ts.getLasttime();//获取它的lasttime
    		if(d==null)
    			continue; 
    		Date now=new Date();//系统当前时间
    		if(d.getYear()!=now.getYear()||d.getMonth()!=now.getMonth()||d.getDate()!=now.getDate())
    		{//如果lasttime记录的不是当天时间，则设置为null
    			ts.setLasttime(null);
    			SearchContext.getDao().updateIObject(ts);
    		}
    		
    	}
    	for(int i=0;i<tasklist.size();i++)//防止有任务一直没执行
    	{
    		if(tasklist.get(i).getLasttime()==null||tasklist.get(i).getLasttime().equals(""))
    		{
    			CrontabTaskAuto task=tasklist.get(i);
    			tasklist.remove(i);
    			tasklist.add(0, task);
    		}
    	}
    	
    	for(CrontabTaskAuto task:tasklist)//获取每一个任务task
    	{
        
    	if(!task.getState().equals("1"))//如果没有启动定时任务
        {
            //如果当前任务在执行，发送停止指令
        	if(runningtask!=null&&task.getRunning()!=null&&task.getRunning().equals("run")&&task.getId().equals(runningtask.getId()))
        	{
        		 IbeaProperty.log.info("发送任务停止指令...");

                 if (NutchCommand.CRAWL_STATUS_RUNNING.equals(
                         RuntimeDataCollect.
                         getCrawl_status())) {
                     RuntimeDataCollect.setCrawl_status(NutchCommand.
                             CRAWL_STATUS_STOPPING);
                     IbeaProperty.log.info("任务停止指令发送成功");
                 }
                 if(SearchContext.getXdtechsite().getSudis())
//                     SearchContext.stopCrawl();
                	 NutchCommand.setCrawl(false);
                   else{
                 NutchCommand.setCrawl(false);
                   }
                 setTaskLastTimeById(runningtask.getId(),null);
                 CrontabTimerTask.runningtask=null;
                 continue;
        	}
        	else
        	continue;
        }
        
        
    	Date date = new Date() ;//系统当前时间
    	if(task.getKind().equals("week"))//如果是星期
    	{
    		if(!task.contain(String.valueOf(date.getDay()),task.getWeeks()))
    			continue;//当前时间weeks里不包含，返回
    	}
    	else if(task.getKind().equals("month"))//如果是月份，则判断当前day是否相符，不相符返回
    	{
    		if(!task.contain(String.valueOf(date.getDate()),task.getDays()))
    			continue;//当前时间days里不包含，返回
    	}
    	
    		
    	Date begindate = new Date() ;//设置当天启动时间
    	begindate.setHours(Integer.valueOf(task.getTaskbeginhour()));
    	begindate.setMinutes(Integer.valueOf(task.getTaskbeginminutes()));
    	begindate.setSeconds(0);
    	Date enddate = new Date() ;//设置当天结束时间
    	enddate.setHours(Integer.valueOf(task.getTaskendhour()));
    	enddate.setMinutes(Integer.valueOf(task.getTaskendminutes()));
    	enddate.setSeconds(0);
    	


		if(runningtask!=null&&runningtask.getRunning().equals("run")&&!runningtask.getId().equals(task.getId()))//如果当前有任务，而且当前任务在执行状态，并且当前执行任务不是task，则continue
    		continue;
		///如果当前任务已经停止，则进行抢占模式//
	


    	if(((date.getTime()-enddate.getTime())/1000)>0&&runningtask!=null)//如果超过了结束时间
    	{
    		//任务停止
    		if(task.getTimes().equals("one"))//用户设置one，到时间runningtask设置为null
    		{
    			continue;
    		}
    		if(!runningtask.getId().equals(task.getId())||runningtask.getRunning().equals("stop"))//判断当前执行的任务是否是它，是否在执行，不是则continue
    		{
    			continue;
    		}
            IbeaProperty.log.info("发送任务停止指令...");

                if (NutchCommand.CRAWL_STATUS_RUNNING.equals(
                        RuntimeDataCollect.
                        getCrawl_status())) {
                    RuntimeDataCollect.setCrawl_status(NutchCommand.
                            CRAWL_STATUS_STOPPING);
                    IbeaProperty.log.info("任务停止指令发送成功");
                }
                if(SearchContext.getXdtechsite().getSudis())
//                    SearchContext.stopCrawl();
                	 NutchCommand.setCrawl(false);
                  else{
                NutchCommand.setCrawl(false);
                  }
                setTaskLastTimeById(runningtask.getId(),null);
                CrontabTimerTask.runningtask=null;
                continue;
    	}
    	

    	if(runningtask!=null&&runningtask.getRunning()!=null&&runningtask.getRunning().equals("run"))//当前任务正在执行
    	{
    		continue;
    	}
    	
    	
    	if((((date.getTime()-begindate.getTime())/1000)>0&&((enddate.getTime()-date.getTime())/1000)>0&&task.getTimes().equals("more"))||(((date.getTime()-begindate.getTime())/1000)<4&&((date.getTime()-begindate.getTime())/1000)>0&&task.getTimes().equals("one")))//如果当前时间大于启动时间，并且小于结束时间
    	{
    		if(task.getLasttime()!=null)//如果lasttime不为空(证明已经执行过)
    		{
    			if(task.getTimes().equals("one"))//单个任务不做再次执行
    			{
    				continue;
    			}
//    			则判断当前时间和lasttime时间间隔是否大于timespan
    			if((date.getTime()-task.getLasttime().getTime())>Integer.valueOf(task.getTimespan())*1000)//大于
    			{
    				//任务启动，执行结束后要记录lasttime
    				if(!NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
                		Thread.sleep(1l);
                	}

                    if(task.getTasktype().equals("database")&&!NutchCommand.CRAWL_COMMAND_FILECRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//启动数据库采集
                    {
                    	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
                        RuntimeDataCollect.setCrawl_page_num(0);
                        RuntimeDataCollect.setCrawl_parse_num(0);
                        RuntimeDataCollect.setCrawl_speed(0);
                        RuntimeDataCollect.setCrawl_times(0);
//                        if(SearchContext.getXdtechsite().getSudis()){
//                            SearchContext.startCrawl();
//                            NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//                        }else
                        {
                        	 ControlAction.addXdmessage("定时任务["+task.getName()+"]开始执行\r\n");
                        	 ControlAction.addXdmessage("开始数据库采集服务");
                        	 FetchListTool.pernum=SearchContext.getXdtechsite().getDbnumber();
                        	 RuntimeDataCollect.setCrawl_database_start_time(new Date());
             				run_ing = true;
            				CrontabTimerTask.runningtask=null;
            				CrontabTimerTask.runningtask = task;
            				CrontabTimerTask.runningtask.setRunning("run");
            				task.setRunning("run");
                            NutchCommand.setCrawl(true);
                            NutchCommand.CRAWL_COMMAND_CRAWLER = true;
                        }
                    }
                    else  if(task.getTasktype().equals("file")&& !NutchCommand.CRAWL_COMMAND_CRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//启动目录采集
                    {
                    	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
                        RuntimeDataCollect.setCrawl_page_num(0);
                        RuntimeDataCollect.setCrawl_parse_num(0);
                        RuntimeDataCollect.setCrawl_speed(0);
                        RuntimeDataCollect.setCrawl_times(0);
//                        if(SearchContext.getXdtechsite().getSudis()){
//                            SearchContext.startCrawl();
//                            NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//                        }else
                        {
                        	ControlAction.addXdmessage("定时任务["+task.getName()+"]开始执行\r\n");
                        	 ControlAction.addXdmessage("开始文件采集服务");
                        	 RuntimeDataCollect.setCrawl_file_start_time(new Date());
             				run_ing = true;
            				CrontabTimerTask.runningtask=null;
            				CrontabTimerTask.runningtask = task;
            				CrontabTimerTask.runningtask.setRunning("run");
            				task.setRunning("run");
                            NutchCommand.setCrawl(true);
                            NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
                        }
                    }
    			}
    			else//小于，返回
    			{
    				continue;
    			}
    		}
    		else//如果lasttime为空，任务启动(第一次执行)，执行结束后要记录lasttime
    		{
				//任务启动，执行结束后要记录lasttime
				if(!NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
            		Thread.sleep(1l);
            	}

                if(task.getTasktype().equals("database")&&!NutchCommand.CRAWL_COMMAND_FILECRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//启动数据库采集
                {
                	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
                    RuntimeDataCollect.setCrawl_page_num(0);
                    RuntimeDataCollect.setCrawl_parse_num(0);
                    RuntimeDataCollect.setCrawl_speed(0);
                    RuntimeDataCollect.setCrawl_times(0);
//                    if(SearchContext.getXdtechsite().getSudis()){
//                        SearchContext.startCrawl();
//                        NutchCommand.CRAWL_COMMAND_CRAWLER = true;
//                    }else
                    {
                    	ControlAction.addXdmessage("定时任务["+task.getName()+"]开始执行\r\n");
                    	 ControlAction.addXdmessage("开始数据库采集服务");
                    	 FetchListTool.pernum=SearchContext.getXdtechsite().getDbnumber();
                    	 RuntimeDataCollect.setCrawl_database_start_time(new Date());
         				run_ing = true;
        				CrontabTimerTask.runningtask=null;
        				CrontabTimerTask.runningtask = task;
        				CrontabTimerTask.runningtask.setRunning("run");
        				task.setRunning("run");
                        NutchCommand.setCrawl(true);
                        NutchCommand.CRAWL_COMMAND_CRAWLER = true;
                    }
                }
                else if(task.getTasktype().equals("file")&& !NutchCommand.CRAWL_COMMAND_CRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//启动目录采集
                {
                	RuntimeDataCollect.setCrawl_status(NutchCommand.CRAWL_STATUS_RUNNING);
                    RuntimeDataCollect.setCrawl_page_num(0);
                    RuntimeDataCollect.setCrawl_parse_num(0);
                    RuntimeDataCollect.setCrawl_speed(0);
                    RuntimeDataCollect.setCrawl_times(0);
//                    if(SearchContext.getXdtechsite().getSudis()){
//                        SearchContext.startCrawl();
//                        NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
//                    }else
                    {
                    	ControlAction.addXdmessage("定时任务["+task.getName()+"]开始执行\r\n");
                    	 ControlAction.addXdmessage("开始文件采集服务");
                    	 RuntimeDataCollect.setCrawl_file_start_time(new Date());
         				run_ing = true;
        				CrontabTimerTask.runningtask=null;
        				CrontabTimerTask.runningtask = task;
        				CrontabTimerTask.runningtask.setRunning("run");
        				task.setRunning("run");
                        NutchCommand.setCrawl(true);
                        NutchCommand.CRAWL_COMMAND_FILECRAWLER = true;
                    }
                }
    			
    		}
    	}
    

    	
    	}
        
    }
	public static CrontabTaskAuto getRunningtask() {
		return runningtask;
	}
	public static void setRunningtask(CrontabTaskAuto runningtask) {
		CrontabTimerTask.runningtask = runningtask;
	}
	public static void setTaskLastTimeById(String id,Date d)
	{
		List<CrontabTaskAuto> ls=SearchContext.getDao().findAllByIObjectCType(CrontabTaskAuto.class);
        for(CrontabTaskAuto s:ls)
        {
        	if(s.getId().equals(id))
        	{
        		s.setLasttime(d);
        		SearchContext.getDao().updateIObject(s);
        	}
        }
	}
	public List<CrontabTaskAuto> getTasklist() {
		return tasklist;
	}
	public void setTasklist(List<CrontabTaskAuto> tasklist) {
		this.tasklist = tasklist;
	}
	  public final int deleteDocuments(Term term,String docno_str,IndexReader reader) throws Exception{
		  org.apache.lucene.index.TermDocs docs = reader.termDocs(term);
		    if (docs == null) return 0;
		    int n = 0;
		    webDbAdminTool=new WebDbAdminTool();
		    String docnos[]=docno_str.split(",");
		    try {
		      while(docs.next()) {
			      Document d=reader.document(docs.doc());
		    	  boolean hasdocno=false;
		    	  for(String s:docnos)
		    	  {
		    		  if(d.getField("docNo").stringValue().equals(s))
		    			  hasdocno=true;
		    	  }
		    	  if(!hasdocno)
		    		  continue;
		    	 String docSource=d.getField("docSource").stringValue();
		    	 if(docSource.equals("database"))
		    	 {
		    		 webDbAdminTool.removeContents(d.getField("type").stringValue());
		    	 }
		         else if(docSource.equals("file"))
		    	 {
		        	 webDbAdminTool.removeContents(d.getField("type").stringValue());
		        	 webDbAdminTool.removeContents(d.getField("subType").stringValue());
		    	 }
		    	 reader.deleteDocument(docs.doc());
		        n++;
		      }
		    }
		    catch(StaleReaderException e)//reader对象过期
		    {
		    	return -1;
		    }
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		    finally {
		      docs.close();
		    }
		    return n;
		  }
}
