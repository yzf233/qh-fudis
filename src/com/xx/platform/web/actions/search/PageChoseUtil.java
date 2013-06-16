package com.xx.platform.web.actions.search;

import java.io.UnsupportedEncodingException;


/**
 * ��ҳ����
 *
 * <p>Company: xd-tech</p>
 *
 * @author qh
 * @version 1.0 2007-05-25
 */
public class PageChoseUtil {
    private static int maxLength = 10;
    private int start;          //��ʼλ��
    private int total;          //����
    private int pagesize;       // page size
    private String q;           //��ѯ�ؼ���
    private String doctype;

    public PageChoseUtil(int start, int total, String q,String doctype) {
        this(start, total,
             com.xx.platform.core.search.SearchImpl.HITS_PER_PAGE, q,doctype);
    }

    public PageChoseUtil(int start, int total, int pagesize, String q,String doctype) {
        this.start = start+1;
        this.total = total;
        this.q = q;
        this.pagesize = pagesize;
        this.doctype = doctype;
    }

    /**
     * ���ɷ�ҳ��html����
     * @return String
     * @throws UnsupportedEncodingException
     */
    public String pageChoseStr(String contextPath) throws UnsupportedEncodingException {
        StringBuffer str = new StringBuffer();
        int pagecount = getCount(total, pagesize); //��ҳ��
        int pagenow = getCount(start, pagesize); //��ǰҳ��
        //head
        str.append("<div class=\"pages\"><a href=\"search.dhtml?q=").append(java.net.URLEncoder.encode(q,"utf-8")).
                append("&ps=").append(pagesize).append("&s=").append(pagenow-2<0?0:(pagenow-2)*pagesize).append(getDocType()).append("\" class=\"pagesNum\">");
        if (pagenow > 1) {str.append("��һҳ");}
        str.append("</a>");

        //body
        int base = pagenow / maxLength;
        int tmp = 0;
        int listPagenow = 0;
        for (int j = (base == 0 ? base : base - 1); j <= base; j++) {
            tmp = j * maxLength;
            for (int i = 0; tmp + i < pagecount && i < maxLength; i++) {
                listPagenow = tmp + i + 1;
                if (listPagenow != pagenow) {
                    str.append("<a href=\"search.dhtml?q=").append(java.net.URLEncoder.encode(q,"utf-8")).append("&ps=").append(pagesize).append("&s=").append((listPagenow - 1) * pagesize).append(getDocType()).append("\">").append(listPagenow).append("</a>");
                } else {
                    str.append("<a href=\"#\" class=\"pagesOnA\">");str.append(listPagenow);str.append("</a> ");
                }
            }
        }

        //foot
        str.append("<a href=\"search.dhtml?q=").append(java.net.URLEncoder.encode(q,"utf-8")).append("&ps=").append(pagesize).append("&s=").append(pagenow + 1 >= pagecount ?(pagecount - 1) * pagesize :(pagenow) * pagesize).append(getDocType()).append("\" class=\"pagesNum\">");
        if (pagenow < pagecount) {str.append(" ��һҳ");}
        str.append("</a></div>");

        return str.toString();
    }

    /**
     * getDocType
     *
     * @return Object
     */
    private String getDocType() {
        if(doctype!=null&&doctype.trim().length()>0)
            return "&ty="+doctype;
        else
            return "";
    }


    /**
     * ���ص�ǰҳ��
     *
     * @param start int
     * @param pagesize int
     * @return int
     */
    private int getCount(int start, int pagesize) {
        int pagenow = 0;
        if (start % pagesize > 0) {
            pagenow = (int) (start / pagesize) + 1;
        } else {
            pagenow = (int) (start / pagesize);
        }
        return pagenow;
    }

public static void main(String[] a) throws Exception {
    PageChoseUtil b = new PageChoseUtil(0,43,"a",null);
//    System.out.println(b.pageChoseStr());
}

}
