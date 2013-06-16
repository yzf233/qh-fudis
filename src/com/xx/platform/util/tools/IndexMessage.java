package com.xx.platform.util.tools;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

import org.apache.nutch.fetcher.FetcherOutput;
import org.apache.nutch.io.Writable;
import org.apache.nutch.io.WritableUtils;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.protocol.Content;

public class IndexMessage implements Writable{
	private static final long serialVersionUID = 9064426926295202440L;
	private String flag;
	private Map<String,String> map;
	public IndexMessage(){
	}
	public IndexMessage(String flag,Map<String,String> map){
		this.flag=flag;
		this.map=map;
	}
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public void readFields(DataInput in) throws IOException {
		byte[] bmap = WritableUtils.readByteArray(in);
		map = (Map<String, String>) ArraysObjectTool.ArrayToObject(bmap);
		flag=WritableUtils.readString(in);
	}
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeByteArray(out, ArraysObjectTool.ObjectToArrays(map));
		WritableUtils.writeString(out, flag);
	}
}
