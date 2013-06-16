package com.xx.platform.domain.model.crawl;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.*;

/**
 * <p>Title: 网站管理</p>
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
@Table(name = "sitemanage")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class SiteManage extends DomainLogic {
    private String id ;
    private String name ;
    private String url ;
    private int lev; //优先级 qh 2007-07-27
    private int fre; //更新频率 qh 2007-07-27
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

    public String getUrl() {
        return url;
    }

    public int getLev() {
        return lev;
    }

    public int getFre() {
        return fre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLev(int lev) {
        this.lev = lev;
    }

    public void setFre(int fre) {
        this.fre = fre;
    }
}
