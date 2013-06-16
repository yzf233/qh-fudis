package com.xx.platform.web.actions.system;

import java.util.List;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.dao.IDaoManager;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.util.tools.ipcheck.CheckIPUtil;
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
public class XdtechsiteAction extends BaseAction{
    private Xdtechsite xdtechsite = SearchContext.getXdtechsite();
    public Xdtechsite getXdtechsite() {
        return xdtechsite;
    }
    public String setting() throws Exception
    {
    	List<Xdtechsite> oldList=(List<Xdtechsite>)service.findAllByIObjectCType(Xdtechsite.class);
    	Xdtechsite old=oldList.remove(0);
    	String oldIp=old.getLocalip();
    	String newIp=xdtechsite.getLocalip();
    	if(oldIp!=newIp){
    		SearchContext.updateLocaip(oldIp, newIp, (IDaoManager)service);
    	}
        SearchContext.resetXdtechSite(xdtechsite , service);
        CheckIPUtil.init();
        message = "保存成功，访问者管理设置马上生效，其他参数<BR/>设置下一次启动采集生效" ;
        return Action.SUCCESS ;
    }
    public void setXdtechsite(Xdtechsite xdtechsite) {
        this.xdtechsite = xdtechsite;
    }
}
