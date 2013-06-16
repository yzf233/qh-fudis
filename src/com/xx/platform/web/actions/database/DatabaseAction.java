package com.xx.platform.web.actions.database;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import sun.jdbc.odbc.JdbcOdbcDriver;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.database.DomainModel;
import com.xx.platform.domain.model.database.Modelproperty;
import com.xx.platform.domain.model.database.Tableproperty;
import com.xx.platform.util.tools.DataMapping;
import com.xx.platform.web.actions.BaseAction;

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
public class DatabaseAction extends BaseAction {
	private List databaseList;

	private List<Dbtable> dbtableList;

	private Dbconfig dbconfig;

	private int page = 1;

	private int num = 1;

	private List<Dbconfig> dbList;

	private String str;

	private String message;
	
	public String checkConnection() throws IOException, SQLException{
		String flag="0";
		dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,dbconfig.getId());
		if(dbconfig!=null){
			Connection conn=null;
			try {
	            Class.forName(dbconfig.getDriverclazz());
	            DriverManager.setLoginTimeout(5);
	            if(!"odbc".equals(dbconfig.getConnecttype())){
	            	conn=DriverManager.getConnection(dbconfig.getDburl(),dbconfig.getDbuser(),dbconfig.getDbpass());
	            }else{
	            	if(dbconfig.getDbuser()!=null||dbconfig.getDbuser().trim().length()>0||dbconfig.getDbpass()!=null||dbconfig.getDbpass().trim().length()>0){
	            		conn=DriverManager.getConnection(dbconfig.getDburl(),dbconfig.getDbuser(),dbconfig.getDbpass());
	            	}else{
	            		conn=DriverManager.getConnection(dbconfig.getDburl());
	            	}
	            }
	        } catch (Exception ex) {
	        	flag="1";
	        	message="���ݿ�����ʧ�ܣ���������ǰ�����ã�";
	        }finally{
	        	if(conn!=null){
	        		conn.close();
	        	}
	        }
		}else{
			flag="1";
		}
		PrintWriter writer=response.getWriter();
		writer.write(flag);
		writer.flush();
		writer.close();
		return null;
	}
	
	public String list() throws Exception {
		databaseList = service.findByIObjectCType(Dbconfig.class, page,
				FIV_PAGE_SIZE);
		return Action.SUCCESS;
	}

	public String dbstables() throws Exception {
		if (dbconfig != null && dbconfig.getId() != null) {
			List<Dbtable> dbtableLists = service
					.findAllByCriteria(DetachedCriteria.forClass(Dbtable.class)
							.add(Restrictions.eq("dbid.id", dbconfig.getId())));
			Map<String, Dbtable> dbtmap = new java.util.HashMap<String, Dbtable>();
			for (Dbtable dbtable : dbtableLists) {
				dbtmap.put(dbtable.getId(), dbtable);
			}
			dbtableList = new ArrayList<Dbtable>(dbtmap.values());
			num = dbtableList != null ? dbtableList.size() : 0;
			dbList = service.findAllByIObjectCType(Dbconfig.class);
			dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					dbconfig.getId());
			
			
            try {
                Class.forName(dbconfig.getDriverclazz());
                Connection conn=null;
                if(!"odbc".equals(dbconfig.getConnecttype())){
                	conn=DriverManager.getConnection(dbconfig.getDburl(),dbconfig.getDbuser(),dbconfig.getDbpass());
                }else{
                	if(dbconfig.getDbuser()!=null||dbconfig.getDbuser().trim().length()>0||dbconfig.getDbpass()!=null||dbconfig.getDbpass().trim().length()>0){
	            		conn=DriverManager.getConnection(dbconfig.getDburl(),dbconfig.getDbuser(),dbconfig.getDbpass());
	            	}else{
	            		conn=DriverManager.getConnection(dbconfig.getDburl());
	            	}
                }
                dbconfig.setConnection(conn);
            } catch (Exception ex) {
                message="���ݿ�����ʧ�ܣ���������ǰ�����ã�";
                return Action.INPUT;
            }
			str = DbtableAction.getDatabasetable(dbconfig);
		}
		return Action.SUCCESS;
	}

	public String addDatabase() throws Exception {
		if (dbconfig != null) {
			/**
			 * MySql
			 * url��ʽ��jdbc:mysql://[hostname][:port]/dbname[?param1=value1][&param2=value2]
			 * 
			 * autoReconnect �����ݿ����Ӷ�ʧʱ�Ƿ��Զ����ӣ�ȡֵtrue/false false maxReconnects
			 * ���autoReconnectΪtrue,�˲���Ϊ���Դ�����ȱʡΪ3�� 3 initialTimeout
			 * ���autoReconnectΪtrue,�˲���Ϊ��������ǰ�ȴ������� 2
			 */
			// if (dbconfig.getDbtype().equals("mysql")) {
			// String url = dbconfig.getDburl();
			// if (url.indexOf("?") < 0) {
			// url = url + "?autoReconnect=true";
			// } else {
			// url = url + "&autoReconnect=true";
			// }
			// //System.out.println(url);
			// dbconfig.setDburl(url);
			// }
			message = setUrl(dbconfig);
			if(message!=null){
				if (request.getParameter("toptest") != null)//toptest������ݹ�����֤����������ݿ����������ճ��
					return "testreturn";
				else
					return Action.INPUT;
				
			}
			if (request.getParameter("type") != null
					&& request.getParameter("type").equals("0")) { // ����
				//����URL
				message = testDb(dbconfig);
				if (request.getParameter("toptest") != null)
				return "testreturn";
				else
				return Action.INPUT;
			} else {
				try {
					dbconfig.setLocalip(SearchContext.getXdtechsite().getLocalip());
					service.saveIObject(dbconfig);
				} catch (Exception ex) {
					message = "���ݿ�������Ϣ�������������Ϣ�Ƿ�����";
				}
				// ����������ṹ
				if (request.getParameter("copytotal") != null
						&& request.getParameter("copytotal").equals("yes")) {
					copyTotal(dbconfig);
				}
			}
		}
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	/**
	 * copyTotal
	 * 
	 * @param dbconfig
	 *            Dbconfig
	 */
	private void copyTotal(Dbconfig dbconfig) throws Exception {
		if (sdbconfig == null || sdbconfig.getId() == null) {
			message = "��ǰû�б����ƵĶ������ȸ���һ�����ݿ⣡";
		} else {
			List<Dbtable> dbtableLists = service
					.findAllByCriteria(DetachedCriteria.forClass(Dbtable.class)
							.add(Restrictions.eq("dbid.id", sdbconfig.getId())));
			Map<String, Dbtable> dbtmap = new java.util.HashMap<String, Dbtable>();
			for (Dbtable dbtable : dbtableLists) {
				dbtmap.put(dbtable.getId(), dbtable);
			}
			for (Dbtable dbtable : dbtmap.values()) {
				dbtable.setDbid(dbconfig);
				dbtable.setPagenum(0);
				dbtable.setName(dbconfig.getName() + "." + dbtable.getName());
				Set<Tableproperty> propertySet = new HashSet<Tableproperty>(
						dbtable.getTableproperty());
				dbtable.setTableproperty(null);
				dbtable.setId(null);
				service.saveIObject(dbtable);
				for (Tableproperty property : propertySet) {
					property.setId(null);
					property.setDbtableid(dbtable.getId());
					service.saveIObject(property);
				}
			}
		}
	}

	/**
	 * ���� ��ȡ����
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String copy() throws Exception {

		if (request.getParameter("id") != null) {
			sdbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					request.getParameter("id"));
		} else if ((sdbconfig != null && sdbconfig.getId() != null)) {
			sdbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					sdbconfig.getId());
		}

		// System.out.println("copy()"+sdbconfig.getName());
		response.setCharacterEncoding("UTF-8");
		resolutionUrl(sdbconfig);
		if (sdbconfig == null || sdbconfig.getName() == null)
			response.getWriter().write("<font color=green>����ʧ��</font>");
		else
			response.getWriter().write(
					"<font color=green>���� " + sdbconfig.getName()
							+ " �ɹ�</font>");
		return null;
		// return Action.SUCCESS;
	}

	/**
	 * ճ�� ��ȡ����
	 * 
	 * @return String
	 * @throws Exception
	 */
	public String paste() throws Exception {
		if (sdbconfig != null && sdbconfig.getId() != null) {
			sdbconfig = sdbconfig;
			dbconfig = sdbconfig;
			dbconfig.setName("");
			resolutionUrl(dbconfig);
			// List<ParserRule>
			// parserRuleList = service.findAllByCriteria(DetachedCriteria.
			// forClass(ParserRule.class).add(Restrictions.eq("parentid",
			// gcrawler.getId())));
			// if ( (crawler != null && crawler.getId() != null)) {
			// for (ParserRule parseRule : parserRuleList) {
			// parseRule.setId(null);
			// parseRule.setParentid(crawler.getId());
			// service.saveIObject(parseRule);
			// }
			// }
		}
		// System.out.println("paste()");
		// return null;
		return Action.SUCCESS;
	}

	public String editDatabase() throws Exception {
		if (request.getParameter("id") != null
				|| (dbconfig != null && dbconfig.getId() != null)) {
			dbconfig = (Dbconfig) service.getIObjectByPK(Dbconfig.class,
					request.getParameter("id") != null ? request
							.getParameter("id") : dbconfig.getId());
			resolutionUrl(dbconfig);
		}
		return Action.SUCCESS;
	}

	public String editDatabaseDo() throws Exception {
		if (dbconfig != null && dbconfig.getId() != null) {
			message = setUrl(dbconfig);
			if(message!=null){
				return Action.INPUT;
			}
			if (request.getParameter("type") != null
					&& request.getParameter("type").equals("0")) { // ����
				message = testDb(dbconfig);
				return Action.INPUT;
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("1")) { // �޸�
				dbconfig.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.updateIObject(dbconfig);
				message = "���ݿ���Ϣ�޸ĳɹ�";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("2")) { // ɾ��
				List<Dbtable> dbtableLists = service
						.findAllByCriteria(DetachedCriteria.forClass(
								Dbtable.class).add(
								Restrictions.eq("dbid.id", dbconfig.getId())));
				service.execByHQL("delete from Dbtable where dbid='"
						+ dbconfig.getId() + "'");
				if (dbtableLists != null){
					StringBuilder sbCondition=new StringBuilder();
					for (Dbtable dbTable : dbtableLists) {
						if(sbCondition.length()==0){
							sbCondition.append("dbtableid in('").append(dbTable.getId()).append("'");
						}else{
							sbCondition.append(",'").append(dbTable.getId()).append("'");
						}
					}
					if(sbCondition.length()>0){
						sbCondition.append(")");
						StringBuilder sbHql=new StringBuilder();
						sbHql.append("delete from Tableproperty where ").append(sbCondition);
						service.execByHQL(sbHql.toString());
					}
//					for (Dbtable dbTable : dbtableLists) {
//						service
//								.execByHQL("delete from Tableproperty where dbtableid='"
//										+ dbTable.getId() + "'");
//
//					}
				}
				service.deleteIObject(dbconfig);
				message = "���ݿ���Ϣɾ���ɹ�";
			}
		} else {
			message = "�������ݿ�������Ϣ�Ƿ�����";
		}
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	private String testDb(Dbconfig dbconfig) {
		Connection conn = null;
		try {
			Class.forName(dbconfig.getDriverclazz());
			if("odbc".equals(dbconfig.getConnecttype())){
				//DriverManager.registerDriver(new JdbcOdbcDriver());
				String userName=dbconfig.getDbuser();
				String userPwd=dbconfig.getDbpass();
				if((userName==null&&userPwd==null)||(userName.trim().length()==0&&userPwd.trim().length()==0)){
					conn = DriverManager.getConnection(dbconfig.getDburl());	
				}else{
					conn = DriverManager.getConnection(dbconfig.getDburl(), 
							dbconfig.getDbuser(), dbconfig.getDbpass());
				}
			}else{
				conn = DriverManager.getConnection(dbconfig.getDburl(), dbconfig
						.getDbuser(), dbconfig.getDbpass());
			}
			message = "���Գɹ������ݿ�������Ϣ��ȷ";
		} catch (SQLException ex) {
			message = "���ݿ�������Ϣ���������û��������롢ip��<br/>�˿�";
		} catch (ClassNotFoundException ex) {
			message = "��ѡ�����ݿ�����";
		} finally {
			try {
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException ex1) {
				message = "�ر����ݿ�������Ӵ���";
			}
		}

		return message;
	}

	/**
	 * ����������datamoduleService ���� ���ύҳ����� ������װҵ���߼����ݣ�֮���ύ�� Service �� ���� ��
	 * �ڸ÷����н�������У�飬�����֤ʧ�ܣ� �򱣴������Ϣ��
	 * 
	 * @param actionMapping
	 *            ActionMapping
	 * @param form
	 *            ActionForm
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 * @return ActionForward
	 */
	public final String addDatamoduleDoAction() throws Exception {
		DomainModel bean = new DomainModel();
		String[] tablename = request.getParameterValues("tablename");
		String[] remarks = request.getParameterValues("remarks");
		service = super.getService("datamoduleService");
		for (int i = 0; tablename != null && i < tablename.length; i++) {
			bean.setTablename(tablename[i]);
			bean.setCode(tablename[i]);
			if (remarks[i] != null && !remarks[i].equalsIgnoreCase("")) {
				bean.setName(remarks[i]);
			} else {
				bean.setName(tablename[i]);
			}

			bean.setModuleok("0");
			bean.setDaook("0");
			bean.setServiceok("0");
			bean.setActionok("0");
			bean.setListviewok("0");
			bean.setFormbeanok("0");
			bean.setAddviewok("0");
			bean.setUpdateviewok("0");
			bean.setRemoveviewok("0");
			bean.setAppid("");
			bean.setMemo("");
			service.saveIObject(bean);

		}
		SearchContext.reloadRules();
		return "";

	}

	public final String getDatabasetable() throws Exception {
		StringBuffer strb = new StringBuffer();
		String dbid = request.getParameter("dbid");
		if (dbid == null || dbid.trim().length() < 1)
			strb.append("<option value=''></option>\n");
		else {
			Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
					Dbconfig.class, dbid);
			ResultSet rs = null;
			Connection conn = null;
			List arrayList = null;

			try {
				Class.forName(databaseconinfo.getDriverclazz());
				if("odbc".equals(databaseconinfo.getConnecttype())){
					DriverManager.registerDriver(new JdbcOdbcDriver());
				}
				System.out.println(databaseconinfo.getDburl()+"             "+databaseconinfo.getDbuser()+"            "+databaseconinfo.getDbpass());
				conn = DriverManager.getConnection(databaseconinfo.getDburl(),
						databaseconinfo.getDbuser(), databaseconinfo
								.getDbpass());

				DatabaseMetaData dbMetData = conn.getMetaData();
				if (databaseconinfo.getDbtype() != null
						&& !databaseconinfo.getDbtype().trim().equals("")) {

					rs = dbMetData.getTables(null, convertDatabaseCharsetType(
							databaseconinfo.getDbtype(), databaseconinfo
									.getDbtype()), null, null);
				} else {
					rs = dbMetData.getTables(null, convertDatabaseCharsetType(
							databaseconinfo.getDbuser(), databaseconinfo
									.getDbtype()), null, null);
				}

				arrayList = new ArrayList();
				TableProperty table = null;
				while (rs.next()) {
					if (rs.getString(4) != null
							&& rs.getString(4).equalsIgnoreCase("TABLE")) {
						table = new TableProperty();
						table.setTABLE_SCHEM(rs.getString(2).toLowerCase());
						table.setTABLE_NAME(rs.getString(3).toLowerCase());
						table.setREMARKS(rs.getString(5));
						arrayList.add(table);
						strb.append("<option value='").append(
								table.getTABLE_NAME()).append("'>").append(
								table.getTABLE_NAME()).append("</option>\n");
					}
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
		}
		response.getWriter().write(strb.toString());
		return null;

	}

	private final List getDatabasetableproperty(String dbid, String tableId,
			String tableName) throws Exception {

		Dbconfig databaseconinfo = (Dbconfig) service.getIObjectByPK(
				Dbconfig.class, dbid);
		ResultSet rs = null;
		ResultSet pk = null;
		ResultSet fk = null;
		Connection conn = null;
		List arrayList = null;
		try {
			Class.forName(databaseconinfo.getDriverclazz());

			conn = DriverManager.getConnection(databaseconinfo.getDburl(),
					databaseconinfo.getDbuser(), databaseconinfo.getDbpass());

			DatabaseMetaData dbMetData = conn.getMetaData();
			if (databaseconinfo.getDbtype() != null
					&& !databaseconinfo.getDbtype().trim().equals("")) {
				rs = dbMetData.getColumns(null, convertDatabaseCharsetType(
						databaseconinfo.getDbtype(), databaseconinfo
								.getDbtype()), convertDatabaseCharsetType(
						tableName, databaseconinfo.getDbtype()), null);
			} else {
				rs = dbMetData.getColumns(null, convertDatabaseCharsetType(
						databaseconinfo.getDbuser().toUpperCase(),
						databaseconinfo.getDbtype()),
						convertDatabaseCharsetType(tableName, databaseconinfo
								.getDbtype()), null);
			}
			pk = dbMetData.getPrimaryKeys(null, databaseconinfo.getDbuser()
					.toUpperCase(), tableName.toUpperCase());
			fk = dbMetData.getImportedKeys(null, databaseconinfo.getDbuser()
					.toUpperCase(), tableName.toUpperCase());

			arrayList = new ArrayList();
			Modelproperty model = null;
			int i = 0;
			String pkCol = "";
			Map<String, FKpropert> fkCol = new HashMap<String, FKpropert>();
			while (pk.next()) {
				pkCol = pk.getString(4);
				// ���������Ϣ
			}
			FKpropert fkProperty = null;
			while (fk.next()) {
				fkProperty = new FKpropert(pk.getString(8), pk.getString(3), pk
						.getString(4));
				fkCol.put(fkProperty.getFkName(), fkProperty);
			}

			while (rs.next()) {
				i++;
				model = new Modelproperty();
				if (rs.getString(4) != null && rs.getString(4).equals(pkCol)) {
					model.setIspk("true");
				}
				if ((fkProperty = fkCol.get(rs.getString(4))) != null) {
					model.setIsfk("true");
					model.setFktable(fkProperty.getPkTabel());
					model.setFkfield(fkProperty.getPkName());
				}
				model.setTableid(tableId);
				model.setCode(rs.getString(4).toLowerCase());
				model.setName(rs.getString(4).toLowerCase());
				if (rs.getString(12) != null) {
					model.setCname(rs.getString(12));
				} else {
					model.setCname(rs.getString(4).toLowerCase());
				}
				model.setSequen(new Integer(i));
				model.setLength(rs.getString(7));
				model.setType(DataMapping.convertDataType(rs.getInt(5)));

				arrayList.add(model);
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
		return arrayList;

	}

	public static String convertDatabaseCharsetType(String in, String type) {
		String dbUser;
		if (in != null) {
			if (type.equals("oracle")) {
				dbUser = in.toUpperCase();
			} else if (type.equals("postgresql")) {
				dbUser = in.toLowerCase();
			} else {
				dbUser = in;
			}
		} else {
			dbUser = "public";
		}
		return dbUser;
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
				rs = dbMetData.getColumns(null, convertDatabaseCharsetType(
						databaseconinfo.getDbuser().toUpperCase(),
						databaseconinfo.getDbtype()), DbtableAction
						.convertDbtableName(tableName, databaseconinfo
								.getDbtype()), null);
			}
			propertySet = new HashSet();
			Tableproperty property = null;
			while (rs.next()) {
				property = new Tableproperty();
				property.setCode(rs.getString(4).toLowerCase());
				property.setName(rs.getString(4).toLowerCase());
				property.setDatatype(DataMapping.convertDataType(rs.getInt(5)));
				property.setAllownull(false);
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

	class FKpropert {
		private String fkName; // �����

		private String pkTabel; // ������õı�

		private String pkName; // ������õı����

		public FKpropert(String fk, String pkT, String pkN) {
			this.fkName = fk;
			this.pkTabel = pkT;
			this.pkName = pkN;
		}

		public String getFkName() {
			return fkName;
		}

		public String getPkTabel() {
			return pkTabel;
		}

		public String getPkName() {
			return pkName;
		}
	}

	public Dbconfig getDbconfig() {
		return dbconfig;
	}

	public List getDatabaseList() {
		return databaseList;
	}

	public int getPage() {
		return page;
	}

	public List getDbtableList() {
		return dbtableList;
	}

	public int getNum() {
		return num;
	}

	public List getDbList() {
		return dbList;
	}

	public String getStr() {
		return str;
	}

	public void setDbconfig(Dbconfig dbconfig) {
		this.dbconfig = dbconfig;
	}

	public void setDatabaseList(List databaseList) {
		this.databaseList = databaseList;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setDbtableList(List dbtableList) {
		this.dbtableList = dbtableList;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setDbList(List dbList) {
		this.dbList = dbList;
	}

	public void setStr(String str) {
		this.str = str;
	}

	/**
	 * 	���ݷ�������ַ���˿ڣ����ݿ���ƴ�����ݿ�����URL
	 */
	private String setUrl(Dbconfig dbconfig) {
		try{
		String message = null;
		String dbtype = dbconfig.getDbtype();
		String server = dbconfig.getServer();
		String port = dbconfig.getPort();
		String dbname = dbconfig.getDbname();
		StringTokenizer st = new StringTokenizer(server,".");
		if(!"odbc".equals(dbconfig.getConnecttype())&&!server.equalsIgnoreCase("localhost")&&st.countTokens()!=4){
			message = "��������ַ����"	;
		}
		if(!"odbc".equals(dbconfig.getConnecttype())&&port.startsWith("0")){
			message = "�˿ں�����";
		}
		if(!"odbc".equals(dbconfig.getConnecttype())&&port.startsWith("0")){
			try{
				Integer.parseInt(port.trim());
			}catch (Exception e) {
				message = "�˿ں�����";// TODO: handle exception
			}
		}
		if(message!=null){
			return message;
		}
		if (dbtype.equals("oracle")&&!"odbc".equals(dbconfig.getConnecttype())) {
			dbconfig.setDburl("jdbc:oracle:thin:@" + server + ":" + port + ":"
					+ dbname);
			dbconfig.setDriverclazz("oracle.jdbc.driver.OracleDriver");
		} else if (dbtype.equals("mysql")&&!"odbc".equals(dbconfig.getConnecttype())) {
			dbconfig.setDburl("jdbc:mysql://" + server + ":" + port + "/"
					+ dbname);
			dbconfig.setDriverclazz("com.mysql.jdbc.Driver");
		} else if (dbtype.equals("mssqlserver")&&!"odbc".equals(dbconfig.getConnecttype())) {
			dbconfig.setDburl("jdbc:microsoft:sqlserver://" + server + ":"
					+ port + ";databaseName=" + dbname);
			dbconfig.setDriverclazz("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		} else if (dbtype.equals("db2")&&!"odbc".equals(dbconfig.getConnecttype())) {
			dbconfig.setDburl("jdbc:db2://" + server + ":" + port + "/"
					+ dbname);
			dbconfig.setDriverclazz("com.ibm.db2.jcc.DB2Driver");
		} else if (dbtype.equals("sybase")&&!"odbc".equals(dbconfig.getConnecttype())) {
			dbconfig.setDburl("jdbc:sybase:Tds:" + server + ":" + port
					+ "?ServiceName=" + dbname);
			dbconfig.setDriverclazz("com.sybase.jdbc.SybDriver");
		}else if("odbc".equals(dbconfig.getConnecttype())){
			dbconfig.setDriverclazz("sun.jdbc.odbc.JdbcOdbcDriver");
			dbconfig.setDburl("jdbc:odbc:"+dbconfig.getDatasourcename());
		}
		}catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 	������ݿ�����URL��Ϊ��������ַ���˿ںţ����ݿ�����ֵ
	 */
	private void resolutionUrl(Dbconfig dbconfig) {
		if(dbconfig==null||dbconfig.getDburl()==null){
			return;
		}
		try{
		String dburl = dbconfig.getDburl();
		String dbtype = dbconfig.getDbtype();
		if (dbtype.equals("oracle")) {
			int start = dburl.indexOf("@") + 1;
			int end = dburl.indexOf(":", start);
			String server = dburl.substring(start, end);
			int portend = dburl.indexOf(":", end + 1);
			String port = dburl.substring(end + 1, portend);
			String dbname = dburl.substring(portend + 1, dburl.length());
			dbconfig.setServer(server);
			dbconfig.setPort(port);
			dbconfig.setDbname(dbname);
		} else if (dbtype.equals("mysql")) {
			int start = dburl.indexOf("//") + 2;
			int end = dburl.indexOf(":", start);
			String server = dburl.substring(start, end);
			int portend = dburl.indexOf("/", end + 1);
			String port = dburl.substring(end + 1, portend);
			String dbname = dburl.substring(portend + 1);
			dbconfig.setServer(server);
			dbconfig.setPort(port);
			dbconfig.setDbname(dbname);
		} else if (dbtype.equals("mssqlserver")) {
			int start = dburl.indexOf("//") + 2;
			int end = dburl.indexOf(":", start);
			String server = dburl.substring(start, end);
			int portend = dburl.indexOf(";", end + 1);
			String port = dburl.substring(end + 1, portend);
			int dbend = dburl.indexOf("=", portend + 1);
			String dbname = dburl.substring(dbend+1);
			dbconfig.setServer(server);
			dbconfig.setPort(port);
			dbconfig.setDbname(dbname);
		} else if (dbtype.equals("db2")) {
			int start = dburl.indexOf("//") + 2;
			int end = dburl.indexOf(":", start);
			String server = dburl.substring(start, end);
			int portend = dburl.indexOf("/", end + 1);
			String port = dburl.substring(end + 1, portend);
			String dbname = dburl.substring(portend + 1);
			dbconfig.setServer(server);
			dbconfig.setPort(port);
			dbconfig.setDbname(dbname);
		} else if (dbtype.equals("sybase")) {
			int start = dburl.indexOf("Tds:") + 4;
			int end = dburl.indexOf(":", start);
			String server = dburl.substring(start, end);
			int portend = dburl.indexOf("?", end + 1);
			String port = dburl.substring(end + 1, portend);
			int dbstart = dburl.indexOf("=", portend + 1);
			String dbname = dburl.substring(dbstart + 1);
			dbconfig.setServer(server);
			dbconfig.setPort(port);
			dbconfig.setDbname(dbname);
		}
		}catch (Exception e) {
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
