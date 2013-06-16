package com.xx.platform.domain.model.user;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.nutch.io.WritableUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "forbidlist")
@org.hibernate.annotations.Proxy(lazy = false)
public class ForbidList extends IPCheckInteface implements org.apache.nutch.io.Writable{
	private String id ;
	private String startip;
	private String name;
	private String endip;
	public String getStartip() {
		return startip;
	}
	public void setStartip(String startip) {
		this.startip = startip;
	}
	public String getEndip() {
		return endip;
	}
	public void setEndip(String endip) {
		this.endip = endip;
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
	@Column(name = "vname")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		startip = WritableUtils.readCompressedString(in);
	 	name = WritableUtils.readCompressedString(in);
	 	endip=  WritableUtils.readCompressedString(in);
	}
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
	    WritableUtils.writeCompressedString(out, startip);
	    WritableUtils.writeCompressedString(out, endip);
	    WritableUtils.writeCompressedString(out, name);
	}
}
