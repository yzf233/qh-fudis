package com.xx.platform.domain.model.system;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.nutch.io.WritableUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "sproject")
@org.hibernate.annotations.Proxy(lazy = false)
public class Sproject implements org.apache.nutch.io.Writable{
	private String id;
	private String name;
	private String code;
	private Long state=0l;
	private Long verify=0l;
	private String query;
	private Long istest=0l;
	public Long getIstest() {
		return istest;
	}
	public void setIstest(Long istest) {
		this.istest = istest;
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
	@Column(name = "vcode")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getState() {
		return state;
	}
	public void setState(Long state) {
		this.state = state;
	}
	public Long getVerify() {
		return verify;
	}
	public void setVerify(Long verify) {
		this.verify = verify;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		name = WritableUtils.readCompressedString(in);
		code = WritableUtils.readCompressedString(in);
		query = WritableUtils.readCompressedString(in);
		state=in.readLong();
		verify=in.readLong();
	}
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
		WritableUtils.writeCompressedString(out, name);
		WritableUtils.writeCompressedString(out, code);
		WritableUtils.writeCompressedString(out, query);
		out.writeLong(state);
		out.writeLong(verify);
	}
}
