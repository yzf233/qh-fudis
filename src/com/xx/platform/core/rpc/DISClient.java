package com.xx.platform.core.rpc;

import java.net.InetSocketAddress;
import org.apache.nutch.util.NutchConf;
import org.apache.nutch.ipc.RPC;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexFieldImpl;
import com.xx.platform.core.nutch.WebDB;
import com.xx.platform.domain.model.crawl.Category;
import com.xx.platform.domain.model.crawl.Crawler;
import com.xx.platform.domain.model.crawl.MetaProcessRule;
import com.xx.platform.domain.model.crawl.ParserRule;
import com.xx.platform.domain.model.crawl.Proregion;
import com.xx.platform.domain.model.crawl.Urlfilterreg;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.util.constants.IbeaProperty;

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
 * RCP客户端
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
public class DISClient {
  private static String DIS_MANAGE_SERVER;
  private static int DIS_MANAGE_SERVER_PORT;

  private static InetSocketAddress defaultAddresses = null;
  private static InetSocketAddress bindAddresses = null;
  static {
    try {
      DIS_MANAGE_SERVER = NutchConf.get().get("server.manage.address",
                                              "127.0.0.1");
      DIS_MANAGE_SERVER_PORT =
          NutchConf.get().getInt("server.manage.port",
                                 SearchContext.MANAGE_SERVER_PORT);
      defaultAddresses = new
          InetSocketAddress(DIS_MANAGE_SERVER, DIS_MANAGE_SERVER_PORT);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

  }
  /**
   * 合并新索引
   * @return Urlfilterreg[]
   * @throws Exception
   */
  public static void addNewIndex(Diserver diserver) {
    if(bindAddresses==null)
      bindAddresses = new InetSocketAddress(diserver.getIpaddress() , diserver.getDismport()) ;
    try {
      ( (ClientInterface) RPC.getProxy(ClientInterface.class, bindAddresses)).
          addNewIndex(true);
    }
    catch (Exception ex) {
      IbeaProperty.log.info("合并异常，可能未启动分布式查询服务器管理服务："+ex.getMessage());
    }
  }

  /**
   * 站点信息
   * @return Urlfilterreg[]
   * @throws Exception
   */
  public static Diserver getDiserver(String serverip, int port) throws Exception{
    try {
      return ( (ServerInterface) RPC.getProxy(ServerInterface.class,
                                              defaultAddresses)).
          getBindSearchServer(serverip, port);
    }
    catch (Exception ex) {
      throw ex;
    }
  }

}
