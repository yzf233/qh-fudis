package com.xx.platform.plugin.url;

import java.net.MalformedURLException;

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
public interface InnerURL {
    public void setUrl(String url) throws MalformedURLException;
    public void setExtra(String extra) throws Exception ;
    public void putScore(int lev) ;
}
