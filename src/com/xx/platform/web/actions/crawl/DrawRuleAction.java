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
public class DrawRuleAction extends BaseAction {
    private List<DrawRule> drawRuleList;
    private DrawRule drawRule;
    private Integer sum=0;

    public String list() throws Exception {
        drawRuleList=service.findAllByIObjectCType(DrawRule.class);
        return Action.SUCCESS;
    }

    public String addDo() throws Exception {
        if(drawRule!=null){
            service.saveIObject(drawRule);
        }
        return Action.SUCCESS;
    }

    public DrawRule getDrawRule() {
        return drawRule;
    }

    public List getDrawRuleList() {
        return drawRuleList;
    }

    public Integer getSum() {
        if(drawRuleList!=null){
            sum=drawRuleList.size();
        }
        return sum;
    }

    public void setDrawRule(DrawRule drawRule) {
        this.drawRule = drawRule;
    }

    public void setDrawRuleList(List drawRuleList) {
        this.drawRuleList = drawRuleList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

}
