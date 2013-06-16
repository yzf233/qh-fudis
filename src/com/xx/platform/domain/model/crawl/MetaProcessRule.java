package com.xx.platform.domain.model.crawl;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.*;
import java.io.IOException;
import org.apache.nutch.io.WritableUtils;
import java.io.DataOutput;
import java.io.DataInput;

/**
 * <p>Title: 元处理规则</p>
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
@Table(name = "metaprocessrule")
@org.hibernate.annotations.Proxy(lazy = false)
    public class MetaProcessRule
    extends DomainLogic implements org.apache.nutch.io.Writable {
  private String id;
  private String name;
  private String code;
  private String parseruleid;
  private Integer xdtype = new Integer(0); //替换:0;前缀:1;后缀:2 转换大写 3 ; 转换小写 4
  private String otext; //前缀或后缀字符
  private String xdreg; //替换的 正则或替换前的字符
  //   private String parentid;
  private String xdreplacetext; //替换后的字符

  //   private Integer xdorder; //元处理规则在处理中是按顺序执行的
  @Transient
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out, id);
    WritableUtils.writeCompressedString(out, name);
    WritableUtils.writeCompressedString(out, code);
    WritableUtils.writeCompressedString(out, parseruleid);
    WritableUtils.writeCompressedString(out, otext);
    WritableUtils.writeCompressedString(out, xdreg);
    WritableUtils.writeCompressedString(out, xdreplacetext);
    out.writeInt(xdtype);
  }

  @Transient
  public void readFields(DataInput in) throws IOException {
    id = WritableUtils.readCompressedString(in);
    name = WritableUtils.readCompressedString(in);
    code = WritableUtils.readCompressedString(in);
    parseruleid = WritableUtils.readCompressedString(in);
    otext = WritableUtils.readCompressedString(in);
    xdreg = WritableUtils.readCompressedString(in);
    xdreplacetext = WritableUtils.readCompressedString(in);
    xdtype = in.readInt();
  }

  @Id
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  public String getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public Integer getXdtype() {
    return xdtype;
  }

  public String getXdreplacetext() {
    return xdreplacetext;
  }

  public String getXdreg() {
    return xdreg;
  }

  public String getOtext() {
    return otext;
  }

  public String getParseruleid() {
    return parseruleid;
  }

  public void setXdtype(Integer xdtype) {
    this.xdtype = xdtype;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOtext(String otext) {
    this.otext = otext;
  }

  public void setXdreg(String xdreg) {
    this.xdreg = xdreg;
  }

  public void setXdreplacetext(String xdreplacetext) {
    this.xdreplacetext = xdreplacetext;
  }

  public void setParseruleid(String parseruleid) {
    this.parseruleid = parseruleid;
  }

}
