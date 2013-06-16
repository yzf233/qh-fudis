package com.xx.platform.core.rpc;

import com.xx.platform.core.nutch.*;

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
public interface WebDBI extends ServiceInterface{
  public boolean addWebDB(WebDB webDb) throws Exception  ;
  public WebDB[] getWebDB_A(long start, long page_size) throws Exception ;
  public void reload() throws Exception  ;
  public void updateWebDB(WebDB webDb) throws Exception  ;
}
