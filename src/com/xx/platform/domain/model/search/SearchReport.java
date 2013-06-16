package com.xx.platform.domain.model.search;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.nutch.io.WritableUtils;
import org.hibernate.annotations.GenericGenerator;

import com.xx.platform.util.tools.ArraysObjectTool;
@Entity
@Table(name = "searchreport")
@org.hibernate.annotations.Proxy(lazy = false)
public class SearchReport implements org.apache.nutch.io.Writable,Comparable{
	private String id;
	private String keyword;
	private String query;
	private Date createtime;
	private Long resultsnum;
	private Long type;
	private String username;
	private Long queryTimes=0l;
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
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Long getResultsnum() {
		return resultsnum;
	}
	public void setResultsnum(Long resultsnum) {
		this.resultsnum = resultsnum;
	}
	@Column(name = "vtype")
	public Long getType() {
		return type;
	}
	public void setType(Long type) {
		this.type = type;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Transient
	public Long getQueryTimes() {
		return queryTimes;
	}
	public void setQueryTimes(Long queryTimes) {
		this.queryTimes = queryTimes;
	}
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
	 	keyword = WritableUtils.readCompressedString(in);
	 	username = WritableUtils.readCompressedString(in);
	 	query=  WritableUtils.readCompressedString(in);
	 	byte[] bcreatetime=WritableUtils.readCompressedByteArray(in);
	 	createtime=(Date)ArraysObjectTool.ArrayToObject(bcreatetime);
	 	type=in.readLong();
	 	resultsnum=in.readLong();
	 	queryTimes=in.readLong();
	}
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
	    WritableUtils.writeCompressedString(out, keyword);
	    WritableUtils.writeCompressedString(out, username);
	    WritableUtils.writeCompressedString(out, query);
	    WritableUtils.writeCompressedByteArray(out, ArraysObjectTool.ObjectToArrays(createtime));
	    out.writeLong(type);
	    out.writeLong(resultsnum);
	    out.writeLong(queryTimes);
	}
	public int compareTo(Object o) {
		SearchReport sr=(SearchReport)o;
		if(sr.getQueryTimes()>this.queryTimes){
			return 1;
		}else{
			return 0;
		}
	}
}
