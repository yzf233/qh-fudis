package com.xx.platform.domain.model.crawl;

import javax.persistence.*;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * <p>Title: </p>
 *
 * <p>Description: 非结构化信息抽取</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
@Entity
@Table(name = "ndestruct")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class NdeStruct extends DomainLogic {
    private String id ;
    private String name ;//组合名称
    private String code;// 组合代码
    private String domain;//所属域
    private String domainrule;//域规则

    public String getCode() {
        return code;
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

    public String getDomain() {
        return domain;
    }

    public String getDomainrule() {
        return domainrule;
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setDomainrule(String domainrule) {
        this.domainrule = domainrule;
    }

}
