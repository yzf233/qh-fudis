package com.xx.platform.domain.model.crawl;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.*;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.nutch.io.WritableUtils;

/**
 * <p>Title:分类管理 </p>
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
@Table(name = "category")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
        public class Category extends DomainLogic implements org.apache.nutch.io.Writable{
    private String id;
    private String name;
    private String code ;
    @Transient
    public void write(DataOutput out) throws IOException {
      WritableUtils.writeCompressedString(out , id) ;
      WritableUtils.writeCompressedString(out , name) ;
      WritableUtils.writeCompressedString(out , code) ;
    }
    @Transient
    public void readFields(DataInput in) throws IOException {
      id = WritableUtils.readCompressedString(in) ;
      name = WritableUtils.readCompressedString(in) ;
      code = WritableUtils.readCompressedString(in) ;
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

    public String getCode() {
        return code;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }



}
