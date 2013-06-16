package com.xx.platform.core.search;

import java.util.*;

import com.xx.platform.web.actions.search.SearchBean;

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
public class SearchList extends ArrayList {
    private int start;
    private int end;
    private long total;
    private float time;
    private String queryString;
    private SearchBean searchBean;
    public SearchList() {
        super();
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public float getTime() {
        return time;
    }

    public long getTotal() {
        return total;
    }

    public SearchBean getSearchBean() throws Exception {
        searchBean = new SearchBean();
        searchBean.setTotal(getTotal());
        searchBean.setStart(start + 1);
        searchBean.setEnd(getEnd());
        searchBean.setTime(getTime());
        if (searchBean.getTime() < 0.001) {
            searchBean.setTime((float) 0.001);
        }
        searchBean.setQueryStr(queryString);
        searchBean.setUTF8QueryStr(java.net.URLEncoder.encode(queryString,
                "UTF-8"));

        return searchBean;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setSearchBean(SearchBean searchBean) {
        this.searchBean = searchBean;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}
