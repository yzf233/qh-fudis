package com.xx.platform.domain.model.system;

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
@Table(name = "relation")
@org.hibernate.annotations.Proxy(lazy = false)
public class Relation implements org.apache.nutch.io.Writable{
	private String id;
	private String userid;
	private String projectid;
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
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		userid = WritableUtils.readCompressedString(in);
		projectid = WritableUtils.readCompressedString(in);
	}
	public void write(DataOutput out) throws IOException {
		 WritableUtils.writeCompressedString(out, id);
		 WritableUtils.writeCompressedString(out, userid);
		 WritableUtils.writeCompressedString(out, projectid);
	}
}
