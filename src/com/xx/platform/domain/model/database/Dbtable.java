package com.xx.platform.domain.model.database;

import java.io.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.xx.platform.domain.service.*;

import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

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
@Table(name = "dbtable")
@org.hibernate.annotations.Proxy(lazy = false)
public class Dbtable extends DomainLogic implements java.lang.Cloneable,Writable, java.io.Serializable {
    private String id ;
    private String name ;
    private String code ;
    private Dbconfig dbid ;
    private String dbname ;
    private Integer pagenum = 0 ; //改成了数据条数，最好用long
    private Date lasttime ;
    private String idnum ;
    private Set<Tableproperty> tableproperty ;
    public String getCode() {
        return code;
    }
    public String getDbname() {
        return dbname;
    }

    public String getName() {
        return name;
    }
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")

    public String getId() {
        return id;
    }
    @ManyToOne
    @JoinColumn(name="dbid")
    public Dbconfig getDbid() {
        return dbid;
    }
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name ="dbtableid")
    @OrderBy("code")
    public Set<Tableproperty> getTableproperty() {
        return tableproperty;
    }

  public Integer getPagenum() {
    return pagenum!=null?pagenum:0;
  }

  public Date getLasttime() {
    return lasttime;
  }


    public void setCode(String code) {
        this.code = code;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDbid(Dbconfig dbid) {
        this.dbid = dbid;
    }

    public void setTableproperty(Set tableproperty) {
        this.tableproperty = tableproperty;
    }

  public void setPagenum(Integer pagenum) {
    this.pagenum = pagenum;
  }

  public void setLasttime(Date lasttime) {
    this.lasttime = lasttime;
  }


    public Object clone() throws CloneNotSupportedException {
        Dbtable reDbTable = new Dbtable();
        reDbTable.setDbid(this.getDbid());
        reDbTable.setCode(this.getCode());
        reDbTable.setId(this.getId());
        reDbTable.setName(this.getName());
        reDbTable.setTableproperty(new HashSet());
        for(Tableproperty property:tableproperty)
        {
            Tableproperty t= (Tableproperty)property.clone() ;
            reDbTable.getTableproperty().add(t) ;
        }
        return reDbTable;
    }

    @Transient
    public void write(DataOutput out) throws IOException {
        WritableUtils.writeCompressedString(out, id);
        WritableUtils.writeCompressedString(out, name);
        WritableUtils.writeCompressedString(out, code);
        WritableUtils.writeCompressedString(out, dbname);
        out.writeInt(pagenum);
        out.writeInt(tableproperty.size());
        for(Tableproperty property:tableproperty)
        {
            WritableUtils.writeCompressedString(out, property.getIndexfield());
        }
    }

    @Transient
    public void readFields(DataInput in) throws IOException {
        id = WritableUtils.readCompressedString(in);
        name = WritableUtils.readCompressedString(in);
        code = WritableUtils.readCompressedString(in);
        dbname = WritableUtils.readCompressedString(in);
        pagenum = in.readInt();
        int tasnum = in.readInt();
        Tableproperty t = null;
        tableproperty = new HashSet<Tableproperty>();
        for(int i=0;i<tasnum;i++){
            t = new Tableproperty();
            t.setIndexfield(WritableUtils.readCompressedString(in));
            tableproperty.add(t);

        }
    }
    public String toString(){
        System.out.println(id+":"+name+":"+code);
        return null;
    }
	public String getIdnum() {
		return idnum;
	}
	public void setIdnum(String idnum) {
		this.idnum = idnum;
	}
}
