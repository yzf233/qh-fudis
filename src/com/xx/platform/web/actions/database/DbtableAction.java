package com.xx.platform.web.actions.database;

import java.sql.*;
import java.util.*;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.*;
import com.xx.platform.domain.model.crawl.Crawler;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.util.tools.DataMapping;
import com.xx.platform.web.actions.BaseAction;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

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
public class DbtableAction extends BaseAction {
	public static String[] TYPE = { "TABLE", "VIEW" };
	public static String[] EXCELTYPE = { "TABLE", "SYSTEM TABLE" };
	private List<Dbtable> dbtableList;
	private List<Dbconfig> dbList;
	private boolean autoSaveIndex = false;
	Dbconfig dbconfig;
	private Dbtable dbtable;
	private Tableproperty tableproperty;
	private List tablepropertyList = new ArrayList();
	private List<Tableproperty> tbList = new ArrayList();
	private List<IndexFieldImpl> indexasoList;// 联合采集索引
	private List<IndexFieldImpl> indexList;
	private int page = 1;
	private int num = 0;
	private String message;
	private String str;
	private WebDbAdminTool adminTool;

	public String list() throws Exception {
		dbtableList = service.findAllByIObjectCType(Dbtable.class);
		num = dbtableList != null ? dbtableList.size() : 0;
		dbList = service.findAllByIObjectCType(Dbconfig.class);
		return Action.SUCCESS;
	}

	public String signlist() throws Exception {
		dbtableList = service.findAllByIObjectCType(Dbtable.class);
		return Action.SUCCESS;
	}

	public String getTable() throws Exception {
		return Action.SUCCESS;
	}

	public String refreshFileCrawl() throws Exception {
		try {
			if (NutchCommand.CRAWL_COMMAND_FILECRAWLER) {
				message = "文件采集正在进行，不能重建索引！";
				return Action.SUCCESS;
			} else {
				adminTool = new WebDbAdminTool();
				adminTool.rmAllWebDB();
				List<Dbtable> dbtables = service
						.findAllByIObjectCType(Dbtable.class);
				if (dbtables != null && dbtables.size() > 0) {
					for (Dbtable dbtable : dbtables) {
						dbtable.setIdnum(null);
						dbtable.setLasttime(null);
						dbtable.setPagenum(0);
						service.updateIObject(dbtable);
					}
				}
				message = "索引重建成功！请关闭Tomcat并手动删除索引目录下的index文件夹，以避免数据信息被重复索引！ ";
				return Action.SUCCESS;
			}
		} catch (Exception e) {
			message = "索引重建失败，请检查索引文件是否损坏！";
			return Action.SUCCESS;
		}
	}

	public String refreshTableAll() throws Exception {
		String tid = request.getParameter("tid");
		String[] s = tid.split(",");
		for (int i = 0; i < s.length; i++) {
			if (s[i].equals(""))
				continue;
			dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, s[i]);
			dbtable.setIdnum(null);
			dbtable.setLasttime(null);
			dbtable.setPagenum(0);
			service.updateIObject(dbtable);
		}
		SearchContext.reloadRules();
		message = "初始化时间戳成功！";
		return Action.SUCCESS;
	}

	public String addDbtable() throws Exception {
		if (dbtable != null && dbtable.getDbid() != null
				&& dbtable.getDbid().getId() != null) {
			if (dbtable.getCode() == null || "".equals(dbtable.getCode())) {
				return Action.SUCCESS;
			}
			if (dbtable.getCode() == null || dbtable.getCode().equals(""))
				dbtable.setCode(dbtable.getName());
			Set<Tableproperty> propertySet = getDatabasetableproperty(dbtable
					.getDbid().getId(), dbtable.getCode());
			if (propertySet == null || propertySet.size() == 0)
				propertySet = getDatabasetablepropertyByRs(dbtable.getDbid()
						.getId(), dbtable.getCode());
			service.saveIObject(dbtable);

			dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					request.getParameter("dbconfig.id"));

			// boolean hasuispk=false;
			for (Tableproperty property : propertySet) {
				if (autoSaveIndex) {
					if (SearchContext.getIndexFieldSet().size() == 0) {
						IndexFieldImpl index = new IndexFieldImpl();
						index.setName(property.getName());
						index.setCode(property.getCode());
						index.setIsstorge(false);
						index.setTokentype(1);
						service.saveIObject(index);
						property.setIndexfield(index.getId());
					}
				}
				property.setDbtableid(dbtable.getId());
				property.setLength("0");
				// if("mssqlserver".equals(dbconfig.getDbtype())&&
				// "id".equals(property.getCode())){
				// property.setIsorderby("1");
				// }else{
				property.setIsorderby("0");
				// }
				/**
				 * if(hasuispk==false&&(property.getDatatype().equals("Float")||
				 * property
				 * .getDatatype().equals("Double")||property.getDatatype(
				 * ).equals
				 * ("Integer")||property.getDatatype().equals("java.util.Date"
				 * ))) { property.setUispk(true); hasuispk=true; } else
				 * if(hasuispk
				 * ==false&&property.getIspk()!=null&&property.getIspk
				 * ().equals(true
				 * )&&(property.getDatatype().equals("Float")||property
				 * .getDatatype
				 * ().equals("Double")||property.getDatatype().equals
				 * ("Integer")||
				 * property.getDatatype().equals("java.util.Date"))) {
				 * property.setUispk(true); hasuispk=true; } else
				 * property.setUispk(false);
				 **/
				// System.out.println("tableproperty.datatype: "+property.getDatatype());
				service.saveIObject(property);
			}
			SearchContext.reloadRules();
		}
		dbtableList = service.findAllByCriteria(DetachedCriteria.forClass(
				Dbtable.class)
				.add(
						Restrictions.eq("dbid.id", request
								.getParameter("dbconfig.id"))));

		return Action.SUCCESS;
	}

	public String resetDbtable() throws Exception {
		if (request.getParameter("id") != null) {
			dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, request
					.getParameter("id"));
			dbtable.setPagenum(0);
			service.updateIObject(dbtable);
			for (Dbtable dbTable : SearchContext.getDbtableList()) {
				if (dbTable.getId().equals(dbtable.getId()))
					dbTable.setPagenum(0);
			}
		}
		return Action.SUCCESS;
	}

	public String delDbtable() throws Exception {
		if (request.getParameter("id") != null) {

			service.execByHQL("delete from Dbtable where id='"
					+ request.getParameter("id") + "'");
			service.execByHQL("delete from Tableproperty where dbtableid='"
					+ request.getParameter("id") + "'");
			SearchContext.reloadRules();
		}
		dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class, request
				.getParameter("dbconfig.id"));
		return Action.SUCCESS;
	}

	public String editTableproperty() throws Exception {

		if (dbtable != null && dbtable.getId() != null)
			dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, dbtable
					.getId());
		indexList = service.findAllByIObjectCType(IndexFieldImpl.class);
		List<IndexFieldImpl> ls = service
				.findAllByIObjectCType(IndexFieldImpl.class);
		indexasoList = new ArrayList();
		for (IndexFieldImpl s : ls) {
			if (s.isIsfilecontent())
				indexasoList.add(s);
		}
		Set propertySet = getDatabasetablepropertys(dbtable.getDbid().getId(),
				dbtable.getCode());

		if (propertySet == null || propertySet.size() == 0)
			propertySet = getDatabasetablepropertyBydbRs(dbtable.getDbid()
					.getId(), dbtable.getCode());

		if (propertySet == null) {
			dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					request.getParameter("dbconfig.id"));
			str = DbtableAction.getDatabasetable(dbconfig);
			message = "连接表失败，可能数据库连接已经被修改。";
			return Action.INPUT;
		}
		tablepropertyList
				.addAll(getTableproperty(propertySet, dbtable.getId()));// 取得所有没有添加的字段的字段名

		for (int j = 0; j < tablepropertyList.size(); j++) {
			// Dbconfig d = (Dbconfig)
			// service.getIObjectByPK(Dbconfig.class,request.getParameter("dbconfig.id"));
			Tableproperty t = new Tableproperty();
			t.setCode((String) tablepropertyList.get(j));
			int cltype = getColumnType(dbtable.getId(), dbtable.getCode(),
					(String) tablepropertyList.get(j));
			t.setDatatype(DataMapping.convertDataType(cltype, dbtable.getDbid()
					.getDbtype()));
			tbList.add(t);// 把没有添加的字段塞进list
		}

		setTableproperty(null);
		return Action.SUCCESS;
	}

	private int getColumnType(String dbid, String tableName, String cloName)
			throws Exception {
		int typenum = 0;
		Statement statement = null;
		ResultSet rs = null;
		dbtable = (Dbtable) service.getIObjectByPK(Dbtable.class, dbid);
		if (dbtable.getDbid().getConnection() == null
				|| dbtable.getDbid().getConnection().isClosed()) {
			try {
				Class.forName(dbtable.getDbid().getDriverclazz());
				dbtable.getDbid().setConnection(
						DriverManager.getConnection(dbtable.getDbid()
								.getDburl(), dbtable.getDbid().getDbuser(),
								dbtable.getDbid().getDbpass()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		statement = dbtable.getDbid().getConnection().createStatement();

		String dbtype = dbtable.getDbid().getDbtype();
		if (dbtype.equals("mssqlserver"))
			rs = statement.executeQuery("select top 1 * from ["
					+ dbtable.getCode() + "]");
		else if (dbtype.equals("oracle"))
			rs = statement.executeQuery("select * from " + dbtable.getCode()
					+ " WHERE  rownum<=1");
		else if (dbtype.equals("mysql"))
			rs = statement.executeQuery("select * from `" + dbtable.getCode()
					+ "` LIMIT 0,1");

		java.sql.ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			if ((meta.getColumnName(i)).equalsIgnoreCase(cloName)) {
				typenum = meta.getColumnType(i);
				return typenum;
			}
		}
		rs.close();
		return typenum;
	}

	/*
	 * public String setTableMainOrder() throws Exception { if (dbtable != null
	 * && dbtable.getId() != null && tableproperty != null &&
	 * tableproperty.getId() != null) { dbtable = (Dbtable)
	 * service.getIObjectByPK(Dbtable.class,dbtable.getId()); indexList =
	 * service.findAllByIObjectCType(IndexFieldImpl.class); Set propertySet =
	 * getDatabasetablepropertys(dbtable.getDbid(). getId(), dbtable.getCode());
	 * tablepropertyList.addAll(getTableproperty(propertySet,dbtable.getId()));
	 * 
	 * Iterator<Tableproperty> it = dbtable.getTableproperty().iterator();
	 * Tableproperty property = null; while (it.hasNext()) { property =
	 * it.next(); if(property.getId().equals(tableproperty.getId()))
	 * property.setUorderby(1); else property.setUorderby(0);
	 * service.updateIObject(property); SearchContext.reloadRules(); }
	 * setTableproperty(null); } return Action.SUCCESS; }
	 * 
	 * public String setTableOrder() throws Exception { if (dbtable != null &&
	 * dbtable.getId() != null && tableproperty != null && tableproperty.getId()
	 * != null) { dbtable = (Dbtable)
	 * service.getIObjectByPK(Dbtable.class,dbtable.getId()); indexList =
	 * service.findAllByIObjectCType(IndexFieldImpl.class); Set propertySet =
	 * getDatabasetablepropertys(dbtable.getDbid(). getId(), dbtable.getCode());
	 * tablepropertyList.addAll(getTableproperty(propertySet,dbtable.getId()));
	 * 
	 * Iterator<Tableproperty> it = dbtable.getTableproperty().iterator();
	 * Tableproperty property = null; while (it.hasNext()) { property =
	 * it.next(); if(property.getId().equals(tableproperty.getId()))
	 * property.setUorderby(2); else property.setUorderby(0);
	 * service.updateIObject(property); SearchContext.reloadRules(); }
	 * setTableproperty(null); } return Action.SUCCESS; }
	 */

	private Set getTableproperty(Set<Tableproperty> propertySet,
			String dbtableid) {

		List<Tableproperty> tablepropertyLists = service
				.findAllByCriteria(DetachedCriteria.forClass(
						Tableproperty.class).add(
						Restrictions.eq("dbtableid", dbtableid)));
		for (int i = 0; tablepropertyLists != null
				&& tablepropertyLists.size() > i; i++) {
			if (tablepropertyLists.get(i).isDbfield() == true)
				propertySet.remove(tablepropertyLists.get(i).getCode());
		}
		return propertySet;
	}

	public String setTablePropertyDefaultValue() throws Exception {
		if (tableproperty != null && tableproperty.getId() != null) {
			Tableproperty tablepp = (Tableproperty) service.getIObjectByPK(
					Tableproperty.class, tableproperty.getId());
			tablepp.setDefaultvalue(tableproperty.getDefaultvalue());
			service.updateIObject(tablepp);
			SearchContext.reloadRules();
		}
		return Action.SUCCESS;
	}

	public String delTableProperty() throws Exception {
		if (tableproperty != null && tableproperty.getId() != null)
			service.deleteIObject(tableproperty);
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	public Dbtable getDbtable() {
		return dbtable;
	}

	public List getDbtableList() {
		return dbtableList;
	}

	public int getPage() {
		return page;
	}

	public List getDbList() {
		return dbList;
	}

	public int getNum() {
		return num;
	}

	public Tableproperty getTableproperty() {
		return tableproperty;
	}

	public void setDbtable(Dbtable dbtable) {
		this.dbtable = dbtable;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setDbtableList(List dbtableList) {
		this.dbtableList = dbtableList;
	}

	public void setDbList(List dbList) {
		this.dbList = dbList;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setTableproperty(Tableproperty tableproperty) {
		this.tableproperty = tableproperty;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setIndexList(List indexList) {
		this.indexList = indexList;
	}

	public void setDbconfig(Dbconfig dbconfig) {
		this.dbconfig = dbconfig;
	}

	public String getMessage() {
		return message;
	}

	public List getIndexList() {
		return indexList;
	}

	public Dbconfig getDbconfig() {
		return dbconfig;
	}

	public static final String getDatabasetable(Dbconfig databaseconinfo)
			throws Exception {
		StringBuffer strb = new StringBuffer();
		strb
				.append("<select name='dbtable.code' style='width:150px;'  class='required' >");
		if (databaseconinfo != null && databaseconinfo.getId() != null
				&& databaseconinfo.getId().trim().length() > 1) {
			ResultSet rs = null;
			Connection conn = null;
			List arrayList = null;

			try {
				Class.forName(databaseconinfo.getDriverclazz());
				if (!"odbc".equals(databaseconinfo.getConnecttype())) {
					conn = DriverManager.getConnection(databaseconinfo
							.getDburl(), databaseconinfo.getDbuser(),
							databaseconinfo.getDbpass());
				} else {
					if (databaseconinfo.getDbuser() != null
							|| databaseconinfo.getDbuser().trim().length() > 0
							|| databaseconinfo.getDbpass() != null
							|| databaseconinfo.getDbpass().trim().length() > 0) {
						conn = DriverManager.getConnection(databaseconinfo
								.getDburl(), databaseconinfo.getDbuser(),
								databaseconinfo.getDbpass());
					} else {
						conn = DriverManager.getConnection(databaseconinfo
								.getDburl());
					}
				}
				DatabaseMetaData dbMetData = conn.getMetaData();
				if ("excel".equals(databaseconinfo.getDbtype())) {
					rs = dbMetData.getTables(null, null, null, EXCELTYPE);
				} else {
					rs = dbMetData.getTables(null, convertDatabaseCharsetType(
							databaseconinfo.getDbuser(), databaseconinfo
									.getDbtype()), null, TYPE);
				}
				if ("excel".equals(databaseconinfo.getDbtype())) {
					while (rs.next()) {
						String temp3 = rs.getString(3);
						String temp4 = rs.getString(4);
						if (temp4 != null
								&& (temp4.equalsIgnoreCase("TABLE") || temp4
										.equalsIgnoreCase("SYSTEM TABLE"))) {
							strb.append("<option value='").append(
									temp3.toLowerCase()).append("'>").append(
									temp3.toLowerCase()).append("</option>\n");
						}
					}
				} else {
					while (rs.next()) {
						String temp3 = rs.getString(3);
						String temp4 = rs.getString(4);
						if (temp4 != null
								&& (temp4.equalsIgnoreCase("TABLE") || temp4
										.equalsIgnoreCase("VIEW"))) {
							strb.append("<option value='").append(
									temp3.toLowerCase()).append("'>").append(
									temp3.toLowerCase()).append("</option>\n");
						}
					}
				}
			} catch (Exception ex) {
				log.info("读取数据表时异常[" + ex.getMessage() + "]");
				ex.printStackTrace();
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			}

		}
		strb.append("</select>");
		return strb.toString();

	}

	/**
	 * 获表的字段名称和代码
	 * 
	 * @param dbid
	 *            String
	 * @param tableName
	 *            String
	 * @return Set
	 * @throws Exception
	 */
	private final Set getDatabasetablepropertys(String dbid, String tableName)
			throws Exception {
		ResultSet rs = null;
		Connection conn = null;
		Set propertySet = null;

		Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
				Dbconfig.class, dbid);

		try {
			Class.forName(databaseconinfo.getDriverclazz());

			conn = DriverManager.getConnection(databaseconinfo.getDburl(),
					databaseconinfo.getDbuser(), databaseconinfo.getDbpass());

			DatabaseMetaData dbMetData = conn.getMetaData();
			{
				rs = dbMetData.getColumns(null, "", convertDbtableName(
						tableName, databaseconinfo.getDbtype()), null);
			}
			propertySet = new HashSet();
			while (rs.next()) {
				String temp4 = rs.getString(4);
				propertySet.add(temp4.toLowerCase());
			}
		} catch (Exception ex) {
			// ex.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		if (propertySet == null || propertySet.size() == 0) {
			propertySet = getDatabasetablepropertyBydbRs(dbid, tableName);
		}

		return propertySet;

	}

	private final Set getDatabasetablepropertyBydbRs(String dbid,
			String tableName) throws Exception {

		Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
				Dbconfig.class, dbid);
		ResultSet rs = null;
		Connection conn = null;
		Set propertySet = null;
		Statement statement = null;
		try {
			Class.forName(databaseconinfo.getDriverclazz());
			conn = DriverManager.getConnection(databaseconinfo.getDburl(),
					databaseconinfo.getDbuser(), databaseconinfo.getDbpass());

			statement = conn.createStatement();
			if (databaseconinfo.getDbtype().equals("mssqlserver"))
				rs = statement.executeQuery("select top 1 * from [" + tableName
						+ "]");
			else if (databaseconinfo.getDbtype().equals("oracle"))
				rs = statement.executeQuery("select * from " + tableName
						+ "  WHERE  rownum<=1");
			else if (databaseconinfo.getDbtype().equals("mysql"))
				rs = statement.executeQuery("select * from `" + tableName
						+ "` LIMIT 0,1");
			else if (databaseconinfo.getDbtype().equals("execel"))
				rs = statement.executeQuery("SELECT * FROM [" + tableName
						+ "] WHERE 1>1");
			java.sql.ResultSetMetaData meta = rs.getMetaData();
			int column = meta.getColumnCount();
			propertySet = new HashSet();
			Tableproperty property = null;
			for (int i = 1; i <= column; i++) {
				String colName = meta.getColumnName(i);
				propertySet.add(colName.toLowerCase());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null)
				statement.close();
			if (conn != null) {
				conn.close();
			}
		}
		return propertySet;

	}

	/**
	 * 获取数据表的元数据信息
	 * 
	 * @param dbid
	 *            String
	 * @param tableName
	 *            String
	 * @return Set
	 * @throws Exception
	 */
	private final Set getDatabasetableproperty(String dbid, String tableName)
			throws Exception {
		Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
				Dbconfig.class, dbid);
		ResultSet rs = null;
		Connection conn = null;
		Set propertySet = null;
		try {
			Class.forName(databaseconinfo.getDriverclazz());

			conn = DriverManager.getConnection(databaseconinfo.getDburl(),
					databaseconinfo.getDbuser(), databaseconinfo.getDbpass());

			DatabaseMetaData dbMetData = conn.getMetaData();
			{
				rs = dbMetData.getColumns(null, "", convertDbtableName(
						tableName, databaseconinfo.getDbtype()), null);
			}
			propertySet = new HashSet();
			Tableproperty property = null;
			while (rs.next()) {
				property = new Tableproperty();
				String name=rs.getString(4);
				property.setCode(name.toLowerCase());
				property.setName(name.toLowerCase());
				property.setDatatype(DataMapping.convertDataType(rs.getInt(5),
						databaseconinfo.getDbtype()));
				property.setAllownull(false);

				for (IndexField indexField : SearchContext.getIndexFieldSet()) {
					if (indexField != null
							&& indexField.getCode() != null
							&& indexField
									.getCode()
									.replace("_", "")
									.equalsIgnoreCase(
											property.getCode() != null ? property
													.getCode().replaceAll("_",
															"")
													: "")) {
						property.setIndexfield(((IndexFieldImpl) indexField)
								.getId());
						break;
					}
				}
				propertySet.add(property);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return propertySet;

	}

	/**
	 * 另外一种方式获取到数据表的元数据， 在 DB2的 jcc 方式下， 有可能通过第一种方式会出错，导致无法获取到数据表信息
	 * 
	 * @param dbid
	 *            String
	 * @param tableName
	 *            String
	 * @return Set
	 * @throws Exception
	 */
	private final Set getDatabasetablepropertyByRs(String dbid, String tableName)
			throws Exception {

		Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
				Dbconfig.class, dbid);
		ResultSet rs = null;
		Connection conn = null;
		Set propertySet = null;
		Statement statement = null;
		try {
			Class.forName(databaseconinfo.getDriverclazz());
			conn = DriverManager.getConnection(databaseconinfo.getDburl(),
					databaseconinfo.getDbuser(), databaseconinfo.getDbpass());

			statement = conn.createStatement();
			String dbtype = databaseconinfo.getDbtype();
			if (dbtype.equals("mssqlserver"))
				rs = statement.executeQuery("select top 1 * from [" + tableName
						+ "]");
			else if (dbtype.equals("oracle"))
				rs = statement.executeQuery("select * from " + tableName
						+ "  WHERE  rownum<=1");
			else if (dbtype.equals("mysql"))
				rs = statement.executeQuery("select * from `" + tableName
						+ "` LIMIT 0,1");
			java.sql.ResultSetMetaData meta = rs.getMetaData();
			int column = meta.getColumnCount();
			propertySet = new HashSet();
			Tableproperty property = null;
			for (int i = 1; i <= column; i++) {
				String colName = meta.getColumnName(i);
				property = new Tableproperty();
				property.setCode(colName.toLowerCase());
				property.setName(colName.toLowerCase());
				property.setDatatype(DataMapping.convertDataType(meta
						.getColumnType(i), databaseconinfo.getDbtype()));
				property.setAllownull(false);
				for (IndexField indexField : SearchContext.getIndexFieldSet()) {
					if (indexField != null
							&& indexField.getCode() != null
							&& indexField
									.getCode()
									.replace("_", "")
									.equalsIgnoreCase(
											property.getCode() != null ? property
													.getCode().replaceAll("_",
															"")
													: "")) {
						property.setIndexfield(((IndexFieldImpl) indexField)
								.getId());
						break;
					}
				}
				propertySet.add(property);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (statement != null)
				statement.close();
			if (conn != null) {
				conn.close();
			}
		}
		return propertySet;

	}

	public static String convertDatabaseCharsetType(String in, String type) {
		String dbUser;
		if (in != null) {
			if (type.equals("oracle")) {
				dbUser = in.toUpperCase();
			} else if (type.equals("postgresql")) {
				// dbUser = in.toLowerCase();
				dbUser = "public";
			} else if (type.equals("mysql")) {
				dbUser = null;
			} else if (type.equals("mssqlserver")) {
				dbUser = null;
			} else if (type.equals("db2")) {
				dbUser = in.toUpperCase();
			} else {
				dbUser = in;
			}
		} else {
			dbUser = "public";
		}
		return dbUser;
	}

	public static String convertDbtableName(String tableName, String type) {
		String rTableName;
		if (tableName != null) {
			if (type.equals("oracle")) {
				rTableName = tableName.toUpperCase();
			} else
				rTableName = tableName;
		} else {
			rTableName = "public";
		}
		return rTableName;
	}

	class TableProperty {
		private String TABLE_NAME;
		private String TABLE_SCHEM;
		private String REMARKS;

		public String getTABLE_NAME() {
			return TABLE_NAME;
		}

		public String getTABLE_SCHEM() {
			return TABLE_SCHEM;
		}

		public void setTABLE_NAME(String TABLE_NAME) {
			this.TABLE_NAME = TABLE_NAME;
		}

		public void setTABLE_SCHEM(String TABLE_SCHEM) {
			this.TABLE_SCHEM = TABLE_SCHEM;
		}

		public String getREMARKS() {
			return REMARKS;
		}

		public void setREMARKS(String REMARKS) {
			this.REMARKS = REMARKS;
		}

	}

	public List getTablepropertyList() {
		return tablepropertyList;
	}

	public void setTablepropertyList(List tablepropertyList) {
		this.tablepropertyList = tablepropertyList;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public List<Tableproperty> getTbList() {
		return tbList;
	}

	public void setTbList(List<Tableproperty> tbList) {
		this.tbList = tbList;
	}

	public List<IndexFieldImpl> getIndexasoList() {
		return indexasoList;
	}

	public void setIndexasoList(List<IndexFieldImpl> indexasoList) {
		this.indexasoList = indexasoList;
	}

	public boolean isAutoSaveIndex() {
		return autoSaveIndex;
	}

	public void setAutoSaveIndex(boolean autoSaveIndex) {
		this.autoSaveIndex = autoSaveIndex;
	}

}
