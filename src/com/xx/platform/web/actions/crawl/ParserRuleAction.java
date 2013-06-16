package com.xx.platform.web.actions.crawl;

import java.util.*;
import java.util.regex.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.crawl.*;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.web.actions.*;

import org.apache.nutch.protocol.*;
import org.hibernate.criterion.*;
import org.apache.nutch.parse.html.HtmlParser;
import org.apache.nutch.parse.Parse;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: 北京线点科技有限公司</p>
 *
 * @author iceworld
 * @version 1.0
 */
public class ParserRuleAction extends BaseAction {
    private List<ParserRule> parserRuleList;
    private List<Crawler> crawlerList ;
    private List<IndexFieldImpl> indexFieldList ;
    private List<Tableproperty> propertyList ;
    private ParserRule parserRule;
    private Dbtable dbtable;
    private Integer sum = 0;
    private int page = 1;
    private String id;
    public String list() throws Exception {
        parserRuleList = service.findByIObjectCType(ParserRule.class, page,
                TEN_PAGE_SIZE);
        propertyList = service.findAllByIObjectCType(Tableproperty.class) ;
        crawlerList = service.findAllByIObjectCType(Crawler.class);
        indexFieldList = service.findAllByIObjectCType(IndexFieldImpl.class) ;
        return Action.SUCCESS;

    }

    //采集规则下的所有抽取规则
    public String listByID() throws Exception {
        parserRuleList = null;
         String pid = id;
         if(pid != null && pid.trim().length()>0) {
             request.setAttribute("pid",pid);
             parserRuleList = service.findAllByCriteria(DetachedCriteria.
                     forClass(ParserRule.class).add(Restrictions.eq("parentid",pid)));
         }
         propertyList = service.findAllByIObjectCType(Tableproperty.class);
         crawlerList = service.findAllByIObjectCType(Crawler.class);
         indexFieldList = service.findAllByIObjectCType(IndexFieldImpl.class);
         Crawler c = (Crawler) service.getIObjectByPK(Crawler.class,pid);
         request.setAttribute("cname",c.getName());
         request.setAttribute("cid",c.getId());
         return Action.SUCCESS;
    }

    //抽取规则测试
    public String test() throws Exception {
        String pid = request.getParameter("pid");
        String url = request.getParameter("url")==null?"":request.getParameter("url");
        if(pid != null && pid.trim().length()>0) {
            //检查url是否匹配
            Crawler c = (Crawler) service.getIObjectByPK(Crawler.class,pid);
            if(c!=null && c.getUrlreg()!=null){
                java.util.regex.Matcher m1 = java.util.regex.Pattern.compile(c.getUrlreg()).matcher(url);
                if(m1.find()){
                    parserRuleList = service.findAllByCriteria(DetachedCriteria.
                     forClass(ParserRule.class).add(Restrictions.eq("parentid",pid)));
                     byte[] content = getContent(url);
                     Parse parse = new HtmlParser().getParse(new Content(url,url,
                                                        content,"text/html",
                                                        new Properties()));

                     if(content!=null){
                         java.util.regex.Pattern pattern;
                         java.util.regex.Matcher matcher;
                         java.util.Map<String,String> testmap = new java.util.HashMap<String,String>();
                         for(ParserRule parseRule : parserRuleList){
                             pattern = Pattern.compile(parseRule.getValue(),
                                      Pattern.CASE_INSENSITIVE);
                             //System.out.println(new String(content));
                             matcher = pattern.matcher(new String(content,parse.getData().get("CharEncodingForConversion")));
                             if (matcher.find()) {
                                 if (matcher.groupCount() >= 1)
                                     testmap.put(parseRule.getName(),matcher.group(1));
                                 else
                                     testmap.put(parseRule.getName(),"<font color=red>没有找到匹配项</font>");
                             }else
                                 testmap.put(parseRule.getName(),"<font color=red>没有找到匹配项</font>");
                         }
                         request.setAttribute("testmap",testmap);
                     }else{
                         message = "抓取页面错误,请检查url,或稍后再试!";
                     }
                }else
                    message = "url不匹配,请重新填写!";

            }
        }
        return Action.SUCCESS;
    }



    public String addDo() throws Exception {
        if (parserRule != null) {
            service.saveIObject(parserRule);
            SearchContext.reloadRules();
        }
        if(request.getParameter("to")!=null && request.getParameter("to").equals("patest"))
            return "patest";
        else
            return Action.SUCCESS;

    }

    public String edit() throws Exception {
        if ((parserRule != null && parserRule.getId() != null)) {
            parserRule = (ParserRule) service.getIObjectByPK(ParserRule.class,
                    parserRule.getId());
        } else if (request.getParameter("id") != null) {
            parserRule = (ParserRule) service.getIObjectByPK(ParserRule.class,
                    request.getParameter("id"));
        }
        crawlerList = service.findAllByIObjectCType(Crawler.class);
        indexFieldList = service.findAllByIObjectCType(IndexFieldImpl.class) ;
        if(parserRule!=null && parserRule.getTablepropertyid()!=null && parserRule.getTablepropertyid().trim().length()>0)
        {
            Tableproperty property = (Tableproperty) service.getIObjectByPK(
                    Tableproperty.class, parserRule.getTablepropertyid());
            dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class,
                    property.getDbtableid());
        }else
        {
            dbtable = new Dbtable();
            dbtable.setTableproperty(new HashSet());
        }
        if(request.getParameter("to")!=null && request.getParameter("to").equals("patest"))
           return "patest";
       else
           return Action.SUCCESS;


    }

    public String editDo() throws Exception {
        if (request.getParameter("type") != null &&
            request.getParameter("type").equals("1")) {
            if (parserRule != null && parserRule.getId() != null) {
                service.deleteIObject(parserRule);
            }
        } else {
            if (parserRule != null && parserRule.getId() != null) {
                service.updateIObject(parserRule);
            }
        }
        SearchContext.reloadRules();
        if(request.getParameter("to")!=null && request.getParameter("to").equals("patest"))
            return "patest";
        else
            return Action.SUCCESS;

    }

    private byte[] getContent(String url){
        Protocol protocol = null;
        try {
            protocol = ProtocolFactory.getProtocol(url);
        } catch (ProtocolNotFound ex) {
            return null;
        }
        ProtocolOutput output = protocol.getProtocolOutput(url);
        Content content = output.getContent();
        if(output.getStatus().getCode()==ProtocolStatus.SUCCESS)
            return content.getContent();
        else return null;
    }

    public ParserRule getParserRule() {
        return parserRule;
    }

    public List getParserRuleList() {
        return parserRuleList;
    }

    public Integer getSum() {
        if (parserRuleList != null) {
            sum = parserRuleList.size();
        }
        return sum;
    }

    public List getCrawlerList() {
        return crawlerList;
    }

    public int getPage() {
        return page;
    }

    public List getIndexFieldList() {
        return indexFieldList;
    }

    public Dbtable getDbtable() {
        return dbtable;
    }

    public List getPropertyList() {
        return propertyList;
    }

    public String getId() {
        return id;
    }


    public void setParserRule(ParserRule parserRule) {
        this.parserRule = parserRule;
    }

    public void setParserRuleList(List parserRuleList) {
        this.parserRuleList = parserRuleList;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void setCrawlerList(List crawlerList) {
        this.crawlerList = crawlerList;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setIndexFieldList(List indexFieldList) {
        this.indexFieldList = indexFieldList;
    }

    public void setDbtable(Dbtable dbtable) {
        this.dbtable = dbtable;
    }

    public void setPropertyList(List propertyList) {
        this.propertyList = propertyList;
    }

    public void setId(String id) {
        this.id = id;
    }


}
