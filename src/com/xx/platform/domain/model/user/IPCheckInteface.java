package com.xx.platform.domain.model.user;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.nutch.io.WritableUtils;


public class IPCheckInteface implements org.apache.nutch.io.Writable{
	private String id;
	private String name;
	private String startip;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
