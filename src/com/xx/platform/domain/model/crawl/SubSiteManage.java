package com.xx.platform.domain.model.crawl;

import com.xx.platform.domain.service.*;

import javax.persistence.Table;
import javax.persistence.SequenceGenerator;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Column;

/**
 * <p>Title:网站内地址管理 </p>
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
@Table(name = "subsitemanage")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class SubSiteManage extends DomainLogic {
    private String id ;
    private String name ;
    private Integer parentid;
    private String url ;
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

    public Integer getParentid() {
        return parentid;
    }

    public String getUrl() {
        return url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
