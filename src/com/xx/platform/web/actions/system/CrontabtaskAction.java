package com.xx.platform.web.actions.system;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.nutch.plugin.Plugin;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.system.CrontabTaskAuto;
import com.xx.platform.web.actions.BaseAction;

/**
 * <p>定时任务action </p>
 *
 * <p>胡俊</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>xdtech </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CrontabtaskAction extends BaseAction {
    private List<CrontabTaskAuto> crontabTaskautoList;
    private List<Plugin> pluginList;
    private List<Dbmessage> DbmessageList;
    private CrontabTaskAuto crontabTaskauto;
    private String state;
    private String message;
    public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String list() throws Exception {
    	DbmessageList=new ArrayList();
        crontabTaskautoList = service.findAllByIObjectCType(CrontabTaskAuto.class);
        if(request.getParameter("crontabTaskauto.id")!=null)
        	crontabTaskauto=(CrontabTaskAuto)service.getIObjectByPK(CrontabTaskAuto.class, request.getParameter("crontabTaskauto.id"));
        
        if(crontabTaskauto!=null)
        	state=crontabTaskauto.getState();
        
      try
      {
       List<Dbconfig> dbList = service.findAllByIObjectCType(Dbconfig.class);
       for(Dbconfig d:dbList)//把所有的数据库和表对象传递给页面
       {
    	   String hql="from Dbtable where dbid='"+d.getId()+"' ";
    	   List<Dbtable> dbt=service.findByQuery(hql);//service.findAllByCriteria(detach);
    	   Dbmessage dbmessage=new Dbmessage();
    	   dbmessage.setDbconfig(d);
    	   dbmessage.setDbtablelist(dbt);
    	   DbmessageList.add(dbmessage);
       }
      }catch(Exception e)
      {
    	  e.printStackTrace();
      }
        return Action.SUCCESS;
    }
    public String delDo() throws Exception {
        service.deleteIObject(crontabTaskauto);
        return Action.SUCCESS;
    }


    public String StringConvert(String [] str)
    {
    	String s="";
    	for(int i=0;str!=null&&i<str.length;i++)
    	{
    		s+=str[i]+" ";
    	}
    	return s;
    	
    }
    public String addautoDo() throws Exception {
    	String k=request.getParameter("k").toString();
    	if(k.equals("0"))//提交
    	{
    		String [] weeks=request.getParameterValues("crontabTaskauto.weeks");
    		String week="";
    		for(int i=0;weeks!=null&&i<weeks.length;i++)
    		{
    			week+=weeks[i]+" ";
    		}
    		crontabTaskauto.setWeeks(week);
    		if(crontabTaskauto.getKind().equals("day"))
    		{
    			crontabTaskauto.setWeeks("");
    			crontabTaskauto.setDays("");
    		}	
    		else if(crontabTaskauto.getKind().equals("week"))
    		{
    			crontabTaskauto.setDays("");
    		}
    		else if(crontabTaskauto.getKind().equals("month"))
    		{
    			crontabTaskauto.setWeeks("");
    		}   
    		if(crontabTaskauto.getTimes().equals("one"))
    		{
    			crontabTaskauto.setTaskendhour("0");
    			crontabTaskauto.setTaskendminutes("0");
    		}
    		crontabTaskauto.setLasttime(null);
    		crontabTaskauto.setState("0");
    		service.saveIObject(crontabTaskauto);
            return Action.SUCCESS;
    		
    	}
    	else if(k.equals("1"))//启动
    	{
    		if(RuntimeDataCollect.isSynchroIsRunning()){
    			message="集群启动时定时任务不能运行！";
    			return Action.SUCCESS;
    		}
    		crontabTaskauto=(CrontabTaskAuto)service.getIObjectByPK(CrontabTaskAuto.class, crontabTaskauto.getId());
    		crontabTaskauto.setLasttime(null);
    		crontabTaskauto.setState("1");
    		service.updateIObject(crontabTaskauto);
            return Action.SUCCESS;
    	}
    	else if(k.equals("2"))//停止
    	{
    		crontabTaskauto=(CrontabTaskAuto)service.getIObjectByPK(CrontabTaskAuto.class, crontabTaskauto.getId());
    		crontabTaskauto.setLasttime(null);
    		crontabTaskauto.setState("0");
    		service.updateIObject(crontabTaskauto);
            return Action.SUCCESS;
    		
    	}
    	else if(k.equals("3"))//保存
    	{
    		String [] weeks=request.getParameterValues("crontabTaskauto.weeks");
    		String week="";
    		for(int i=0;weeks!=null&&i<weeks.length;i++)
    		{
    			week+=weeks[i]+" ";
    		}
    		crontabTaskauto.setWeeks(week);
    		if(crontabTaskauto.getKind().equals("day"))
    		{
    			crontabTaskauto.setWeeks("");
    			crontabTaskauto.setDays("");
    		}	
    		else if(crontabTaskauto.getKind().equals("week"))
    		{
    			crontabTaskauto.setDays("");
    		}
    		else if(crontabTaskauto.getKind().equals("month"))
    		{
    			crontabTaskauto.setWeeks("");
    		}   
    		if(crontabTaskauto.getTimes().equals("one"))
    		{
    			crontabTaskauto.setTaskendhour("0");
    			crontabTaskauto.setTaskendminutes("0");
    		}
    		if(crontabTaskauto.getTasktype().equals("file"))
    		{
    			crontabTaskauto.setDbtid(null);
    		}
    		service.updateIObject(crontabTaskauto);
            return Action.SUCCESS;
    		
    	}
    		
    	return Action.SUCCESS;
    }
    
    public String getStartCount() throws Exception {
    	int n=service.getCountByCriteria(DetachedCriteria.forClass(CrontabTaskAuto.class).add(Restrictions.eq("state","1")));
    	response.setCharacterEncoding("utf-8");
    	PrintWriter out=response.getWriter();
    	out.write(""+n);
    	out.close();
    	return null;
    }


    public List getPluginList() {
        return pluginList;
    }

    public void setPluginList(List pluginList) {
        this.pluginList = pluginList;
    }

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<CrontabTaskAuto> getCrontabTaskautoList() {
		return crontabTaskautoList;
	}

	public void setCrontabTaskautoList(List<CrontabTaskAuto> crontabTaskautoList) {
		this.crontabTaskautoList = crontabTaskautoList;
	}

	public CrontabTaskAuto getCrontabTaskauto() {
		return crontabTaskauto;
	}

	public void setCrontabTaskauto(CrontabTaskAuto crontabTaskauto) {
		this.crontabTaskauto = crontabTaskauto;
	}
	public List<Dbmessage> getDbmessageList() {
		return DbmessageList;
	}
	public void setDbmessageList(List<Dbmessage> dbmessageList) {
		DbmessageList = dbmessageList;
	}




}
