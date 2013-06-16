package com.xx.platform.domain.model.database;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.xx.platform.domain.service.DomainLogic;

import org.apache.nutch.io.Writable;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import javax.persistence.Transient;
import org.apache.nutch.io.WritableUtils;
import javax.persistence.GeneratedValue;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.Id;
import javax.persistence.Column;

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
@Table(name = "filedirectory")
@org.hibernate.annotations.Proxy(lazy = false)
public class FileDirectory extends DomainLogic implements java.lang.Cloneable,Writable, java.io.Serializable{
    private String id ;
    private String path ;
    private String state ;
    private String islayers;
    private String fileTypes;
    private String dirtype;
    private String uName;
    private String uPwd;
    private String remoteIPaddress;
    private String remoteFiledir;
    private String localip;


	@Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getState() {
        return state;
    }
    public String getIslayers() {
		return islayers;
	}
    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setState(String state) {
        this.state = state;
    }

	public void setIslayers(String islayers) {
		this.islayers = islayers;
	}
    @Transient
    public void write(DataOutput out) throws IOException {
        WritableUtils.writeCompressedString(out, id);
        WritableUtils.writeCompressedString(out, path);
        WritableUtils.writeCompressedString(out, state);

    }
    @Transient
    public void readFields(DataInput in) throws IOException {
        id = WritableUtils.readCompressedString(in);
        path = WritableUtils.readCompressedString(in);
        state = WritableUtils.readCompressedString(in);

    }

	public String getFileTypes() {
		return fileTypes;
	}

	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}

	public String getDirtype() {
		return dirtype;
	}

	public void setDirtype(String dirtype) {
		this.dirtype = dirtype;
	}

	public String getUName() {
		return uName;
	}

	public void setUName(String name) {
		uName = name;
	}

	public String getUPwd() {
		return uPwd;
	}

	public void setUPwd(String pwd) {
		uPwd = pwd;
	}

	public String getRemoteIPaddress() {
		return remoteIPaddress;
	}

	public void setRemoteIPaddress(String remoteIPaddress) {
		this.remoteIPaddress = remoteIPaddress;
	}

	public String getRemoteFiledir() {
		return remoteFiledir;
	}

	public void setRemoteFiledir(String remoteFiledir) {
		this.remoteFiledir = remoteFiledir;
	}

	public String getLocalip() {
		return localip;
	}

	public void setLocalip(String localip) {
		this.localip = localip;
	}


}
