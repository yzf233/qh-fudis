package com.xx.platform.core.rpc;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.nutch.io.WritableUtils;

import com.xx.platform.domain.service.DomainLogic;

public class DBList extends DomainLogic implements org.apache.nutch.io.Writable {
	private List<Map<String, String>> dbList = new ArrayList<Map<String, String>>();

	public List<Map<String, String>> getDbList() {
		return dbList;
	}
	public void setDbList(List<Map<String, String>> dbList) {
		this.dbList = dbList;
	}
	public DBList() {
	}
	public DBList(List<Map<String, String>> ls) {
		this.dbList = ls;
	}

	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedByteArray(out, convert(dbList)); 
	}

	public void readFields(DataInput in) throws IOException {
		
		byte[] data=WritableUtils.readCompressedByteArray(in);
		java.io.ByteArrayInputStream bi = new java.io.ByteArrayInputStream(data);
		java.io.ObjectInputStream oi = new java.io.ObjectInputStream(bi);
        try {
			this.dbList=(List<Map<String, String>>)oi.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally
		{
			oi.close();
			bi.close();
		}

	}

	private byte[] convert(Object dataObject) throws IOException {
		java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(
				baout);
		objOut.writeObject(dataObject);
		byte[] data = baout.toByteArray();
		objOut.close();
		baout.close();
		return data;
	}

}
