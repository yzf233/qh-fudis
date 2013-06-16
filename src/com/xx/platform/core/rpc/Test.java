package com.xx.platform.core.rpc;

import org.apache.nutch.searcher.NutchBean;
import org.apache.nutch.searcher.DistributedSearch;
import java.io.*;
import java.net.InetSocketAddress;
import org.apache.nutch.searcher.Query;
import org.apache.nutch.searcher.Hit;
import org.apache.nutch.searcher.HitDetails;
import java.lang.reflect.Method;
import org.apache.nutch.ipc.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.IndexFieldImpl;
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
public class Test {


    public static class Client {

        InetSocketAddress[] defaultAddresses = new InetSocketAddress[] {new
                                               InetSocketAddress("localhost",
                8232)};

        public  DBList getFetchList() throws Exception
        {
          return ((ServerInterface) RPC.getProxy(ServerInterface.class, defaultAddresses[0])).getFetchList();
        }
        public IndexFieldImpl[] getIndexList() throws Exception
        {
          return ((ServerInterface) RPC.getProxy(ServerInterface.class, defaultAddresses[0])).indexFieldList();
        }
    }
    public static void addIndex(IndexFieldImpl indexfield)throws Exception
    {
        InetSocketAddress[] defaultAddresses = new InetSocketAddress[] {new
                InetSocketAddress("192.168.1.101",
  8232)};
    }


    /** Runs a search server. */
    public static void main(String[] args) throws Exception {
    	IndexFieldImpl indexfield=new IndexFieldImpl();
    	indexfield.setName("123");
    	indexfield.setCode("123");
    	addIndex(indexfield);
        
        
    }
}
