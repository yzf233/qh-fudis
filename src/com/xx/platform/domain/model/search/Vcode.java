package com.xx.platform.domain.model.search;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

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
@Table(name = "vcode")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class Vcode  extends DomainLogic{

  private String id ;
  private String name ;
  private String xdcode ;
  private String xdclass ;
  private String xdclassname;
  private String xdtype ;
  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getXdclass() {
    return xdclass;
  }

  public String getXdclassname() {
    return xdclassname;
  }

  public String getXdcode() {
    return xdcode;
  }

  public String getXdtype() {
    return xdtype;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setXdclass(String xdclass) {
    this.xdclass = xdclass;
  }

  public void setXdclassname(String xdclassname) {
    this.xdclassname = xdclassname;
  }

  public void setXdcode(String xdcode) {
    this.xdcode = xdcode;
  }

  public void setXdtype(String xdtype) {
    this.xdtype = xdtype;
  }
}
