package com.xx.platform.core.nutch;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.nutch.db.*;
import org.apache.nutch.io.*;
import org.hibernate.annotations.*;

import com.xx.platform.core.*;
import com.xx.platform.domain.service.*;
import com.xx.platform.plugin.url.*;
import com.xx.platform.util.constants.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
@Entity
@Table(name = "webdb")
//@SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_ID")
@org.hibernate.annotations.Proxy(lazy = false)
//@OneToMany(targetEntity = Attachment.class)
//@Cascade(value = {org.hibernate.annotations.CascadeType.DELETE_ORPHAN,
//org.hibernate.annotations.CascadeType.ALL})
//@JoinColumn(name = "info_id")
//@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    public class WebDB
    extends DomainLogic implements InnerURL, Writable, java.io.Serializable {
  public WebDB() {}

  public WebDB(Page page) throws MalformedURLException {
    this.md5CHash = page.getMD5();
    this.md5hash = page.getMD5().toString();
    this.url = page.getURL().toString();
    //this.version = this.md5hash ;
    this.retriessincefetch = new Integer( (int) page.getRetriesSinceFetch());
    this.outlinks = page.getNumOutlinks();
    this.domain = (url == null || url.trim().length() == 0) ? "" :
        new URL(url).getHost(); // hash url, by default
    this.score = page.getScore();
    this.nextscore = page.getNextScore();
    this.nextfetch = page.getNextFetchTime();
    this.cratetime = new Date().getTime();
    this.isparseoutlink = page.getIsParseOutLink();
    this.intervals = 0;
    this.fromdb = page.getFromdb();
    this.extra = page.getExtra();
    this.isFilter = page.isIsFilter();
  }

  @Transient
  public void write(DataOutput out) throws IOException {
    WritableUtils.writeCompressedString(out, domain);
    WritableUtils.writeCompressedString(out, "");
    WritableUtils.writeCompressedString(out, url);
    WritableUtils.writeCompressedString(out, extra);
    md5CHash.write(out);
    out.writeLong(nextfetch);
    out.writeLong(cratetime);
    out.writeInt(retriessincefetch);
    out.writeInt(intervals);
    out.writeInt(outlinks);
    out.writeFloat(score);
    out.writeFloat(nextscore);
    out.writeInt(isparseoutlink);
    out.writeBoolean(isDelete);
    out.writeLong(index);
    out.writeLong(key);
    out.writeInt(errornum);
    out.writeInt(fromdb);
  }

  @Transient
  public void readFields(DataInput in) throws IOException {
    domain = WritableUtils.readCompressedString(in);
    version = WritableUtils.readCompressedString(in);
    url = WritableUtils.readCompressedString(in);
    extra = WritableUtils.readCompressedString(in);
    md5CHash = md5CHash.read(in);
    nextfetch = in.readLong();
    cratetime = in.readLong();
    retriessincefetch = in.readInt();
    intervals = in.readInt();
    outlinks = in.readInt();
    score = in.readFloat();
    nextscore = in.readFloat();
    isparseoutlink = in.readInt();
    isDelete = in.readBoolean();
    index = in.readLong();
    key = in.readLong();
    errornum = in.readInt();
    fromdb = in.readInt();
  }

  private String id;
  private String domain;
  private String version = "";
  private String url;
  private Long nextfetch;
  @Transient
  private Long cratetime;
  private Integer retriessincefetch;
  private Integer intervals;
  private Integer outlinks;
  private Float score = 1.0f;
  private Float nextscore = 1.0f;
  private Integer isparseoutlink = 1;
  private String extra = "";
  @Transient
  private long index = 0;
  @Transient
  private long key = 0;
  @Transient
  private String md5hash = "";
  @Transient
  private MD5Hash md5CHash;
  @Transient
  private Boolean isDelete = false;
  @Transient
  private boolean isFilter = false;
  private int errornum = 0;
  @Transient
  private int fromdb = 1; //qh 2007-07-27 标志是否来自webDB表,默认为是

  @Id
  //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
  @Column(length = 32)
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")

  public String getId() {
    //System.out.println("1:"+this.id);
    return this.id != null && !this.id.equals("") ? this.id : this.getMd5CHash() != null ?
        this.getMd5CHash().toString() : null;
  }

  public Integer getIntervals() {
    return intervals == null ? SearchContext.getXdtechsite().getUpperiod() :
        intervals;
  }

  public Long getNextfetch() {
    return nextfetch;
  }

  public Float getNextscore() {
    return nextscore;
  }

  public Integer getOutlinks() {
    return outlinks;
  }

  public Integer getRetriessincefetch() {
    return retriessincefetch;
  }

  public Float getScore() {
    return score;
  }

  public String getVersion() {
    return version;
  }

  public String getUrl() {
    return url;
  }

  public String getDomain() {
    return domain;
  }

  public Integer getIsparseoutlink() {
    return isparseoutlink;
  }

  @Transient
  public Long getCratetime() {
    return cratetime;
  }

  @Transient
  public Boolean getIsDelete() {
    return isDelete;
  }

  @Transient
  public String getMd5hash() {
    return md5hash;
  }

  @Transient
  public MD5Hash getMd5CHash() {
    return md5CHash;
  }

  @Transient
  public long getIndex() {
    return index;
  }

  public String getExtra() {
    return extra;
  }

  @Column(name = "errnum")
  public int getErrornum() {
    return errornum;
  }

  @Transient
  public long getKey() {
    return key;
  }

  @Transient
  public int getFromdb() {
    return fromdb;
  }

  @Transient
  public boolean isIsFilter() {
    return isFilter;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public void setRetriessincefetch(Integer retriessincefetch) {
    this.retriessincefetch = retriessincefetch;
  }

  public void setOutlinks(Integer outlinks) {
    this.outlinks = outlinks;
  }

  public void setNextscore(Float nextscore) {
    this.nextscore = nextscore;
  }

  public void setNextfetch(Long nextfetch) {
    this.nextfetch = nextfetch;
  }

  public void setIntervals(Integer intervals) {
    this.intervals = intervals;
  }

  public void setId(String id) {
    if (this.md5CHash != null)
      this.md5CHash.setDigest(id);
    else
      this.md5CHash = new MD5Hash(id);
    this.id = id;
  }

  public void setUrl(String url) throws MalformedURLException {
    this.domain = (url == null || url.trim().length() == 0) ? "" :
        new URL(url).getHost(); // hash url, by default
    this.url = url;
    this.md5CHash = MD5Hash.digest(url);
    //this.version = MD5.encoding(this.url);
  }

  public void setVersion(String version) {
//        StackTraceElement[] t = Thread.currentThread().getStackTrace();
//        for(StackTraceElement tt:t){System.out.println(tt.getClassName()+":"+tt.getLineNumber());}
//        System.out.print("setVersion:"+version);
    this.version = version;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public void setIsparseoutlink(Integer isparseoutlink) {
    if (isparseoutlink == null) {
      this.isparseoutlink = 1;
    }
    else {
      this.isparseoutlink = isparseoutlink.intValue();
    }
  }

  public void setCratetime(Long cratetime) {
    this.cratetime = cratetime;
  }

  public void setIsDelete(Boolean isDelete) {
    this.isDelete = isDelete;
  }

  public void setMd5hash(String md5hash) {
    this.md5hash = md5hash;
  }

  public void setMd5CHash(MD5Hash md5CHash) {
    this.md5CHash = md5CHash;
  }

  public void setIndex(long index) {
    this.index = index;
  }

  public void setExtra(String extra) {
    this.extra = extra;
  }

  public void setErrornum(int errornum) {
    this.errornum = errornum;
  }

  public void setKey(long key) {
    this.key = key;
  }

  public void setFromdb(int fromdb) {
    this.fromdb = fromdb;
  }

  public void setIsFilter(boolean isFilter) {
    this.isFilter = isFilter;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("Domain: " + this.getDomain() + "\n");
    buf.append("Version: " + this.getVersion() + "\n");
    buf.append("URL: " + this.getUrl() + "\n");
    buf.append("Extra: " + this.getExtra() + "\n");
    buf.append("ID: " + this.getId() + "\n");
    buf.append("Next fetch: " + this.getNextfetch() + "\n");
    buf.append("Retries since fetch: " + this.getRetriessincefetch() + "\n");
    buf.append("Retry interval: " + this.getIntervals() + "\n");
    buf.append("Num outlinks: " + this.getOutlinks() + "\n");
    buf.append("Score: " + this.getScore() + "\n");
    buf.append("NextScore: " + this.getNextfetch() + "\n");
    buf.append("FromDB:" + this.getFromdb() + "\n");
    buf.append("key:" + this.getKey() + "\n");
    buf.append("Errornum():" + this.getErrornum() + "\n");
    buf.append("Index():" + this.getIndex() + "\n");
    return buf.toString();

  }

  public void putScore(int lev) {
    this.score = IbeaProperty.SOURCE_DEFUALT + 3.6f / (lev > 0 ? lev : 1);
  }
}
