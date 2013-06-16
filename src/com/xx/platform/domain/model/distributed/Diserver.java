package com.xx.platform.domain.model.distributed;

import java.io.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

import com.xx.platform.domain.service.*;

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
@Entity
@Table(name = "diserver")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
    public class Diserver
    extends DomainLogic implements org.apache.nutch.io.Writable{
  private String id;
  private String dname;
  private String ipaddress ;
  private Integer disport ;    //分布式端口
  private Integer conport ;    //连接端口
  private Integer dismport ;   //检索服务管理端口
  private Integer crawlmport ; //1 ,节点不响应定时任务
  private Boolean status = false;     //爬虫状态 UP/DOWN
  private String crawlstatus = "Not Running"; //爬虫运行状态  RUNNING/NOT RUNNING/IDLE
  private String serveros ;
  private String indexpath ;
  private Boolean serverstatus = false;
  private Integer threads = 10 ;
  private Integer depths = 1 ;
  private Integer push;
  @Transient
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out, id);
    WritableUtils.writeCompressedString(out, dname);
    WritableUtils.writeCompressedString(out, ipaddress);
    WritableUtils.writeCompressedString(out, serveros);
    WritableUtils.writeCompressedString(out, indexpath);
    WritableUtils.writeCompressedString(out, crawlstatus);
    out.writeInt(disport);
    out.writeInt(conport);
    out.writeInt(dismport);
    out.writeInt(threads);
    out.writeInt(depths);
    out.writeBoolean(serverstatus);
    out.writeBoolean(status);
    out.writeInt(push);
  }
  @Transient
  public void readFields(DataInput in) throws IOException {
    id = WritableUtils.readCompressedString(in);
    dname = WritableUtils.readCompressedString(in);
    ipaddress = WritableUtils.readCompressedString(in);
    serveros = WritableUtils.readCompressedString(in);
    indexpath = WritableUtils.readCompressedString(in);
    crawlstatus = WritableUtils.readCompressedString(in);
    disport = in.readInt();
    conport = in.readInt();
    dismport = in.readInt() ;
    threads = in.readInt();
    depths =  in.readInt();
    serverstatus = in.readBoolean();
    status = in.readBoolean();
    push=in.readInt();
  }
  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  public String getId() {
    return id;
  }

  public Integer getConport() {
    return conport;
  }

  public Integer getDisport() {
    return disport;
  }

  public String getDname() {
    return dname;
  }

  public String getIpaddress() {
    return ipaddress;
  }


  public String getServeros() {
    return serveros;
  }

  public String getIndexpath() {
    return indexpath;
  }

  public Integer getDismport() {
    return dismport;
  }

  public Boolean getServerstatus() {
    return serverstatus;
  }

  public Integer getThreads() {
    return threads;
  }

  public Integer getDepths() {
    return depths;
  }

  public Boolean getStatus() {
    return status;
  }

  public String getCrawlstatus() {
    return crawlstatus;
  }

  public Integer getCrawlmport() {
    return crawlmport;
  }

  public void setIpaddress(String ipaddress) {
    this.ipaddress = ipaddress;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDname(String dname) {
    this.dname = dname;
  }

  public void setDisport(Integer disport) {
    this.disport = disport;
  }

  public void setConport(Integer conport) {
    this.conport = conport;
  }

  public void setServeros(String serveros) {
    this.serveros = serveros;
  }

  public void setIndexpath(String indexpath) {
    this.indexpath = indexpath;
  }

  public void setDismport(Integer dismport) {
    this.dismport = dismport;
  }

  public void setServerstatus(Boolean serverstatus) {
    this.serverstatus = serverstatus;
  }

  public void setThreads(Integer threads) {
    this.threads = threads;
  }

  public void setDepths(Integer depths) {
    this.depths = depths!=null?depths:1;
  }

  public void setStatus(Boolean status) {
    this.status = status!=null?status:false;
  }

  public void setCrawlstatus(String crawlstatus) {
    this.crawlstatus = crawlstatus;
  }

  public void setCrawlmport(Integer crawlmport) {
    this.crawlmport = crawlmport;
  }
public Integer getPush() {
	return push;
}
public void setPush(Integer push) {
	this.push = push;
}

}
