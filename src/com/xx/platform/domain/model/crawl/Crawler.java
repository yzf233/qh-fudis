package com.xx.platform.domain.model.crawl;

import java.io.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

import com.xx.platform.domain.model.database.*;
import com.xx.platform.domain.service.*;

/**
 * <p>Title:爬虫列表 </p>
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
@Table(name = "crawler")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")

@org.hibernate.annotations.Proxy(lazy = false)
public class Crawler extends DomainLogic implements org.apache.nutch.io.Writable{
    private String id ;
    private String name ;
    private String urlreg ;   // URL地址 匹配规则
    private String parserules ;
    private String itextbody ;
    private String categoryid ; //分类ID
    private String dbtableid ;
    @Transient
    private Dbtable dbtable ; //数据表
    @Transient
    private String tempcode ; //临时 的 下级分类代码 ， 方便使用

    private String code;     //采集规则分类编码,对应索引中subDocType  qh 07/07/30
    public void write(DataOutput out) throws IOException {
      WritableUtils.writeCompressedString(out, id);
      WritableUtils.writeCompressedString(out, name);
      WritableUtils.writeCompressedString(out, urlreg);
      WritableUtils.writeCompressedString(out, parserules);
      WritableUtils.writeCompressedString(out, categoryid);
      WritableUtils.writeCompressedString(out, dbtableid);
      WritableUtils.writeCompressedString(out, itextbody);
    }
    @Transient
    public void readFields(DataInput in) throws IOException {
      id = WritableUtils.readCompressedString(in);
      name = WritableUtils.readCompressedString(in);
      urlreg = WritableUtils.readCompressedString(in);
      parserules = WritableUtils.readCompressedString(in);
      categoryid = WritableUtils.readCompressedString(in);
      dbtableid = WritableUtils.readCompressedString(in);
      itextbody = WritableUtils.readCompressedString(in);
  }

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

    public String getParserules() {
        return parserules;
    }

    public String getUrlreg() {
        return urlreg;
    }

    public String getCategoryid() {
        return categoryid;
    }
    @Transient
    public Dbtable getDbtable() {
        return dbtable;
    }

    public String getDbtableid() {
        return dbtableid;
    }

    public String getCode() {
        return code;
    }

  public String getItextbody() {
    return itextbody;
  }
  @Transient
  public String getTempcode() {
    return tempcode;
  }

  public void setId(String id) {
        this.id = id;
    }

    public void setUrlreg(String urlreg) {
        this.urlreg = urlreg;
    }

    public void setParserules(String parserules) {
        this.parserules = parserules;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public void setDbtable(Dbtable dbtable) {
        this.dbtable = dbtable;
    }

    public void setDbtableid(String dbtableid) {
        this.dbtableid = dbtableid;
    }

    public void setCode(String code) {
        this.code = code;
    }

  public void setItextbody(String itextbody) {
    this.itextbody = itextbody;
  }

  public void setTempcode(String tempcode) {
    this.tempcode = tempcode;
  }
}
