package com.xx.platform.domain.model.distributed;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.nutch.io.WritableUtils;

public class SynState implements org.apache.nutch.io.Writable {
	private static final long serialVersionUID = -4269637262978932071L;
	private String ip;
	private String state;
	private boolean fileCrawl;
	private boolean crawl;

	public boolean isCrawl() {
		return crawl;
	}

	public void setCrawl(boolean crawl) {
		this.crawl = crawl;
	}

	public boolean isFileCrawl() {
		return fileCrawl;
	}

	public void setFileCrawl(boolean fileCrawl) {
		this.fileCrawl = fileCrawl;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void readFields(DataInput in) throws IOException {
		ip = WritableUtils.readCompressedString(in);
		state = WritableUtils.readCompressedString(in);
		fileCrawl = in.readBoolean();
		crawl = in.readBoolean();
	}

	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, ip);
		WritableUtils.writeCompressedString(out, state);
		out.writeBoolean(fileCrawl);
		out.writeBoolean(crawl);
	}
}
