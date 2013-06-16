package com.xx.platform.domain.model.distributed;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.service.*;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
@Entity
@Table(name = "synchro")
@org.hibernate.annotations.Proxy(lazy = false)
public class Synchro extends DomainLogic implements
		org.apache.nutch.io.Writable {
	private String id;
	private String dname;
	private String ipaddress;
	private int state;
	@Transient
	private ImInterface im ;
	@Transient
	public ImInterface getIm() {
		return im;
	}

	public void setIm(ImInterface im) {
		this.im = im;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
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

	public String getDname() {
		return dname;
	}

	public void setDname(String dname) {
		this.dname = dname;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		dname = WritableUtils.readCompressedString(in);
		ipaddress = WritableUtils.readCompressedString(in);
		state = in.readInt();
	}

	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
		WritableUtils.writeCompressedString(out, dname);
		WritableUtils.writeCompressedString(out, ipaddress);
		out.writeInt(state);
	}

}
