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
    	if(RuntimeDataCollect.getCrawl_status().equals(NutchCommand.CRAWL_STATUS_NOT_RUNNING))//�������״̬������idle״̬����ôɾ��û��ɾ��������-����
    	{
    		List<Delaydelindex> delaylist=SearchContext.getDao().findAllByIObjectCType(Delaydelindex.class);
    		File file = new File(SearchContext.search_dir + File.separator +
                    "index"); 
    		if(delaylist!=null&&delaylist.size()>0&&!file.exists())//��������ļ������ڣ�����ӳ�ɾ��������Ϣ-����
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
            	  //�������������κβ���
              }
              else
              {
    		    for(Delaydelindex d:delaylist)
    		    {
    			int delDoces = deleteDocuments(new Term(d.getFname(),d.getFvalue()),d.getFdocno(),reader); 
    			if(delDoces==-1)
    				continue;
            	SLOG.addMessage("Webserviceɾ���ӿڵ��� field:"+d.getFname()+"  value:"+d.getFvalue()+"  �ӳ�ɾ��"+String.valueOf(delDoces)+"����");
    			SearchContext.getDao().deleteIObject(d);
                if (delDoces > 0) {
                	if(RuntimeDataCollect.diserver==1)//�����ֲ�ʽ
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
    	if(tasklist==null||tasklist.size()==0)//��������б�Ϊ��
    	return;//����
    	
    	for(CrontabTaskAuto ts:tasklist)//�ж϶�ʱ������������е�lasttime
    	{
    		Date d=ts.getLasttime();//��ȡ����lasttime
    		if(d==null)
    			continue; 
    		Date now=new Date();//ϵͳ��ǰʱ��
    		if(d.getYear()!=now.getYear()||d.getMonth()!=now.getMonth()||d.getDate()!=now.getDate())
    		{//���lasttime��¼�Ĳ��ǵ���ʱ�䣬������Ϊnull
    			ts.setLasttime(null);
    			SearchContext.getDao().updateIObject(ts);
    		}
    		
    	}
    	for(int i=0;i<tasklist.size();i++)//��ֹ������һֱûִ��
    	{
    		if(tasklist.get(i).getLasttime()==null||tasklist.get(i).getLasttime().equals(""))
    		{
    			CrontabTaskAuto task=tasklist.get(i);
    			tasklist.remove(i);
    			tasklist.add(0, task);
    		}
    	}
    	
    	for(CrontabTaskAuto task:tasklist)//��ȡÿһ������task
    	{
        
    	if(!task.getState().equals("1"))//���û��������ʱ����
        {
            //�����ǰ������ִ�У�����ָֹͣ��
        	if(runningtask!=null&&task.getRunning()!=null&&task.getRunning().equals("run")&&task.getId().equals(runningtask.getId()))
        	{
        		 IbeaProperty.log.info("��������ָֹͣ��...");

                 if (NutchCommand.CRAWL_STATUS_RUNNING.equals(
                         RuntimeDataCollect.
                         getCrawl_status())) {
                     RuntimeDataCollect.setCrawl_status(NutchCommand.
                             CRAWL_STATUS_STOPPING);
                     IbeaProperty.log.info("����ָֹͣ��ͳɹ�");
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
        
        
    	Date date = new Date() ;//ϵͳ��ǰʱ��
    	if(task.getKind().equals("week"))//���������
    	{
    		if(!task.contain(String.valueOf(date.getDay()),task.getWeeks()))
    			continue;//��ǰʱ��weeks�ﲻ����������
    	}
    	else if(task.getKind().equals("month"))//������·ݣ����жϵ�ǰday�Ƿ���������������
    	{
    		if(!task.contain(String.valueOf(date.getDate()),task.getDays()))
    			continue;//��ǰʱ��days�ﲻ����������
    	}
    	
    		
    	Date begindate = new Date() ;//���õ�������ʱ��
    	begindate.setHours(Integer.valueOf(task.getTaskbeginhour()));
    	begindate.setMinutes(Integer.valueOf(task.getTaskbeginminutes()));
    	begindate.setSeconds(0);
    	Date enddate = new Date() ;//���õ������ʱ��
    	enddate.setHours(Integer.valueOf(task.getTaskendhour()));
    	enddate.setMinutes(Integer.valueOf(task.getTaskendminutes()));
    	enddate.setSeconds(0);
    	


		if(runningtask!=null&&runningtask.getRunning().equals("run")&&!runningtask.getId().equals(task.getId()))//�����ǰ�����񣬶��ҵ�ǰ������ִ��״̬�����ҵ�ǰִ��������task����continue
    		continue;
		///�����ǰ�����Ѿ�ֹͣ���������ռģʽ//
	


    	if(((date.getTime()-enddate.getTime())/1000)>0&&runningtask!=null)//��������˽���ʱ��
    	{
    		//����ֹͣ
    		if(task.getTimes().equals("one"))//�û�����one����ʱ��runningtask����Ϊnull
    		{
    			continue;
    		}
    		if(!runningtask.getId().equals(task.getId())||runningtask.getRunning().equals("stop"))//�жϵ�ǰִ�е������Ƿ��������Ƿ���ִ�У�������continue
    		{
    			continue;
    		}
            IbeaProperty.log.info("��������ָֹͣ��...");

                if (NutchCommand.CRAWL_STATUS_RUNNING.equals(
                        RuntimeDataCollect.
                        getCrawl_status())) {
                    RuntimeDataCollect.setCrawl_status(NutchCommand.
                            CRAWL_STATUS_STOPPING);
                    IbeaProperty.log.info("����ָֹͣ��ͳɹ�");
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
    	

    	if(runningtask!=null&&runningtask.getRunning()!=null&&runningtask.getRunning().equals("run"))//��ǰ��������ִ��
    	{
    		continue;
    	}
    	
    	
    	if((((date.getTime()-begindate.getTime())/1000)>0&&((enddate.getTime()-date.getTime())/1000)>0&&task.getTimes().equals("more"))||(((date.getTime()-begindate.getTime())/1000)<4&&((date.getTime()-begindate.getTime())/1000)>0&&task.getTimes().equals("one")))//�����ǰʱ���������ʱ�䣬����С�ڽ���ʱ��
    	{
    		if(task.getLasttime()!=null)//���lasttime��Ϊ��(֤���Ѿ�ִ�й�)
    		{
    			if(task.getTimes().equals("one"))//�����������ٴ�ִ��
    			{
    				continue;
    			}
//    			���жϵ�ǰʱ���lasttimeʱ�����Ƿ����timespan
    			if((date.getTime()-task.getLasttime().getTime())>Integer.valueOf(task.getTimespan())*1000)//����
    			{
    				//����������ִ�н�����Ҫ��¼lasttime
    				if(!NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
                		Thread.sleep(1l);
                	}

                    if(task.getTasktype().equals("database")&&!NutchCommand.CRAWL_COMMAND_FILECRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//�������ݿ�ɼ�
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
                        	 ControlAction.addXdmessage("��ʱ����["+task.getName()+"]��ʼִ��\r\n");
                        	 ControlAction.addXdmessage("��ʼ���ݿ�ɼ�����");
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
                    else  if(task.getTasktype().equals("file")&& !NutchCommand.CRAWL_COMMAND_CRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//����Ŀ¼�ɼ�
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
                        	ControlAction.addXdmessage("��ʱ����["+task.getName()+"]��ʼִ��\r\n");
                        	 ControlAction.addXdmessage("��ʼ�ļ��ɼ�����");
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
    			else//С�ڣ�����
    			{
    				continue;
    			}
    		}
    		else//���lasttimeΪ�գ���������(��һ��ִ��)��ִ�н�����Ҫ��¼lasttime
    		{
				//����������ִ�н�����Ҫ��¼lasttime
				if(!NutchCommand.CRAWL_STATUS_NOT_RUNNING.equals(RuntimeDataCollect.getCrawl_status())){
            		Thread.sleep(1l);
            	}

                if(task.getTasktype().equals("database")&&!NutchCommand.CRAWL_COMMAND_FILECRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//�������ݿ�ɼ�
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
                    	ControlAction.addXdmessage("��ʱ����["+task.getName()+"]��ʼִ��\r\n");
                    	 ControlAction.addXdmessage("��ʼ���ݿ�ɼ�����");
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
                else if(task.getTasktype().equals("file")&& !NutchCommand.CRAWL_COMMAND_CRAWLER&&RuntimeDataCollect.getCrawl_status().equalsIgnoreCase("Not Running"))//����Ŀ¼�ɼ�
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
                    	ControlAction.addXdmessage("��ʱ����["+task.getName()+"]��ʼִ��\r\n");
                    	 ControlAction.addXdmessage("��ʼ�ļ��ɼ�����");
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
		    catch(StaleReaderException e)//reader�������
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
