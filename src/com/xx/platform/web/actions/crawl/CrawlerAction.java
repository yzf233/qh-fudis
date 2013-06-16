package com.xx.platform.web.actions.crawl;

import java.util.*;

import org.hibernate.criterion.*;
import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.web.actions.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author 杨庆
 * @version 1.0
 */
public class CrawlerAction
    extends BaseAction {
  //private static Crawler gcrawler;
  private List<Dbtable> dbtableList;
  private List<Crawler> crawlerList;
  private List<Category> categoryList;
  private Dbtable dbtable;
  private Crawler crawler;
  private Integer sum = 0;
  private int page = 1;
  private String sname ;
  public String list() throws Exception {
//        crawlerList = service.findAllByIObjectCType(Crawler.class) ;
//        crawlerList = new DCriteriaPageSupport(crawlerList , crawlerList.size()) ;
//        sum = crawlerList.size() ;
    DetachedCriteria dc = DetachedCriteria.forClass(Crawler.class);
    if(sname!=null && !sname.trim().equals(""))
    {
      dc.add(Restrictions.ilike("name",sname,MatchMode.ANYWHERE)) ;
    }

    dc.addOrder(org.hibernate.criterion.Order.desc("name"));
    crawlerList = service.findPageByCriteria(dc, 50, page);
    dbtableList = service.findAllByIObjectCType(Dbtable.class);
    categoryList = service.findAllByIObjectCType(Category.class);
    return Action.SUCCESS;
  }

  public String addDo() throws Exception {
    if (crawler != null) {
      service.saveIObject(crawler);
      if (SearchContext.getXdtechsite().getUrlfilterreg()) {

        Urlfilterreg filter = new Urlfilterreg();
        filter.setXdname(crawler.getName());
        filter.setXdcode(crawler.getCode());
        filter.setCrawlerid(crawler.getId());
        filter.setXsource(0);
        filter.setFiltertype(1);
        filter.setXdprocess(1);
        filter.setFilterreg(crawler.getUrlreg());
        service.saveIObject(filter);
      }

      SearchContext.reloadRules();
    }
    return Action.SUCCESS;
  }

  public String edit() throws Exception {
    if ( (crawler != null && crawler.getId() != null)) {
      crawler = (Crawler) service.getIObjectByPK(Crawler.class,
                                                 crawler.getId());
    }
    else if (request.getParameter("id") != null) {
      crawler = (Crawler) service.getIObjectByPK(Crawler.class,
                                                 request.getParameter("id"));
    }
    categoryList = service.findAllByIObjectCType(Category.class);
    dbtableList = service.findAllByIObjectCType(Dbtable.class);
    return Action.SUCCESS;
  }

  /**
   * 复制 抽取规则
   * @return String
   * @throws Exception
   */
  public String copy() throws Exception {
    if ( (crawler != null && crawler.getId() != null)) {
      gcrawler = (Crawler) service.getIObjectByPK(Crawler.class,
                                                  crawler.getId());
    }
    else if (request.getParameter("id") != null) {
      gcrawler = (Crawler) service.getIObjectByPK(Crawler.class,
                                                  request.getParameter("id"));
    }
    //System.out.println("copy()"+gcrawler.getName());
    response.setCharacterEncoding("UTF-8");
    if (gcrawler == null || gcrawler.getName() == null)
      response.getWriter().write("<font color=green>复制失败</font>");
    else
      response.getWriter().write("<font color=green>复制 " + gcrawler.getName() +
                                 " 成功</font>");
    return null;
    //return Action.SUCCESS;
  }

  /**
   * 粘贴 抽取规则
   * @return String
   * @throws Exception
   */
  public String paste() throws Exception {
    if (gcrawler != null && gcrawler.getId() != null) {
      List<ParserRule>
          parserRuleList = service.findAllByCriteria(DetachedCriteria.
          forClass(ParserRule.class).add(Restrictions.eq("parentid",
          gcrawler.getId())));
      if ( (crawler != null && crawler.getId() != null)) {
        for (ParserRule parseRule : parserRuleList) {
          parseRule.setId(null);
          parseRule.setParentid(crawler.getId());
          service.saveIObject(parseRule);
        }
      }
    }
    //System.out.println("paste()");
    // return null;
    return Action.SUCCESS;
  }

  public String getTableProperty() throws Exception {
    response.setHeader("Cache-Control", "no-cache"); //Forces caches to obtain a new copy of the page from the origin server
    response.setHeader("Cache-Control", "no-store"); //Directs caches not to store the page under any circumstance
    response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
    response.setHeader("Pragma", "no-cache"); //HTTP 1.0 backward compatibility
    StringBuffer strb = new StringBuffer();
    strb.append(
        "<select name='parserRule.tablepropertyid' style='width:100px;'>");
    strb.append("<option value=''>请选择......</option>");

    if (crawler != null && crawler.getId() != null &&
        crawler.getId().trim().length() > 0) {
      crawler = (Crawler) service.getIObjectByPK(Crawler.class,
                                                 crawler.getId());

      if (crawler != null && crawler.getDbtableid() != null &&
          crawler.getDbtableid().trim().length() > 0) {
        dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class,
            crawler.getDbtableid());

        if (dbtable != null && dbtable.getTableproperty() != null &&
            dbtable.getTableproperty().size() > 0) {

          for (Tableproperty property : dbtable.getTableproperty()) {
            strb.append("<option value='").append(property.getId()).
                append("'>").append(property.getCode()).append(
                    "</option>\n");

          }

        }
      }

    }
    strb.append("</select>");
    response.setCharacterEncoding("UTF-8");

    response.getWriter().write(strb.toString());
    return null;
  }

  public String editDo() throws Exception {
    Urlfilterreg urlFilter = null;
    if (crawler != null && crawler.getId() != null) {
      List<Urlfilterreg> urlFilterList = service.findPageByCriteria(
          DetachedCriteria.forClass(Urlfilterreg.class).add(
              Restrictions.eq(
                  "crawlerid", crawler.getId())), 1, 0);
      if (urlFilterList != null && urlFilterList.size() > 0)
        urlFilter = urlFilterList.get(0);
    }

    if (request.getParameter("type") != null &&
        request.getParameter("type").equals("1")) {
      if (crawler != null && crawler.getId() != null) {
        service.deleteIObject(crawler);
        if (crawler.getId().equals(gcrawler != null ? gcrawler.getId() : ""))
          gcrawler = null;
        service.execByHQL("delete from ParserRule where parentid='" +
                          crawler.getId() + "'");
        if (urlFilter != null && urlFilter.getId() != null)
          service.deleteIObject(urlFilter);
      }
    }
    else {
      if (crawler != null && crawler.getId() != null) {
        Crawler crawler_ = (Crawler) service.getIObjectByPK(Crawler.class,
            crawler.getId());
        if (crawler_.getDbtableid() != null &&
            crawler_.getDbtableid().equals(crawler.getDbtableid()))
          service.updateIObject(crawler);
        else {
          service.updateIObject(crawler);
          service.execByHQL(
              "update ParserRule set tablepropertyid=null where parentid='" +
              crawler.getId() + "'");
        }
        if (SearchContext.getXdtechsite().getUrlfilterreg()) {
          if (urlFilter != null && urlFilter.getId() != null) {
            urlFilter.setFilterreg(crawler.getUrlreg());
            service.updateIObject(urlFilter);
          }
          else {
            Urlfilterreg filter = new Urlfilterreg();
            filter.setCrawlerid(crawler.getId());
            filter.setXdname(crawler.getName());
            filter.setXdcode(crawler.getCode());
            filter.setCrawlerid(crawler.getId());
            filter.setXsource(0);
            filter.setFiltertype(1);
            filter.setXdprocess(1);
            filter.setFilterreg(crawler.getUrlreg());
            service.saveIObject(filter);
          }
        }
      }
    }
    SearchContext.reloadRules();
    return Action.SUCCESS;
  }

  public Crawler getCrawler() {
    return crawler;
  }

  public List getCrawlerList() {
    return crawlerList;
  }

  public Integer getSum() {
    if (crawlerList != null) {
      sum = crawlerList.size();
    }
    return sum;
  }

  public List getCategoryList() {
    return categoryList;
  }

  public List getDbtableList() {
    return dbtableList;
  }

  public int getPage() {
    return page;
  }

  public Dbtable getDbtable() {
    return dbtable;
  }

  public String getSname() {
    return sname;
  }

  public void setCrawler(Crawler crawler) {
    this.crawler = crawler;
  }

  public void setCrawlerList(List crawlerList) {
    this.crawlerList = crawlerList;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }

  public void setCategoryList(List categoryList) {
    this.categoryList = categoryList;
  }

  public void setDbtableList(List dbtableList) {
    this.dbtableList = dbtableList;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setDbtable(Dbtable dbtable) {
    this.dbtable = dbtable;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }
}
