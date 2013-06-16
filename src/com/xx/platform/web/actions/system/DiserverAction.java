package com.xx.platform.web.actions.system;

import java.util.*;

import org.hibernate.criterion.*;
import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.core.rpc.*;
import com.xx.platform.domain.model.distributed.*;
import com.xx.platform.web.actions.*;

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
public class DiserverAction
    extends BaseAction {
  private List<Diserver> diserverList;
  private Diserver diserver;
  private CrawlInfo crawlInfo ;
  private String message;
  private int page = 0;
  private String DISABLE_PORT="8332,8432";

  public String list() {
    diserverList = SearchContext.getDiserverList();
    return Action.SUCCESS;
  }
  /**
   * 检查port是否存在与非法端口范围中，是true，否false；
   * @param port
   * @return boolean
   */
  private boolean checkPort(String port)
  {
	  String ports[]=DISABLE_PORT.split(",");
	  for(String p:ports)
	  {
		  if(p.equals(port))
			  return true;
	  }
	  return false;
  }
  public String addDo() throws Exception {
	  if(SearchContext.getDiserverList().size()>=SearchContext.CONTROL.getDisNumer())
	  {
		  this.message = "最多只能添加"+SearchContext.CONTROL.getDisNumer()+"个节点。";
	      return Action.INPUT;
	  }
	  if(checkPort(String.valueOf(diserver.getDisport()))||checkPort(String.valueOf(diserver.getDismport())))
	  {
		  this.message = "端口"+DISABLE_PORT+"是程序使用端口，不能使用。";
	      return Action.INPUT;
	  }
    diserverList = service.findPageByCriteria(
        DetachedCriteria.forClass(Diserver.class).add(Restrictions.and(
        Restrictions.eq("ipaddress", diserver.getIpaddress()),
        Restrictions.or(Restrictions.or(Restrictions.eq("conport",
        diserver.getConport()), Restrictions.eq("disport", diserver.getDisport())),
                        Restrictions.eq("dismport", diserver.getDismport())))));
    if (diserverList != null && diserverList.size() > 0) {
      this.message = "服务节点端口冲突，请检查节点服务端口";
      return Action.INPUT;
    }
    else {
      if (diserver != null) {
        service.saveIObject(diserver);
      }
    }
    SearchContext.setServerList(service.findAllByIObjectCType(Diserver.class));
    return Action.SUCCESS;
  }

  public String editDiserver() throws Exception {
    if ( (diserver != null && diserver.getId() != null)) {
      diserver = (Diserver) service.getIObjectByPK(Diserver.class,
          diserver.getId());
    }
    else if (request.getParameter("id") != null) {
      diserver = (Diserver) service.getIObjectByPK(Diserver.class,
          request.getParameter("id"));
    }
    return Action.SUCCESS;

  }

  public String editDo() throws Exception {
    if (request.getParameter("type") != null &&
        request.getParameter("type").equals("1")) {
      if (diserver != null && diserver.getId() != null) {
        service.deleteIObject(diserver);
      }
    }
    else {
      if (diserver != null && diserver.getId() != null) {
    	  diserverList = service.findPageByCriteria(
    		        DetachedCriteria.forClass(Diserver.class).add(Restrictions.and(
    		        Restrictions.eq("ipaddress", diserver.getIpaddress()),
    		        Restrictions.or(Restrictions.or(Restrictions.eq("conport",
    		        diserver.getConport()), Restrictions.eq("disport", diserver.getDisport())),
    		                        Restrictions.eq("dismport", diserver.getDismport())))).add(Restrictions.ne("id", diserver.getId())));
    		    if (diserverList != null && diserverList.size() > 0) {
    		      this.message = "服务节点端口冲突，请检查节点服务端口";
    		      return Action.INPUT;
    		    }
    		   if(checkPort(String.valueOf(diserver.getDisport()))||checkPort(String.valueOf(diserver.getDismport())))
    			{
    			   this.message = "端口"+DISABLE_PORT+"是程序使用端口，不能使用。";
    			   return Action.INPUT;
    			}
        service.updateIObject(diserver);
      }
    }
    SearchContext.setServerList(service.findAllByIObjectCType(Diserver.class));
    return Action.SUCCESS;
  }

  public String consoleDiserver() throws Exception
  {
    List<Diserver> serverList = SearchContext.getDiserverList();
    for(Diserver server :serverList)
    {
      if(diserver.getId()!=null && diserver.getId().equals(server.getId()))
      {
        diserver = server ;
        break;
      }
    }
    if(diserver!=null)
      crawlInfo = DistributedTool.getDiserverStatus(diserver) ;
    return Action.SUCCESS;
  }
  public String consoleDiserverCommandDB() throws Exception
    {
      List<Diserver> serverList = SearchContext.getDiserverList();
      for(Diserver server :serverList)
      {
        if(diserver.getId()!=null && diserver.getId().equals(server.getId()))
        {
          diserver = server ;
          break;
        }
      }
      if(diserver!=null)
      {
        boolean command = request.getParameter("com") != null &&
                                   request.getParameter("com").equals("true") ;
        SearchContext.commandCrawlDb(diserver,command);
        if(command)
          message = "启动采集，开始获取地址，获取地址列表将会花费<br/>几分钟，请稍候..." ;
        else
          message = "停止采集，运行线程数开始减少，停止将会花费<br/>几分钟，请稍候..." ;
      }
      return Action.SUCCESS;
  }
  public String consoleDiserverCommandFile() throws Exception
  {
    List<Diserver> serverList = SearchContext.getDiserverList();
    for(Diserver server :serverList)
    {
      if(diserver.getId()!=null && diserver.getId().equals(server.getId()))
      {
        diserver = server ;
        break;
      }
    }
    if(diserver!=null)
    {
      boolean command = request.getParameter("com") != null &&
                                 request.getParameter("com").equals("true") ;
      SearchContext.commandCrawlFile(diserver,command);
      if(command)
        message = "启动采集，开始获取地址，获取地址列表将会花费<br/>几分钟，请稍候..." ;
      else
        message = "停止采集，运行线程数开始减少，停止将会花费<br/>几分钟，请稍候..." ;
    }
    return Action.SUCCESS;
}
  public Diserver getDiserver() {
    return diserver;
  }

  public List getDiserverList() {
    return diserverList;
  }

  public int getPage() {
    return page;
  }

  public void setDiserverList(List diserverList) {
    this.diserverList = diserverList;
  }

  public void setDiserver(Diserver diserver) {
    this.diserver = diserver;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public String getMessage() {
    return message;
  }

  public CrawlInfo getCrawlInfo() {
    return crawlInfo;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setCrawlInfo(CrawlInfo crawlInfo) {
    this.crawlInfo = crawlInfo;
  }
}
