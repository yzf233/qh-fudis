package com.xx.platform.domain.model.crawl;

import java.io.*;
import java.util.regex.*;
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
@Table(name = "urlfilterreg")
@org.hibernate.annotations.Proxy(lazy = false)
public class Urlfilterreg extends DomainLogic implements org.apache.nutch.io.Writable{

    private String id ;          //����ID
    private String xdname ;      //��������
    private String xdcode ;      //�������
    private Integer filtertype ; //���˷�ʽ    0:���� 1:����
    private Integer xdprocess ; //����ʽ     0:���� ����filterreg�����URL���˵� 1:���� ����filterreg�����URL��������ַ��
    private String filterreg =""; //�������
    private Integer xsource = new Integer(1);  //��Դ 0:��ڵ�ַ 1:��̨���
    @Transient
    private Pattern pattern ;
    private String crawlerid ;
    @Transient
    public void write(DataOutput out) throws IOException {
      WritableUtils.writeCompressedString(out , id) ;
      WritableUtils.writeCompressedString(out , xdname) ;
      WritableUtils.writeCompressedString(out , xdcode) ;
      WritableUtils.writeCompressedString(out , filterreg) ;
      WritableUtils.writeCompressedString(out , crawlerid) ;
      out.writeInt(filtertype);
      out.writeInt(xdprocess);
      out.writeInt(xsource!=null?xsource:1);
    }
    @Transient
    public void readFields(DataInput in) throws IOException {
      id = WritableUtils.readCompressedString(in) ;
      xdname = WritableUtils.readCompressedString(in) ;
      xdcode = WritableUtils.readCompressedString(in) ;
      filterreg = WritableUtils.readCompressedString(in) ;
      crawlerid = WritableUtils.readCompressedString(in) ;
      filtertype = in.readInt() ;
      xdprocess = in.readInt() ;
      xsource = in.readInt() ;
    }
    public String getFilterreg() {
        return filterreg;
    }

    public Integer getFiltertype() {
        return filtertype;
    }
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")

    public String getId() {
        return id;
    }

    public String getXdcode() {
        return xdcode;
    }

    public String getXdname() {
        return xdname;
    }

    public Integer getXdprocess() {
        return xdprocess;
    }

    public Integer getXsource() {
        return xsource;
    }
    @Transient
    public Pattern getPattern() {
        return pattern;
    }

  public String getCrawlerid() {
    return crawlerid;
  }

  public void setFilterreg(String filterreg) {
        this.filterreg = filterreg;
    }

    public void setFiltertype(Integer filtertype) {
        this.filtertype = filtertype;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setXdcode(String xdcode) {
        this.xdcode = xdcode;
    }

    public void setXdname(String xdname) {
        this.xdname = xdname;
    }

    public void setXdprocess(Integer xdprocess) {
        this.xdprocess = xdprocess;
    }

    public void setXsource(Integer xsource) {
        this.xsource = xsource;
    }

    public Pattern setPattern(Pattern pattern) {
        return this.pattern = pattern;
    }

  public void setCrawlerid(String crawlerid) {
    this.crawlerid = crawlerid;
  }

}
