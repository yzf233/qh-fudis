package com.xx.platform.domain.model.database;

import com.xx.platform.domain.service.DomainLogic;

import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
@Table(name = "DOMAINMODEL")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class DomainModel extends DomainLogic{
  private String id  = "" ;
  private String name  = "" ;
  private String code  = "" ;
  private String tablename  = "" ;
  private String apppackage = "" ;
  private String memo  = "" ;
  private String dbid  = "" ;
  private String moduleok  = "" ;
  private String daook  = "" ;
  private String serviceok  = "" ;
  private String actionok  = "" ;
  private String listviewok  = "" ;
  private String formbeanok  = "" ;
  private String addviewok  = "" ;
  private String updateviewok  = "" ;
  private String removeviewok  = "" ;
  private String appid  = "" ;

  public String getActionok() {
    return actionok;
  }
  public String getAddviewok() {
    return addviewok;
  }
  public String getAppid() {
    return appid;
  }
  public String getApppackage() {
    return apppackage;
  }
  public String getCode() {
    return code;
  }
  public String getDaook() {
    return daook;
  }
  public String getDbid() {
    return dbid;
  }
  public String getFormbeanok() {
    return formbeanok;
  }
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
  public String getId() {
    return id;
  }
  public String getListviewok() {
    return listviewok;
  }
  public String getMemo() {
    return memo;
  }
  public String getModuleok() {
    return moduleok;
  }
  public String getName() {
    return name;
  }
  public String getRemoveviewok() {
    return removeviewok;
  }
  public String getServiceok() {
    return serviceok;
  }
  public String getTablename() {
    return tablename;
  }
  public String getUpdateviewok() {
    return updateviewok;
  }
  public void setUpdateviewok(String updateviewok) {
    this.updateviewok = updateviewok;
  }
  public void setTablename(String tablename) {
    this.tablename = tablename;
  }
  public void setServiceok(String serviceok) {
    this.serviceok = serviceok;
  }
  public void setRemoveviewok(String removeviewok) {
    this.removeviewok = removeviewok;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setModuleok(String moduleok) {
    this.moduleok = moduleok;
  }
  public void setMemo(String memo) {
    this.memo = memo;
  }
  public void setListviewok(String listviewok) {
    this.listviewok = listviewok;
  }
  public void setId(String id) {
    this.id = id;
  }
  public void setFormbeanok(String formbeanok) {
    this.formbeanok = formbeanok;
  }
  public void setDbid(String dbid) {
    this.dbid = dbid;
  }
  public void setDaook(String daook) {
    this.daook = daook;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public void setApppackage(String apppackage) {
    this.apppackage = apppackage;
  }
  public void setAppid(String appid) {
    this.appid = appid;
  }
  public void setAddviewok(String addviewok) {
    this.addviewok = addviewok;
  }
  public void setActionok(String actionok) {
    this.actionok = actionok;
  }

}
