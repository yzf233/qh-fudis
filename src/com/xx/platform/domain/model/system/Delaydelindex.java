package com.xx.platform.domain.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.xx.platform.domain.service.DomainLogic;

@Entity
@Table(name = "delaydelindex")
@org.hibernate.annotations.Proxy(lazy = false)
public class Delaydelindex  extends DomainLogic implements java.lang.Cloneable{
    private String id ;
    private String fname ;
    private String fvalue ;
    private String fdocno;
    public String getFdocno() {
		return fdocno;
	}
	public void setFdocno(String fdocno) {
		this.fdocno = fdocno;
	}
	public Delaydelindex(){};
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getFvalue() {
		return fvalue;
	}
	public void setFvalue(String fvalue) {
		this.fvalue = fvalue;
	}
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
