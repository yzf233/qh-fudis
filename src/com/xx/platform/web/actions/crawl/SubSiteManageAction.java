package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.util.dao.DCriteriaPageSupport;
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
public class SubSiteManageAction extends BaseAction {
    private List<WebDB> webDBList;
    private WebDB webDb;
    private Integer sum=0;
    private int  page = 1;
    private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
    public String list() throws Exception {

        webDBList= new DCriteriaPageSupport<WebDB>(webDbAdminTool.getWebDBList((page-1)*TEN_PAGE_SIZE,TEN_PAGE_SIZE),(int)webDbAdminTool.size() , TEN_PAGE_SIZE , page*TEN_PAGE_SIZE); //;service.findByIObjectCType(WebDB.class,page,TEN_PAGE_SIZE)
        ((DCriteriaPageSupport)webDBList).setTotalCount((int)webDbAdminTool.size());
        ((DCriteriaPageSupport)webDBList).set_page_size(TEN_PAGE_SIZE);
        return Action.SUCCESS;
    }
    public String del() throws Exception {
        if(request.getParameter("d")!=null)
        {
            webDbAdminTool.rmAllWebDB();
        }else
        {
          long key = webDbAdminTool.getKey(webDb.getMd5CHash().getDigest()) ;
          webDb.setKey(key);
          webDb = webDbAdminTool.getWebDB(webDb) ;
          webDbAdminTool.rmWebDB(webDb);
          webDbAdminTool.rmKey(webDb.getMd5CHash().getDigest());
        }
        return Action.SUCCESS;
    }
    public String addSubSiteManageDo() throws Exception {
        if(webDb!=null)
        {
            webDbAdminTool.addWebDB(webDb);
        }
        return Action.SUCCESS;
    }

    public WebDB getSubSiteManage() {
        return webDb;
    }

    public List getSubSiteManageList() {
        return webDBList;
    }

    public Integer getSum() {
        if(webDBList!=null){
            sum=webDBList.size();
        }
        return sum;
    }

    public int getPage() {
        return page;
    }

    public WebDB getWebDb() {
        return webDb;
    }

    public List getWebDBList() {
        return webDBList;
    }

    public void setSubSiteManage(WebDB webDb) {
        this.webDb = webDb;
    }

    public void setSubSiteManageList(List webDBList) {
        this.webDBList = webDBList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setWebDb(WebDB webDb) {
        this.webDb = webDb;
    }

    public void setWebDBList(List webDBList) {
        this.webDBList = webDBList;
    }

}
