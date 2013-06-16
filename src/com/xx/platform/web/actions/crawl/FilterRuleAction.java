package com.xx.platform.web.actions.crawl;

import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.SearchContext;
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
public class FilterRuleAction extends BaseAction {
    private List<FilterRule> filterRuleList;
    private FilterRule filterRule;
    private Integer sum=0;
    private int  page = 1;

    public String list() throws Exception {
        filterRuleList=service.findByIObjectCType(FilterRule.class, page , THR_PAGE_SIZE);
        return Action.SUCCESS;

    }

    public String addDo() throws Exception {
        if(filterRule!=null){
            service.saveIObject(filterRule);
            SearchContext.reloadRules();
        }
        return Action.SUCCESS;

    }
    public String edit() throws Exception {
        if((filterRule!=null && filterRule.getId()!=null))
        {
            filterRule = (FilterRule) service.getIObjectByPK(FilterRule.class,filterRule.getId());
        }else if(request.getParameter("id")!=null)
        {
            filterRule = (FilterRule) service.getIObjectByPK(FilterRule.class,request.getParameter("id"));
        }
        return Action.SUCCESS;

    }
    public String editDo() throws Exception {
            if(request.getParameter("type")!=null && request.getParameter("type").equals("1"))
            {
                if (filterRule != null && filterRule.getId() != null) {
                    service.deleteIObject(filterRule);
                }
            }else
            {
                if (filterRule != null && filterRule.getId() != null) {
                    service.updateIObject(filterRule);
                }
            }
            SearchContext.reloadRules();
            return Action.SUCCESS;
    }
    public FilterRule getFilterRule() {
        return filterRule;
    }

    public List getFilterRuleList() {
        return filterRuleList;
    }

    public Integer getSum() {
        if(filterRuleList!=null){
            sum=filterRuleList.size();
        }
        return sum;
    }

    public int getPage() {
        return page;
    }

    public void setFilterRule(FilterRule filterRule) {
        this.filterRule = filterRule;
    }

    public void setFilterRuleList(List filterRuleList) {
        this.filterRuleList = filterRuleList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setPage(int page) {
        this.page = page;
    }

}
