package com.xx.platform.domain.model.system;

import java.io.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

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
@Table(name = "xdtechsite")
@org.hibernate.annotations.Proxy(lazy = false)
public class Xdtechsite extends DomainLogic implements
		org.apache.nutch.io.Writable {
	private String id;
	private String searchdir;
	private Integer cthreads = 500; // 线程数
	private Integer depths = 10; // 采集深度
	private Integer hostthreads = 10; // 每服务器最多线程数
	private Boolean updateindex = false; // 是否更新索引
	private Integer upperiod = 30; // 索引更新周期
	private Integer mergefactor = 50;// 索引合并因子
	private Integer minmergedocs = 50; // 索引最小合并文档数
	private Integer summary = 60; // 摘要长度
	private Integer multableindex = 0;// 多个表轮询时当前轮训位置
	private Integer maxsegmentsize = 15000;// 索引数据块最大文件尺寸 ，单位是 兆字节 ， 即MB
	private Integer dbnumber = 50000;// 一次采集数据库多少数据
	private String encryptpwd;
	private String proxy; // 检索时采用的分词处理算法，在XDQueryFilter中使用
							// 1:表示不采用词库分词处理，0：表示需要做关键词处理 ， 默认为 0
	private Integer proxyport;
	private Boolean sudis = false; // 是否启用分布式
	private Boolean urlfilterreg = false; // 是否启用URL地址过滤 ， 自动加入网站地址到过滤规则的转换
	private String dlength;// 索引合并控制数量
	private Integer viewermanager = 0;// 是否开启访问者管理，1：开启 0：禁用
	private String vieweroption = "BLACK";// 访问者权限开启后，该字段表示启用的是黑名单管理还是比名单管理
											// BLACK：黑名单管理 WHITE：白名单管理
											// null：没有启用访问者管理
	private String localip;// 本机IP
	private String issyn;//是否启用集群：    1：是  其他：否

	public String getDlength() {
		return dlength;
	}

	public void setDlength(String dlength) {
		this.dlength = dlength;
	}

	@Transient
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeCompressedString(out, id);
		WritableUtils.writeCompressedString(out, searchdir);
		WritableUtils.writeCompressedString(out, proxy);
		WritableUtils.writeCompressedString(out, encryptpwd);
		out.writeInt(cthreads);
		out.writeInt(depths);
		out.writeInt(hostthreads);
		out.writeBoolean(updateindex);
		out.writeInt(upperiod);
		out.writeInt(mergefactor);
		out.writeInt(minmergedocs);
		out.writeInt(summary);
		out.writeInt(maxsegmentsize);
		out.writeInt(proxyport);
		out.writeBoolean(sudis);
		out.writeBoolean(urlfilterreg);
		WritableUtils.writeCompressedString(out, dlength);
	}

	@Transient
	public void readFields(DataInput in) throws IOException {
		id = WritableUtils.readCompressedString(in);
		searchdir = WritableUtils.readCompressedString(in);
		proxy = WritableUtils.readCompressedString(in);
		encryptpwd = WritableUtils.readCompressedString(in);
		cthreads = in.readInt();
		depths = in.readInt();
		hostthreads = in.readInt();
		updateindex = in.readBoolean();
		upperiod = in.readInt();
		mergefactor = in.readInt();
		minmergedocs = in.readInt();
		summary = in.readInt();
		maxsegmentsize = in.readInt();
		proxyport = in.readInt();
		sudis = in.readBoolean();
		urlfilterreg = in.readBoolean();
		dlength = WritableUtils.readCompressedString(in);
	}

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}

	public String getSearchdir() {
		return searchdir;
	}

	public Integer getCthreads() {
		if (cthreads == null || cthreads.intValue() <= 0)
			cthreads = 500;
		return cthreads;
	}

	public Integer getDepths() {
		if (depths == null || depths.intValue() <= 0)
			depths = 3;
		return depths;
	}

	public Integer getHostthreads() {
		return hostthreads;
	}

	public Integer getMaxsegmentsize() {
		if (maxsegmentsize == null || maxsegmentsize <= 0)
			maxsegmentsize = 15000;
		return maxsegmentsize;
	}

	public Integer getMergefactor() {
		if (mergefactor == null || mergefactor <= 0)
			mergefactor = 50;
		return mergefactor;
	}

	public Integer getMinmergedocs() {
		if (minmergedocs == null || minmergedocs <= 0)
			minmergedocs = 50;
		return minmergedocs;
	}

	public Integer getUpperiod() {
		if (upperiod == null || upperiod <= 0)
			upperiod = 30;
		return upperiod;
	}

	public Boolean getUpdateindex() {
		if (updateindex == null)
			updateindex = false;
		return updateindex;
	}

	public Integer getSummary() {
		if (summary == null || summary.intValue() <= 0)
			summary = 60;
		return summary;
	}

	public Integer getProxyport() {
		return proxyport != null ? proxyport : 8080;
	}

	public String getProxy() {
		return proxy;
	}

	public Boolean getUrlfilterreg() {
		return urlfilterreg != null ? urlfilterreg : false;
	}

	public Boolean getSudis() {
		return sudis != null ? sudis : false;
	}

	public Integer getMultableindex() {
		return multableindex;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSearchdir(String searchdir) {
		this.searchdir = searchdir;
	}

	public void setUpperiod(Integer upperiod) {
		this.upperiod = upperiod;
	}

	public void setMinmergedocs(Integer minmergedocs) {
		this.minmergedocs = minmergedocs;
	}

	public void setMergefactor(Integer mergefactor) {
		this.mergefactor = mergefactor;
	}

	public void setMaxsegmentsize(Integer maxsegmentsize) {
		this.maxsegmentsize = maxsegmentsize;
	}

	public void setHostthreads(Integer hostthreads) {
		this.hostthreads = hostthreads;
	}

	public void setDepths(Integer depths) {
		this.depths = depths;
	}

	public void setCthreads(Integer cthreads) {
		this.cthreads = cthreads;
	}

	public void setUpdateindex(Boolean updateindex) {
		if (updateindex == null)
			updateindex = false;
		this.updateindex = updateindex;
	}

	public void setSummary(Integer summary) {
		this.summary = summary;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public void setProxyport(Integer proxyport) {
		this.proxyport = proxyport;
	}

	public void setUrlfilterreg(Boolean urlfilterreg) {
		this.urlfilterreg = urlfilterreg;
	}

	public void setSudis(Boolean sudis) {
		this.sudis = sudis;
	}

	public void setMultableindex(Integer multableindex) {
		this.multableindex = multableindex;
	}

	public Integer getDbnumber() {
		return dbnumber;
	}

	public void setDbnumber(Integer dbnumber) {
		this.dbnumber = dbnumber;
	}

	public String getEncryptpwd() {
		return encryptpwd;
	}

	public void setEncryptpwd(String encryptpwd) {
		this.encryptpwd = encryptpwd;
	}

	public Integer getViewermanager() {
		return viewermanager;
	}

	public void setViewermanager(Integer viewermanager) {
		this.viewermanager = viewermanager;
	}

	public String getVieweroption() {
		return vieweroption;
	}

	public void setVieweroption(String vieweroption) {
		this.vieweroption = vieweroption;
	}

	public String getLocalip() {
		return localip;
	}

	public void setLocalip(String localip) {
		this.localip = localip;
	}

	public String getIssyn() {
		return issyn;
	}

	public void setIssyn(String issyn) {
		this.issyn = issyn;
	}
}
