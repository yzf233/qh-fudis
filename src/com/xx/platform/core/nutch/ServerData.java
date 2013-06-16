package com.xx.platform.core.nutch;

import java.io.IOException;

/**
 * 服务器数据信息 ， 索引数量 ，分布式服务器索引数量 ，运行的server 数量， Segment数量等
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
public interface ServerData {
    public int getIndexRecNum() throws IOException ;
    public int getServerIndexRecNum() throws IOException ;
    public int getServerNum() throws IOException ;
    public int getSegmentsNum() throws IOException ;
    public int getServerSegmentsNum() throws IOException ;
}
