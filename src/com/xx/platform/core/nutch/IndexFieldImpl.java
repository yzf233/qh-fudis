package com.xx.platform.core.nutch;

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
 * <p>Description: IndexMore 索引字段映射说明 </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
@Entity
@Table(name = "indexfield")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
    public class IndexFieldImpl
    extends DomainLogic implements IndexField, org.apache.nutch.io.Writable {
  private String id;
  private String name;
  private String code;
  private int dtype; //dataType
  private int itype; //indexType ;
  @Transient
  private CrawlRule crawlRule;
  private boolean isindex = true;
  private boolean isstorge = true;                //2009-8-27修正功能，是否存储功能作为 是否默认检索字段功能使用 jaddy0302 , 默认为否，即不作为默认检索字段，系统默认的检索字段为 content 和 title
  private boolean istoken = true;
  private boolean isfilecontent = false;
  private boolean isencrypt = false;//是否需要加密
  private int boost =1;
  private int tokentype;//分词算法类别
  private String tokenString;//分词字符
  private String parsereg ; //默认使用的抽取规则
  public IndexFieldImpl(){}
  public IndexFieldImpl(String name , String code , int tokentype , boolean isstorge)
  {
	  this.name = name ;
	  this.code = code ;
	  this.dtype = dtype ;
	  this.tokentype = tokentype ;
	  this.isstorge = isstorge ;
  }
  @Transient
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out, id);
    WritableUtils.writeCompressedString(out, name);
    WritableUtils.writeCompressedString(out, code);
    WritableUtils.writeCompressedString(out, parsereg);
    WritableUtils.writeCompressedString(out, tokenString);
    out.writeInt(dtype);
    out.writeInt(itype);
    out.writeBoolean(isindex);
    out.writeBoolean(isstorge);
    out.writeBoolean(istoken);
    out.writeBoolean(isfilecontent);
    out.writeBoolean(isencrypt);
    out.writeInt(boost);
    out.writeInt(tokentype);
  }
  

  
  @Transient
  public void readFields(DataInput in) throws IOException {
    id = WritableUtils.readCompressedString(in);
    name = WritableUtils.readCompressedString(in);
    code = WritableUtils.readCompressedString(in);
    parsereg = WritableUtils.readCompressedString(in);
    tokenString = WritableUtils.readCompressedString(in);
    dtype = in.readInt();
    itype = in.readInt();
    isindex = in.readBoolean();
    isstorge = in.readBoolean();
    istoken = in.readBoolean();
    isfilecontent=in.readBoolean();
    isencrypt = in.readBoolean();
    boost = in.readInt();
    tokentype=in.readInt();
  }

  public String getCode() {
    return code;
  }

  @Transient
  public CrawlRule getCrawlRule() {
    return crawlRule;
  }

  @Transient
  public int getDataType() {
    return dtype;
  }

  @Transient
  public int getIndexType() {
    return itype;
  }

  public String getName() {
    return name;
  }

  public int getDtype() {
    return dtype;
  }

  @Id
  //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  public String getId() {
    return id;
  }

  @Transient
  public boolean isIndex() throws Exception {
    return isindex;
  }

  public boolean isIsindex() {
    return isindex;
  }

  public boolean isIsstorge() {
    return isstorge;
  }

  public boolean isIstoken() {
    return istoken;
  }

  public int getItype() {
    return itype;
  }

  public String getParsereg() {
    return parsereg;
  }

  public int getBoost() {
    return boost;
  }

  @Transient
  public boolean isStorge(){
    return isstorge;
  }

  @Transient
  public boolean isToken(){
    return istoken;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setDtype(int dtype) {
    this.dtype = dtype;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setItype(int itype) {
    this.itype = itype;
  }

  public void setIstoken(boolean istoken) {
    this.istoken = istoken;
  }

  public void setIsstorge(boolean isstorge) {
    this.isstorge = isstorge;
  }

  public void setIsindex(boolean isindex) {
    this.isindex = isindex;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParsereg(String parsereg) {
    this.parsereg = parsereg;
  }

  public void setBoost(int boost) {
    this.boost = boost;
  }

public boolean isIsfilecontent() {
	return isfilecontent;
}

public void setIsfilecontent(boolean isfilecontent) {
	this.isfilecontent = isfilecontent;
}

public int getTokentype() {
	return tokentype<1?1:tokentype;
}

public void setTokentype(int tokentype) {
	this.tokentype = tokentype;
}

public String getTokenString() {
	return tokenString;
}

public void setTokenString(String tokenString) {
	this.tokenString = tokenString;
}
public boolean isIsencrypt() {
	return isencrypt;
}
public void setIsencrypt(boolean isencrypt) {
	this.isencrypt = isencrypt;
}


}
