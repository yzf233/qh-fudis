package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.util.constants.IbeaProperty;
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
public class ProregionAction
    extends BaseAction {
  private List<Proregion> proRegionList = null;
  private Proregion proregion = null;
  private int page = 0;
  private int num = 0;
  public String list() throws Exception {
    proRegionList = service.findAllByIObjectCType(Proregion.class);
    this.num = proRegionList.size();
    return Action.SUCCESS;
  }

  public String addDo() throws Exception {
    if (proregion != null) {
      service.saveIObject(proregion);
    }
    //把proregion放到缓存中
    SearchContext.proregionList = service.findAllByIObjectCType(Proregion.class);

    return Action.SUCCESS;
  }

  public String edit() throws Exception {
    if ( (proregion != null && proregion.getId() != null)) {
      proregion = (Proregion) service.getIObjectByPK(Proregion.class,
          proregion.getId());
    }
    else if (request.getParameter("id") != null) {
      proregion = (Proregion) service.getIObjectByPK(Proregion.class,
          request.getParameter("id"));
    }
    return Action.SUCCESS;

  }

  public String editDo() throws Exception {
    if (request.getParameter("type") != null &&
        request.getParameter("type").equals("1")) {
      if (proregion != null && proregion.getId() != null) {
        service.deleteIObject(proregion);
      }
    }
    else {
      if (proregion != null && proregion.getId() != null) {
        service.updateIObject(proregion);
      }
    }

    //把proregion放到缓存中

    SearchContext.proregionList = service.findAllByIObjectCType(Proregion.class);
    return Action.SUCCESS;
  }

  public int getPage() {
    return page;
  }

  public List getProRegionList() {
    return proRegionList;
  }

  public Proregion getProregion() {
    return proregion;
  }

  public int getNum() {
    return num;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setProRegionList(List proRegionList) {
    this.proRegionList = proRegionList;
  }

  public void setProregion(Proregion proregion) {
    this.proregion = proregion;
  }

  public void setNum(int num) {
    this.num = num;
  }

}
