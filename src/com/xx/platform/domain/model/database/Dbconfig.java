package com.xx.platform.domain.model.database;

import java.sql.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.xx.platform.domain.service.*;

import org.hibernate.annotations.*;

@Entity
@Table(name = "dbconfig")
@org.hibernate.annotations.Proxy(lazy = false)
public class Dbconfig extends DomainLogic implements java.io.Serializable {
    private String id ; //id
    private String name = ""; //���ݿ�����
    private String code = ""; //���ݿ�������
    private String dbtype = ""; //���ݿ� ��������ַ
    private String driverclazz = ""; //���ݿ� ���
    private String dburl = ""; //���ݿ�������˿�
    private String dbuser = ""; //���ݿ��½�û���
    private String dbpass = ""; //���ݿ��û�����
    private String datasourcename="";//odbc����Դ����
    private String connecttype;//��������
    private String localip;//����IP
    public String getLocalip() {
		return localip;
	}

	public void setLocalip(String localip) {
		this.localip = localip;
	}

	@Transient
    private Connection connection ;

    @Transient
    private String server;
    @Transient
    private String port;
    @Transient
    private String dbname;
    
    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDbpass() {
        return dbpass;
    }

    public String getDburl() {
        return dburl;
    }

    public String getDbuser() {
        return dbuser;
    }



    public String getName() {
        return name;
    }

    public String getDriverclazz() {
        return driverclazz;
    }

    public String getDbtype() {
        return dbtype;
    }
    @Transient
    public Connection getConnection() {
        return connection;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setDbuser(String dbuser) {
        this.dbuser = dbuser;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    public void setDbpass(String dbpass) {
        this.dbpass = dbpass;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDriverclazz(String driverclazz) {
        this.driverclazz = driverclazz;
    }

    public void setDbtype(String dbtype) {
        this.dbtype = dbtype;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    @Transient
	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	@Transient
	public String getPort() {
		return port;
	}
	 
	public void setPort(String port) {
		this.port = port;
	}
	@Transient
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getDatasourcename() {
		return datasourcename;
	}

	public void setDatasourcename(String datasourcename) {
		this.datasourcename = datasourcename;
	}

	public String getConnecttype() {
		return connecttype;
	}

	public void setConnecttype(String connecttype) {
		this.connecttype = connecttype;
	}
    
}
