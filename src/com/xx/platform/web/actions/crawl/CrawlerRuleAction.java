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
public class CrawlerRuleAction extends BaseAction {
    private List<CrawlerRule> crawlerRuleList;
    private CrawlerRule crawlerRule;
    private Integer sum=0;

    public String list() throws Exception {
        crawlerRuleList=service.findAllByIObjectCType(CrawlerRule.class);
        return Action.SUCCESS;
    }

    public String addDo() throws Exception {
        if(crawlerRule!=null){
            service.saveIObject(crawlerRule);
        }
        return Action.SUCCESS;

    }

    public CrawlerRule getCrawlerRule() {
        return crawlerRule;
    }

    public List getCrawlerRuleList() {
        return crawlerRuleList;
    }

    public Integer getSum() {
        if(crawlerRuleList!=null){
            sum=crawlerRuleList.size();
        }
        return sum;
    }

    public void setCrawlerRule(CrawlerRule crawlerRule) {
        this.crawlerRule = crawlerRule;
    }

    public void setCrawlerRuleList(List crawlerRuleList) {
        this.crawlerRuleList = crawlerRuleList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
