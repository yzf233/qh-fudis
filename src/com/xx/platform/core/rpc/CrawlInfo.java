package com.xx.platform.core.rpc;

import java.io.*;
import java.util.*;

import org.apache.nutch.io.*;

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
public class CrawlInfo implements org.apache.nutch.io.Writable{
  public long time ;
  private String status ;
  private String threads ;
  private String depths ;
  private long crawledNum ;
  private long crawl_parse_num;
  private long crawl_speed;
  private long crawl_times;
  private long process_num ;
  private Date starttime = null ;

  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out , status) ;
    WritableUtils.writeCompressedString(out , threads) ;
    WritableUtils.writeCompressedString(out , depths) ;
    out.writeLong(time);
    out.writeLong(crawledNum);
    out.writeLong(crawl_parse_num);
    out.writeLong(crawl_speed);
    out.writeLong(crawl_times);
    out.writeLong(process_num);
  }

  public void readFields(DataInput in) throws IOException {
    status = WritableUtils.readCompressedString(in) ;
    threads = WritableUtils.readCompressedString(in) ;
    depths = WritableUtils.readCompressedString(in) ;
    time = in.readLong() ;
    crawledNum = in.readLong() ;
    crawl_parse_num = in.readLong();
    crawl_speed = in.readLong();
    crawl_times = in.readLong() ;
    process_num = in.readLong();
    if(crawl_times>0)
      starttime = new Date(crawl_times);
  }

  public String getDepths() {
    return depths;
  }

  public long getCrawledNum() {
    return crawledNum;
  }

  public String getStatus() {
    return status;
  }

  public String getThreads() {
    return threads;
  }

  public long getTime() {
    return time;
  }

  public long getCrawl_parse_num() {
    return crawl_parse_num;
  }

  public long getCrawl_speed() {
    return crawl_speed;
  }

  public long getCrawl_times() {
    return crawl_times;
  }

  public long getProcess_num() {
    return process_num;
  }

  public Date getStarttime() {
    return starttime;
  }

  public void setCrawledNum(long crawledNum) {
    this.crawledNum = crawledNum;
  }

  public void setDepths(String depths) {
    this.depths = depths;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setThreads(String threads) {
    this.threads = threads;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public void setCrawl_times(long crawl_times) {
    this.crawl_times = crawl_times;
  }

  public void setCrawl_speed(long crawl_speed) {
    this.crawl_speed = crawl_speed;
  }

  public void setCrawl_parse_num(long crawl_parse_num) {
    this.crawl_parse_num = crawl_parse_num;
  }

  public void setProcess_num(long process_num) {
    this.process_num = process_num;
  }

  public void setStarttime(Date starttime) {
    this.starttime = starttime;
  }

}
