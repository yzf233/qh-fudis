package com.xx.platform.domain.model.database;

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
@Table(name = "MODELPROPERTY")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class Modelproperty {
    private String id = "";
    private String tableid = "";
    private String code = "";
    private String name = "";
    private String cname = "";
    private String length = "";
    private String type = "";
    private String isnull = "";
    private String ispk = "";
    private String isfk = "";
    private String fktable = "";
    private String fkfield = "";
    private String issplit = "";
    private String islistfield = "";
    private String isaddfield = "";
    private String isupdatefield = "";
    private String addnullver = "";
    private String updatenullver = "";
    private String addreg = "";
    private String updatereg = "";
    private String comtype = "";
    private Integer sequen = new Integer(0);
    public String getAddnullver() {
        return addnullver;
    }

    public String getAddreg() {
        return addreg;
    }

    public String getCname() {
        return cname;
    }

    public String getComtype() {
        return comtype;
    }

    public String getCode() {
        return code;
    }

    public String getFkfield() {
        return fkfield;
    }

    public String getFktable() {
        return fktable;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
    public String getId() {
        return id;
    }

    public String getIsaddfield() {
        return isaddfield;
    }

    public String getIsfk() {
        return isfk;
    }

    public String getIsnull() {
        return isnull;
    }

    public String getIspk() {
        return ispk;
    }

    public String getIsupdatefield() {
        return isupdatefield;
    }

    public String getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getUpdatenullver() {
        return updatenullver;
    }

    public String getUpdatereg() {
        return updatereg;
    }

    public void setUpdatereg(String updatereg) {
        this.updatereg = updatereg;
    }

    public void setUpdatenullver(String updatenullver) {
        this.updatenullver = updatenullver;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setIsupdatefield(String isupdatefield) {
        this.isupdatefield = isupdatefield;
    }

    public void setIsnull(String isnull) {
        this.isnull = isnull;
    }

    public void setIslistfield(String islistfield) {
        this.islistfield = islistfield;
    }

    public void setIsfk(String isfk) {
        this.isfk = isfk;
    }

    public void setIsaddfield(String isaddfield) {
        this.isaddfield = isaddfield;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFktable(String fktable) {
        this.fktable = fktable;
    }

    public void setFkfield(String fkfield) {
        this.fkfield = fkfield;
    }

    public void setAddnullver(String addnullver) {
        this.addnullver = addnullver;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIslistfield() {
        return islistfield;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getTableid() {
        return tableid;
    }

    public Integer getSequen() {
        return sequen;
    }

    public String getIssplit() {
        return issplit;
    }

    public void setComtype(String comtype) {
        this.comtype = comtype;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public void setAddreg(String addreg) {
        this.addreg = addreg;
    }

    public void setIspk(String ispk) {
        this.ispk = ispk;
    }

    public void setSequen(Integer sequen) {
        this.sequen = sequen;
    }

    public void setIssplit(String issplit) {
        this.issplit = issplit;
    }

}
