package com.xx.platform.core.nutch;

import org.apache.nutch.db.IWebDBWriter;
import java.io.IOException;
import org.apache.nutch.db.Page;
import org.apache.nutch.db.Link;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import java.net.*;
import java.util.Date;
import java.util.regex.Pattern;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.domain.model.crawl.Urlfilterreg;
import com.xx.platform.util.tools.MD5;

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
public class WebDBWriter implements IWebDBWriter {

    public void close() throws IOException {
        ; //啥也不做
    }
    private Urlfilterreg urlFilter ;
    private java.util.regex.Pattern pattern ;
    private java.util.regex.Matcher matcher ;
    private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
    private boolean isAllowed(String url)
    {//是否允许将URL地址加入到地址库
        boolean isAllowed = false ;
        for(int i=0 ; url!=null && SearchContext.getUrlFilterList()!=null && i<SearchContext.getUrlFilterList().size() ; i++)
        {
            urlFilter = (Urlfilterreg)SearchContext.getUrlFilterList().get(i) ;
            if(urlFilter.getXdprocess()!=null)
            {
                if(urlFilter.getFiltertype()!=null && urlFilter.getFiltertype().intValue()==1)
                {
                    pattern = urlFilter.getPattern()!=null ? urlFilter.getPattern(): urlFilter.setPattern(Pattern.compile(urlFilter.getFilterreg() , Pattern.CASE_INSENSITIVE)) ;
                    matcher = pattern.matcher(url) ;
                    if(matcher.find())
                    {
                         if(urlFilter.getXdprocess().intValue()==1)
                             isAllowed = true ;
                         else
                         {
                             isAllowed = false;
                             break ;
                         }
                    }
                }else if(urlFilter.getFiltertype()!=null && urlFilter.getFiltertype().intValue()==0)
                {
                    if(url.indexOf(urlFilter.getFilterreg())>=0)
                    {
                        if(urlFilter.getXdprocess().intValue()==1)
                            isAllowed = true ;
                        else
                        {
                            isAllowed = false;
                             break ;
                        }
                    }
                }
            }
        }
        return isAllowed ;
    }
    private boolean isDecline(String url)
    {//是否禁止将URL地址加入到地址库
        boolean isDecline = false ;
        for(int i=0 ; url!=null && SearchContext.getUrlFilterList()!=null && i<SearchContext.getUrlFilterList().size() ; i++)
        {
            urlFilter = (Urlfilterreg)SearchContext.getUrlFilterList().get(i) ;
            if(urlFilter.getXdprocess()!=null)
            {
                if(urlFilter.getFiltertype()!=null && urlFilter.getFiltertype().intValue()==1)
                {
                    pattern = Pattern.compile(urlFilter.getFilterreg() , Pattern.CASE_INSENSITIVE) ;
                    matcher = pattern.matcher(url) ;
                    if(matcher.find())
                    {
                        if(urlFilter.getXdprocess().intValue()==0)
                        {
                            isDecline = true;
                            break ;
                        }
                    }
                }else if(urlFilter.getFiltertype()!=null && urlFilter.getFiltertype().intValue()==0)
                {
                    if(url.indexOf(urlFilter.getFilterreg())>=0)
                    {
                        if(urlFilter.getXdprocess().intValue()==0)
                        {
                            isDecline = true;
                            break ;
                        }
                    }
                }
            }
        }

        return isDecline ;
    }
    public void addPage(Page page) throws IOException {
        if(page==null || page.getURL()==null || page.getURL().toString().length()<=7)
            return ;
        //WebDB webDb = new WebDB(page) ;
        addWebDB(page) ;
    }

    public WebDB addWebDB(Page page) throws IOException {
            if(page==null || page.getURL()==null || page.getURL().toString().length()<=7)
                return null;
            WebDB webDb = new WebDB(page) ;
            return addWebDB(webDb) ;
    }

    public WebDB addWebDB(WebDB webDb) throws IOException {

            if(SearchContext.getXdtechsite().getUrlfilterreg() && !webDb.isIsFilter())
            {//地址过滤流程处理 ， 不符合所有规则检查的地址将被忽略掉
                if(!isAllowed(webDb.getUrl()))
                    return null;
            }else
            {//地址过滤流程处理 ， 未被过滤的地址都允许加入到地址库中
                if(isDecline(webDb.getUrl()))
                {
                    return null;
                }
            }
            {
                if (1==1) {

                    webDb.setNextfetch(new Date().getTime() +
                                       (long) SearchContext.getXdtechsite().
                                       getUpperiod() * 1000 * 60 * 60 * 24);
//            System.out.println(webDb.toString());
                    try {
                        if(webDbAdminTool.addWebDB(webDb))
                            return webDb;
                        else
                            return null;
                    }
                    catch (Exception ex) {
                      throw new IOException(ex.getMessage()) ;
                    }
//                SearchContext.getDataHandler().saveIObject(webDb);
                }
            }
            return null;
    }
    public void addPageWithScore(Page page) throws IOException {
        addPage(page);
    }

    public void addPageIfNotPresent(Page page) throws IOException {
        addPage(page);
    }

    public void addPageIfNotPresent(Page page, Link link) throws IOException {
        addPage(page);
    }

    public void deletePage(String url) throws IOException {
        try {
            SearchContext.getDataHandler().execByHQL("FROM WebDB WHERE url='" +
                    url + "'");
        } catch (Exception ex) {
            throw new IOException(ex.getMessage());
        }

    }

    public void addLink(Link link) throws IOException {
        ; //啥也不做
    }
}
