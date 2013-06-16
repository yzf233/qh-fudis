package com.xx.platform.domain.model.database;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

import com.xx.platform.domain.service.*;

@Entity
@Table(name = "tableproperty")

@org.hibernate.annotations.Proxy(lazy = false)
public class Tableproperty extends DomainLogic implements java.lang.Cloneable,java.io.Serializable {
  private String id  ;
  private String dbtableid ;
  private String code ;
  private String name ;
  private String length ;
  private String datatype ;
  private boolean allownull ;
  private String defaultvalue ;
  private String indexfield ;
  private Boolean ispk ;
  private boolean dbfield = true;
  private String multfuction ;
  private String isorderby;
  private String isfiledata;
  private String asoindexfield;
 
  //ÎÄ¼þ×Öµä
  private String html;
  private String doc;
  private String xls;
  private String docx;
  private String xlsx;
  private String mht;
  private String txt;
  private String pdf;

  
  
  @Transient
  private String parseValue ;
    public boolean isAllownull() {
        return allownull;
    }

    public String getCode() {
        return code;
    }

    public String getDatatype() {
        return datatype;
    }
    public String getDbtableid() {
        return dbtableid;
    }
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public String getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }
    @Transient
    public String getParseValue() {
        return parseValue;
    }

  public String getIndexfield() {
    return indexfield;
  }

  public Boolean getIspk() {
    return ispk;
  }

  public boolean isDbfield() {
    return dbfield;
  }

  public String getMultfuction() {
    return multfuction;
  }

  public void setName(String name) {
        this.name = name;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDbtableid(String dbtableid) {
        this.dbtableid = dbtableid;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAllownull(boolean allownull) {
        this.allownull = allownull;
    }

    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void setParseValue(String parseValue) {
        this.parseValue = parseValue;
    }

  public void setIndexfield(String indexfield) {
    this.indexfield = indexfield;
  }

  public void setIspk(Boolean ispk) {
    this.ispk = ispk;
  }

  public void setDbfield(boolean dbfield) {
    this.dbfield = dbfield;
  }

  public void setMultfuction(String multfuction) {
    this.multfuction = multfuction;
  }

  public Object clone() throws CloneNotSupportedException {
        Tableproperty tableProperty = new Tableproperty();
        tableProperty.setId(this.getId());
        tableProperty.setName(this.getName());
        tableProperty.setCode(this.getCode());
        tableProperty.setAllownull(this.isAllownull());
        tableProperty.setDatatype(this.getDatatype());
        tableProperty.setDbtableid(this.getDbtableid());
        tableProperty.setDefaultvalue(this.getDefaultvalue());
        tableProperty.setLength(this.getLength());
        tableProperty.setParseValue(this.getParseValue());
        return tableProperty;

    }

public String getIsorderby() {
	return isorderby;
}

public void setIsorderby(String isorderby) {
	this.isorderby = isorderby;
}

public String getIsfiledata() {
	return isfiledata;
}

public void setIsfiledata(String isfiledata) {
	this.isfiledata = isfiledata;
}

public String getAsoindexfield() {
	return asoindexfield;
}

public void setAsoindexfield(String asoindexfield) {
	this.asoindexfield = asoindexfield;
}

public String getHtml() {
	return html;
}

public void setHtml(String html) {
	this.html = html;
}

public String getDoc() {
	return doc;
}

public void setDoc(String doc) {
	this.doc = doc;
}

public String getXls() {
	return xls;
}

public void setXls(String xls) {
	this.xls = xls;
}

public String getDocx() {
	return docx;
}

public void setDocx(String docx) {
	this.docx = docx;
}

public String getXlsx() {
	return xlsx;
}

public void setXlsx(String xlsx) {
	this.xlsx = xlsx;
}

public String getMht() {
	return mht;
}

public void setMht(String mht) {
	this.mht = mht;
}

public String getTxt() {
	return txt;
}

public void setTxt(String txt) {
	this.txt = txt;
}

public String getPdf() {
	return pdf;
}

public void setPdf(String pdf) {
	this.pdf = pdf;
}

}
