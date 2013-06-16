package com.xx.platform.web.actions.system;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.system.Sqllog;
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
public class SqlLogAction extends BaseAction{
    private List<Sqllog> logList ;
    private Sqllog sqllog;
    private String sname;
    private int page = 1;
    public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public Sqllog getSqllog() {
		return sqllog;
	}

	public void setSqllog(Sqllog sqllog) {
		this.sqllog = sqllog;
	}

	public String listLog()
    {
		sname=request.getParameter("sname");
		DetachedCriteria d=DetachedCriteria.forClass(Sqllog.class);
		d.addOrder(Order.desc("dtime"));
		if(sname!=null&&!sname.equals(""))
		d.add(Restrictions.ilike("message", sname, MatchMode.ANYWHERE));
		logList=service.findPageByCriteria(d,20,page);
        return Action.SUCCESS;
    }
  
    public String deleteLog() throws Exception
    {
//    	sqllog=(Sqllog)service.getIObjectByPK(Sqllog.class, request.getParameter("id"));
//    	service.deleteIObject(sqllog);
    	String hql="delete from Sqllog where id='"+request.getParameter("id")+"'";
    	((IDaoManager)service).execByHQL(hql,"1");
    	return Action.SUCCESS;
    } 

    public String deleteAllLog()
    {
    	try
    	{
    	service.execByHQL("delete from Sqllog");
    	}
    	catch(Exception e)
    	{}
    	return Action.SUCCESS;
    }

	public List<Sqllog> getLogList() {
		return logList;
	}

	public void setLogList(List<Sqllog> logList) {
		this.logList = logList;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

}
