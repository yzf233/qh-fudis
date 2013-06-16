package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.domain.model.crawl.*;
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
public class NdeStructAction extends BaseAction {
    private List<NdeStruct> ndeStructList;
    private NdeStruct ndeStruct;
    private Integer sum=0;
    private int page = 0 ;

    public String list() throws Exception {
        ndeStructList=service.findByIObjectCType(NdeStruct.class , page , THR_PAGE_SIZE);
        return Action.SUCCESS;
    }

    public String addDo() throws Exception {
        if (ndeStruct != null) {
            service.saveIObject(ndeStruct);
        }
        return Action.SUCCESS;

    }

    public String edit() throws Exception {
        if ((ndeStruct != null && ndeStruct.getId() != null)) {
            ndeStruct = (NdeStruct) service.getIObjectByPK(NdeStruct.class,
                    ndeStruct.getId());
        } else if (request.getParameter("id") != null) {
            ndeStruct = (NdeStruct) service.getIObjectByPK(NdeStruct.class,
                    request.getParameter("id"));
        }
        return Action.SUCCESS;

    }

    public String editDo() throws Exception {
        if (request.getParameter("type") != null &&
            request.getParameter("type").equals("1")) {
            if (ndeStruct != null && ndeStruct.getId() != null) {
                service.deleteIObject(ndeStruct);
            }
        } else {
            if (ndeStruct != null && ndeStruct.getId() != null) {
                service.updateIObject(ndeStruct);
            }
        }
        return Action.SUCCESS;
    }


    public NdeStruct getNdeStruct() {
        return ndeStruct;
    }

    public List getNdeStructList() {
        return ndeStructList;
    }

    public Integer getSum() {
        if(ndeStructList!=null){
            sum=ndeStructList.size();
        }
        return sum;
    }

    public void setNdeStruct(NdeStruct ndeStruct) {
        this.ndeStruct = ndeStruct;
    }

    public void setNdeStructList(List ndeStructList) {
        this.ndeStructList = ndeStructList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
