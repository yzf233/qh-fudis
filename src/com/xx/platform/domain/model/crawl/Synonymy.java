package com.xx.platform.domain.model.crawl;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;

/**
 * <p>Title: 同义词管理 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author qh 2007-07-25
 * @version 1.0
 */

@Entity
@Table(name = "synonymy")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
public class Synonymy {
    private String id;
    private String groups;
    private String words ;
    private boolean isuse ;

    public String getGroups() {
        return groups;
    }

    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public String getWords() {
        return words;
    }

    public boolean isIsuse() {
        return isuse;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public void setIsuse(boolean isuse) {
        this.isuse = isuse;
    }

}
