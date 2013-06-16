package com.xx.platform.domain.model.system;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.nutch.io.WritableUtils;
import org.hibernate.annotations.GenericGenerator;

import com.xx.platform.domain.service.DomainLogic;
import com.xx.platform.util.tools.ArraysObjectTool;

@Entity
@Table(name = "sqllog")
@org.hibernate.annotations.Proxy(lazy = false)
public class Sqllog  extends DomainLogic implements java.lang.Cloneable,org.apache.nutch.io.Writable,java.io.Serializable{
    private String id ;
    private String message ;
    private Date dtime ;
    public Sqllog(){};

    public Date getDtime() {
		return dtime;
	}

	public void setDtime(Date dtime) {
		this.dtime = dtime;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public void readFields(DataInput in) throws IOException {
		 id = WritableUtils.readCompressedString(in) ;
		 message = WritableUtils.readCompressedString(in) ;
		 byte[] data= WritableUtils.readCompressedByteArray(in);
		 dtime=(Date)ArraysObjectTool.ArrayToObject(data);
	}

	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out , id) ;
	    WritableUtils.writeCompressedString(out , message) ;
	    WritableUtils.writeCompressedByteArray(out, ArraysObjectTool.ObjectToArrays(dtime));
	}

}
