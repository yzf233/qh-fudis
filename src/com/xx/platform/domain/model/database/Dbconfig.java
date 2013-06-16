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
    private String name = ""; //数据库类型
    private String code = ""; //数据库驱动名
    private String dbtype = ""; //数据库 服务器地址
    private String driverclazz = ""; //数据库 别称
    private String dburl = ""; //数据库服务器端口
    private String dbuser = ""; //数据库登陆用户名
    private String dbpass = ""; //数据库用户密码
    private String datasourcename="";//odbc数据源名称
    private String connecttype;//连接类型
    private String localip;//本地IP
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
