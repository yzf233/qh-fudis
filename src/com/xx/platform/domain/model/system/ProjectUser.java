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
import javax.persistence.Table;

import org.apache.nutch.io.WritableUtils;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "projectuser")
@org.hibernate.annotations.Proxy(lazy = false)
public class ProjectUser implements org.apache.nutch.io.Writable{
	private String id;
	private String username;
	private String userpassword;
	private String mail;
	private String showname;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserpassword() {
		return userpassword;
	}
	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getShowname() {
		return showname;
	}
	public void setShowname(String showname) {
		this.showname = showname;
	}
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		username = WritableUtils.readCompressedString(in);
		userpassword = WritableUtils.readCompressedString(in);
		mail = WritableUtils.readCompressedString(in);
		showname = WritableUtils.readCompressedString(in);
	}
	public void write(DataOutput out) throws IOException {
		 WritableUtils.writeCompressedString(out, id);
		 WritableUtils.writeCompressedString(out, username);
		 WritableUtils.writeCompressedString(out, userpassword);
		 WritableUtils.writeCompressedString(out, mail);
		 WritableUtils.writeCompressedString(out, showname);
	}
	
}
