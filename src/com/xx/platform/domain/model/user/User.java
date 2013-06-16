package com.xx.platform.domain.model.user;

import javax.persistence.Table;
import javax.persistence.Entity;

import com.xx.platform.domain.service.DomainLogic;

import javax.persistence.GeneratedValue;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Id;
import javax.persistence.Column;
import java.util.Date;

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
@Table(name = "xd_user")
@org.hibernate.annotations.Proxy(lazy = false)
public class User extends DomainLogic implements java.io.Serializable {
    private String id ;
    private String usergroup ;
    private String username ;
    private String password ;
    private String nickname ;
    private String name ;
    private String mail ;
    private Date lasttime ;
    private String status ;

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")

    public String getId() {
        return id;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getStatus() {
        return status;
    }

    public String getUsergroup() {
        return usergroup;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUsergroup(String usergroup) {
        this.usergroup = usergroup;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
