package com.xx.platform.domain.model.crawl;

import javax.persistence.*;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * <p>Title: URL领取规则</p>
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
@Table(name = "drawrule")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class DrawRule extends DomainLogic {
    private String id ;
    private String name ;
    private String value ;
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

    public String getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
