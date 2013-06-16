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
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MetaProcessRuleAction extends BaseAction {
    private List<MetaProcessRule> metaProcessRuleList;
    private List<ParserRule> parseRuleList ;
    private MetaProcessRule metaProcessRule;
    private int page=1;
    private int sum;

    public String list() {
        metaProcessRuleList = service.findByIObjectCType(MetaProcessRule.class,
                page,
                TEN_PAGE_SIZE);
        parseRuleList = service.findAllByIObjectCType(ParserRule.class) ;
        return Action.SUCCESS;
    }

    public String add() {

        if(metaProcessRule!=null)
        {
            service.saveIObject(metaProcessRule);
            SearchContext.reloadRules();
        }
        return Action.SUCCESS;
    }

    public String edit() {
        if (metaProcessRule != null && metaProcessRule.getId() != null)
        {
            metaProcessRule = (MetaProcessRule) service.getIObjectByPK(
                    MetaProcessRule.class, metaProcessRule.getId());
        }
        parseRuleList = service.findAllByIObjectCType(ParserRule.class) ;
        return Action.SUCCESS;
    }

    public String editDo() {
        if (request.getParameter("type") != null &&
            request.getParameter("type").equals("1")) {
            if (metaProcessRule != null && metaProcessRule.getId() != null) {
                service.deleteIObject(metaProcessRule);
            }
        } else {
            if (metaProcessRule != null && metaProcessRule.getId() != null) {
                service.updateIObject(metaProcessRule);
            }
        }
        SearchContext.reloadRules();
        return Action.SUCCESS;
    }


    public MetaProcessRule getMetaProcessRule() {
        return metaProcessRule;
    }

    public List getMetaProcessRuleList() {
        return metaProcessRuleList;
    }

    public int getSum() {
        sum = metaProcessRuleList.size();
        return sum;
    }

    public int getPage() {
        return page;
    }

    public List getParseRuleList() {
        return parseRuleList;
    }


    public void setSum(int sum) {
        this.sum = sum;
    }

    public void setMetaProcessRuleList(List metaProcessRuleList) {
        this.metaProcessRuleList = metaProcessRuleList;
    }

    public void setMetaProcessRule(MetaProcessRule metaProcessRule) {
        this.metaProcessRule = metaProcessRule;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setParseRuleList(List parseRuleList) {
        this.parseRuleList = parseRuleList;
    }

}
