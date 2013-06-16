package com.xx.platform.domain.model.crawl;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

import com.xx.platform.domain.service.DomainLogic;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.nutch.io.WritableUtils;

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
@Table(name = "proregion")
@org.hibernate.annotations.Proxy(lazy = false)
public class Proregion  extends DomainLogic  implements org.apache.nutch.io.Writable{
    private String id ;
    private String sname ;
    private String tarurl ;
    private String username ;
    private String password ;
    private Boolean hascode ;
    private String codeurl ;
    private String usernamefieldname ;
    private String passwordfieldname ;
    private String codefieldname ;

    public void write(DataOutput out) throws IOException {
      WritableUtils.writeCompressedString(out, id);
      WritableUtils.writeCompressedString(out, sname);
      WritableUtils.writeCompressedString(out, tarurl);
      WritableUtils.writeCompressedString(out, username);
      WritableUtils.writeCompressedString(out, password);
      WritableUtils.writeCompressedString(out, codeurl);
      WritableUtils.writeCompressedString(out, usernamefieldname);
      WritableUtils.writeCompressedString(out, passwordfieldname);
      WritableUtils.writeCompressedString(out, codefieldname);
      out.writeBoolean(hascode);
    }

    public void readFields(DataInput in) throws IOException {
      id = WritableUtils.readCompressedString(in);
      sname = WritableUtils.readCompressedString(in);
      tarurl = WritableUtils.readCompressedString(in);
      username = WritableUtils.readCompressedString(in);
      password = WritableUtils.readCompressedString(in);
      codeurl = WritableUtils.readCompressedString(in);
      usernamefieldname = WritableUtils.readCompressedString(in);
      passwordfieldname = WritableUtils.readCompressedString(in);
      codefieldname = WritableUtils.readCompressedString(in);
      hascode = in.readBoolean();
    }

    public String getCodeurl() {
        return codeurl;
    }

    public Boolean getHascode() {
        return hascode;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getSname() {
        return sname;
    }

    public String getTarurl() {
        return tarurl;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordfieldname() {
        return passwordfieldname;
    }

    public String getCodefieldname() {
        return codefieldname;
    }

    public String getUsernamefieldname() {
        return usernamefieldname;
    }

    public void setCodeurl(String codeurl) {
        this.codeurl = codeurl;
    }

    public void setHascode(Boolean hascode) {
        this.hascode = hascode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public void setTarurl(String tarurl) {
        this.tarurl = tarurl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCodefieldname(String codefieldname) {
        this.codefieldname = codefieldname;
    }

    public void setPasswordfieldname(String passwordfieldname) {
        this.passwordfieldname = passwordfieldname;
    }

    public void setUsernamefieldname(String usernamefieldname) {
        this.usernamefieldname = usernamefieldname;
    }

}
