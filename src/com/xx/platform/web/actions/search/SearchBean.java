package com.xx.platform.web.actions.search;

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
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SearchBean {
    private long start;
    private long end;
    private long length;
    private long total;
    private float time;
    private int pagesize = SearchAction.HITS_PER_PAGE;
    private int docNum;
    private String queryStr ;
    private String UTF8QueryStr ;
    public int getDocNum() {
        return docNum;
    }

    public long getEnd() {
        return end;
    }

    public long getLength() {
        return length;
    }

    public int getPagesize() {
        return pagesize;
    }

    public long getStart() {
        return start;
    }

    public float getTime() {
        return time;
    }

    public long getTotal() {
        return total;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public String getUTF8QueryStr() {
        return UTF8QueryStr;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setDocNum(int docNum) {
        this.docNum = docNum;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public void setUTF8QueryStr(String UTF8QueryStr) {
        this.UTF8QueryStr = UTF8QueryStr;
    }
}
