package com.xx.platform.web.actions.crawl;

import java.util.*;

import org.hibernate.criterion.*;
import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.web.actions.*;

/**
 * <p>Title:地址过滤规则管理 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author jaddy
 * @version 1.0
 */
public class UrlfilterregAction
    extends BaseAction {
  private List<Urlfilterreg> urlfilterregList;
  private Urlfilterreg urlfilterreg;
  private Integer sum = 0;
  private String message;
  private int page = 1;
  private String sname;
  public String list() throws Exception {
    DetachedCriteria dc = DetachedCriteria.forClass(Urlfilterreg.class);
    if (sname != null && !sname.trim().equals("")) {
      dc.add(Restrictions.ilike("xdname", sname, MatchMode.ANYWHERE));
    }
    urlfilterregList = service.findPageByCriteria(dc, 50, page);
    return Action.SUCCESS;

  }

  public String addDo() throws Exception {
    if (urlfilterreg != null) {
      {
        service.saveIObject(urlfilterreg);
        SearchContext.reloadRules();
      }
    }
    return Action.SUCCESS;
  }

  public String edit() throws Exception {

    if (urlfilterreg != null && urlfilterreg.getId() != null) {
      urlfilterreg = (Urlfilterreg) service.getIObjectByPK(Urlfilterreg.class,
          urlfilterreg.getId());
    }
    return Action.SUCCESS;
  }

  public String editDo() throws Exception {
    if (request.getParameter("type") != null &&
        request.getParameter("type").equals("1")) {
      if (urlfilterreg != null && urlfilterreg.getId() != null) {
        service.deleteIObject(urlfilterreg);
      }
    }
    else {
      if (urlfilterreg != null && urlfilterreg.getId() != null) {
        {
          service.updateIObject(urlfilterreg);
        }
      }
    }
    SearchContext.reloadRules();
    return Action.SUCCESS;
  }

  public Integer getSum() {
    if (urlfilterregList != null) {
      sum = urlfilterregList.size();
    }
    return sum;
  }

  public String getMessage() {
    return message;
  }

  public Urlfilterreg getUrlfilterreg() {
    return urlfilterreg;
  }

  public List getUrlfilterregList() {
    return urlfilterregList;
  }

  public int getPage() {
    return page;
  }

  public String getSname() {
    return sname;
  }

  public void setSum(Integer sum) {
    this.sum = sum;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setUrlfilterregList(List urlfilterregList) {
    this.urlfilterregList = urlfilterregList;
  }

  public void setUrlfilterreg(Urlfilterreg urlfilterreg) {
    this.urlfilterreg = urlfilterreg;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }

}
