package com.xx.platform.domain.model.database;

import javax.persistence.*;

import com.xx.platform.domain.service.*;

@Entity
@Table(name = "Apppackage")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class Apppackage extends DomainLogic {
  private Integer id ;
  private String parentid ;
  private Integer pid ;
  private String name = "";
  private String packagecode = "";
  private String memo = "";
  private String type = "";
  public String getType() {
    return type;
  }

  public String getPackagecode() {
    return packagecode;
  }

  public String getName() {
    return name;
  }

  public String getMemo() {
    return memo;
  }
  @Id
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_ID")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPackagecode(String packagecode) {
    this.packagecode = packagecode;
  }

  public void setType(String type) {
    this.type = type;
  }
    public Integer getPid() {
        return pid;
    }

    public String getParentid() {
        return parentid;
    }


    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public void setParentid(String parentid) {
       this.parentid = (parentid==null)?packagecode: parentid ;
    }
}
