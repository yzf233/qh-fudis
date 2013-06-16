package com.xx.platform.domain.model.crawl;

import javax.persistence.*;

import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.service.*;

import org.hibernate.annotations.GenericGenerator;

/**
 * <p>Title:数据采集规则 </p>
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
@Table(name = "crawlerrule")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class CrawlerRule extends DomainLogic implements CrawlRule {
    private String id;
    private String name;
    private String code;
    private String value;
    private int dataType;
    private boolean isStorge;
    private String description;
    private boolean storge;
    public String getCode() {
        return code;
    }

    public int getDataType() {
        return dataType;
    }

    public String getDescription() {
        return description;
    }
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")

    public String getId() {
        return id;
    }

    public boolean isIsStorge() {
        return isStorge;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsStorge(boolean isStorge) {
        this.isStorge = isStorge;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setStorge(boolean storge) {
        this.storge = storge;
    }

    public boolean isStorge() {
        return false;
    }

}
