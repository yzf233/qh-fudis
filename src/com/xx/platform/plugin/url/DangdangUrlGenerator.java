package com.xx.platform.plugin.url;

import java.util.*;

import com.xx.platform.dao.*;

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
public class DangdangUrlGenerator implements UrlGenerator{
    public List generator() throws Exception {
//        "http://product.dangdang.com/product.aspx?product_id=9269918"
        List urlList = new ArrayList() ;
        StringBuffer url = new StringBuffer("http://product.dangdang.com/product.aspx?product_id=") ;
        for(int i=9269001 ; i<9269918 ; i++)
        {
            url = new StringBuffer("http://product.dangdang.com/product.aspx?product_id=") ;
            urlList.add(url.append(i).toString()) ;
        }
        return urlList;
    }

    public void generator(IBase service,InnerURL innerUrl) throws Exception {
    }

}
