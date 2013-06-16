package com.xx.platform.plugin.url;

import java.util.List;

import com.xx.platform.dao.IBase;

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
public interface UrlGenerator {
    public List<String> generator() throws Exception ;
    public void generator(IBase service,InnerURL innerUrl) throws Exception ;
}
