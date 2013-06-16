package com.xx.platform.domain.model.search;

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
@Table(name = "guide")
@org.hibernate.annotations.Proxy(lazy = false)
public class Guide implements org.apache.nutch.io.Writable{
	private String id;
	private String pid;
	private String name;
	private String code;
	private String query;
	private String parentid;
	private String uripath;
	public String getUripath() {
		return uripath;
	}
	public void setUripath(String uripath) {
		this.uripath = uripath;
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
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	@Column(name = "vname")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "vcode")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public void readFields(DataInput in) throws IOException {
		 	id = WritableUtils.readCompressedString(in);
		 	pid = WritableUtils.readCompressedString(in);
		 	name = WritableUtils.readCompressedString(in);
		 	code=  WritableUtils.readCompressedString(in);
		 	query=  WritableUtils.readCompressedString(in);
		 	parentid=  WritableUtils.readCompressedString(in);
	}
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
	    WritableUtils.writeCompressedString(out, pid);
	    WritableUtils.writeCompressedString(out, name);
	    WritableUtils.writeCompressedString(out, code);
	    WritableUtils.writeCompressedString(out, query);
	    WritableUtils.writeCompressedString(out, parentid);
	}
}
