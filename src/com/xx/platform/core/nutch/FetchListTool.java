package com.xx.platform.core.nutch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.DBList;
import com.xx.platform.core.rpc.ServerInterface;
import com.xx.platform.core.task.CrontabTimerTask;
import com.xx.platform.domain.model.database.*;
import com.xx.platform.util.constants.IbeaProperty;
import com.xx.platform.util.tools.blob.BlobThread;
import com.xx.platform.web.actions.BaseAction;
import com.xx.platform.web.actions.system.*;
import com.xx.platform.web.listener.WebApplicationContextListener;

import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.util.LogFormatter;

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
/**
 * �����blob���������ȡ�������̣���������� quhuan
 * **/
public class FetchListTool {
	public static final Logger LOG = LogFormatter
			.getLogger("com.xx.platform.core.nutch.FetchListTool");
	private String groupName = String.valueOf(System.currentTimeMillis()); //blobTread group name
	private ThreadGroup group = new ThreadGroup(groupName); // our group
	private Fetcher fetcher; //��ǰfetcherָ��
	public static long MAX_RAM_SIZE = 15 * 1024 * 1024;//���յ���Ϊjvm�����ڴ��50%��
	private long RAM_USED = 0; //blobʹ�ô���
	private boolean useRAM = false;
	private static Object obj = new Object(); //ͬ����
	public static long pernum = SearchContext.getXdtechsite().getDbnumber();
	private static List<Map<String, String>> dbList = new ArrayList<Map<String, String>>();
	private Dbtable dbtable;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");
	private final SimpleDateFormat dateFormat_oracle = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat userDefineDateFormat = null;
	private static Map<String, SimpleDateFormat> dateFormatMap = new HashMap();
	private boolean isblob = false;
	private String hasDates = null;
	private String hasDate2s = null;
	private static boolean isfirst = true;
	/**
	 * @desc ���� CTRL+C ���Ժ� �������ر�ʱ ������ڲɼ����ݣ���δ�ɼ���ɵ����ݱ����� segments Ŀ¼�µ� temp Ŀ¼
	 * DATABASE_TEMP_FILE_PATH �Ǳ����·��
	 * DATABASE_TEMP_FILE_NAME �Ǳ�����ļ���
	 */
	public static final String DATABASE_TEMP_FILE_PATH = SearchContext.search_dir
			+ File.separator + "segments" + File.separator + "temp";
	public static final String DATABASE_TEMP_FILE_NAME = "data.temp";
	private static List<Map<String, String>> pdfurl = new ArrayList<Map<String, String>>();

	public FetchListTool(Fetcher fetcher) {
		try {
			if (fetcher != null)
				this.fetcher = fetcher;
			/**
			 * ����ϴ�������ȡ�����йر��� ����������δ�ɼ������ݻ��浽�ļ��У����ֲɼ����ݵ�ʱ�����ȴӻ����������
			 * ��ȡ�Ѿ���������ݣ��������������δ�����л�������ݣ�����������ݿ��л�ȡ����
			 */
			dbList = getFetchListB() ;
			dbList = dbList!=null ? dbList :getFetchListA();
			//            while (dbList != null && dbList.size() < pernum &&
			//                   !(SearchContext.getXdtechsite().getMultableindex() + 1 >=
			//                     SearchContext.getDbtableList().size())) {
			//                dbList.addAll(getFetchListA());
			//            }
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}

	}

	public FetchListTool() {
		try {
			/**
			 * ����ϴ�������ȡ�����йر��� ����������δ�ɼ������ݻ��浽�ļ��У����ֲɼ����ݵ�ʱ�����ȴӻ����������
			 * ��ȡ�Ѿ���������ݣ��������������δ�����л�������ݣ�����������ݿ��л�ȡ����
			 */
			dbList = getFetchListB() ;
			dbList = dbList!=null ? dbList :getFetchListA();
			//            while (dbList != null && dbList.size() < pernum &&
			//                   !(SearchContext.getXdtechsite().getMultableindex() + 1 >=
			//                     SearchContext.getDbtableList().size())) {
			//                dbList.addAll(getFetchListA());
			//            }
			//            if (dbList == null || dbList.size() == 0) {
			//                dbList = new ArrayList<Map<String, String>>();
			//                DirectoryFethListTool tool = new DirectoryFethListTool();
			//                dbList.addAll(tool.getDbList());
			//            }
		} catch (Exception ex1) {
			ex1.printStackTrace();
		} finally {
			if (useRAM) {
				//        		fetcher.closeWriter();
				fetcher = null;
			}
		}
	}

	public FetchListTool(boolean file) {
		try {
			if (file) {
				dbList = new ArrayList<Map<String, String>>();
				pdfurl = new ArrayList<Map<String, String>>();
				//            DirectoryFethListTool tool = new DirectoryFethListTool();
//				NetDirectoryFethListTool tool = new NetDirectoryFethListTool();
//				dbList.addAll(tool.getDbList());
//				pdfurl.addAll(tool.getPdfurl());
//				tool.clearFileCache();
			}
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}
	}

	public FetchListTool(List<Map<String, String>> list) {
		dbList = list;
	}

	public static File createNewSegment() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		File segmentFile = null;
		if (segmentFile == null) {
			segmentFile = new File(SearchContext.getSegFilePath(), format
					.format(new java.util.Date()));
		}
		if (!segmentFile.exists()) {
			segmentFile.mkdirs();
		}
		return segmentFile;
	}

	/**
	 * ���ݴ���� segment Ŀ¼��� �����ļ�Ŀ¼
	 * @param path String
	 * @return String
	 */
	public static File getRootPath(String path) {
		File file = new File(path);
		return file.getParentFile().getParentFile();
	}

	/**
	 * �������
	 * @return List
	 */

	public synchronized List getFetchListA() throws Exception {
		List<Dbtable> dataBasedbtableList = SearchContext.getDbtableList();
		List<Dbtable> dbtableList = new ArrayList<Dbtable>(dataBasedbtableList);
		List<Dbtable> removeList=new ArrayList<Dbtable>();
		String xdsiteIp=SearchContext.getXdtechsite().getLocalip();
		for(Dbtable table:dbtableList){
			String dbconfigIP=table.getDbid().getLocalip();
			if(dbconfigIP!=null){
				dbconfigIP=dbconfigIP.trim();
			}
			if(xdsiteIp!=null){
				xdsiteIp=xdsiteIp.trim();
			}
			boolean isOk=true;
			if(dbconfigIP!=null){
				isOk=dbconfigIP.equals(xdsiteIp);
			}
			if(!isOk){
				removeList.add(table);
			}
		}
		for(Dbtable table:removeList){
			System.out.println("�Ƴ�:"+table.getName());
		}
		if(!removeList.isEmpty()){
			dbtableList.removeAll(removeList);
		}
		List<Map<String, String>> dbDataList = null;
		if (dbtableList != null && dbtableList.size() > 0) {
			if (SearchContext.getXdtechsite().getMultableindex() >= dbtableList
					.size()) {
				SearchContext.getXdtechsite().setMultableindex(0);
			}
			dbtable = dbtableList.get(SearchContext.getXdtechsite()
					.getMultableindex());
			if (CrontabTimerTask.runningtask != null)//�����ǰ��ʱ��������ִ�л���ϵͳ�ر�
			{
				if (CrontabTimerTask.runningtask != null
						&& CrontabTimerTask.runningtask.getDbtid() != null
						&& !CrontabTimerTask.runningtask.getDbtid().equals("")
						&& !CrontabTimerTask.runningtask.getDbtid().equals(
								dbtable.getId()))//���runningtaskָ����table�ɼ�,��ǰtable��id�Ƿ��runningtask��table��id����ͬ,hujun
				{
					if (SearchContext.getXdtechsite().getMultableindex() + 1 >= dbtableList
							.size()) {
						SearchContext.getXdtechsite().setMultableindex(0);
					} else {
						SearchContext.getXdtechsite().setMultableindex(
								SearchContext.getXdtechsite()
										.getMultableindex() + 1);
					}
					 if (SearchContext.getXdtechsite().getMultableindex()+1< dbtableList.size()) 
		                    return getFetchListA();
		                    else
		                    return dbDataList;
				}
			}
			if (SearchContext.isShutDown) {
				return null;
			}
			createConnection(dbtable.getDbid());
			dbDataList = getData(dbtable);
			if (dbDataList.size() < pernum) {
				if (SearchContext.getXdtechsite().getMultableindex() + 1 >= dbtableList
						.size()) {
					SearchContext.getXdtechsite().setMultableindex(0);
				} else {
					SearchContext.getXdtechsite()
							.setMultableindex(
									SearchContext.getXdtechsite()
											.getMultableindex() + 1);
				}
				SearchContext.getDataHandler().updateIObject(
						SearchContext.getXdtechsite());
			}
			SearchContext.getDataHandler().updateIObject(dbtable);
		}
		return dbDataList;
	}

	/**
	 * ��ȡ�����е��ļ�
	 * @return
	 * @throws Exception
	 */
	public synchronized List getFetchListB() throws Exception {
		File file = new File(DATABASE_TEMP_FILE_PATH, DATABASE_TEMP_FILE_NAME);
		List dataList = null ;
		if (file.exists()) {
			Object data = FetchListTool.readFSFile(file);
			if(data instanceof List)
				dataList = (List)data ;
		}
		return dataList;
	}

	/**
	 * Fetcher ���е��ô˷��������л�����
	 * @param obj
	 * @param file
	 */
	public static Object readFSFile(File file) {
		ObjectInputStream objInpuput = null;
		FileInputStream inputStream = null;
		Object obj = null;
		boolean hasClose = false;
		try {
			inputStream = new FileInputStream(file);
			objInpuput = new ObjectInputStream(inputStream);
			obj = objInpuput.readObject();
			objInpuput.close() ;
			objInpuput.close() ;
			hasClose = true ;
			file.delete() ;
		} catch (Exception e) {
			LOG.info("��ȡ��ʱ�����ļ�[" + file.getAbsoluteFile() + "]����������Ϣ��"
					+ e.getMessage());
		} finally {
			if (!hasClose) {
				try {
					if (objInpuput != null)
						objInpuput.close();
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//  			e.printStackTrace();
				}
			}
		}
		return obj;
	}

	/**
	 * Fetcher ���е��ô˷��������л�����
	 * @param obj
	 * @param file
	 */
	public static void creatSFile(Object obj, File file) {
		ObjectOutputStream objOutput = null;
		FileOutputStream inputStream = null;
		try {
			inputStream = new FileOutputStream(file);
			objOutput = new ObjectOutputStream(inputStream);
			objOutput.writeObject(obj);
		} catch (Exception e) {
			LOG.info("������ʱ�����ļ�[" + file.getAbsoluteFile() + "]����������Ϣ��"
					+ e.getMessage());
		} finally {
			try {
				if (objOutput != null)
					objOutput.close();
				if (inputStream != null)
					inputStream.close();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//  			e.printStackTrace();
			}
		}
	}

	private void createConnection(Dbconfig dbconfig) throws Exception {
		if (dbconfig.getConnection() == null
				|| dbconfig.getConnection().isClosed()) {
			try {
				Class.forName(dbconfig.getDriverclazz());
				Connection conn=null;
				if(!"odbc".equals(dbconfig.getConnecttype())){
					conn=DriverManager.getConnection(dbconfig.getDburl(), dbconfig.getDbuser(), dbconfig.getDbpass());	
				}else{
					if(dbconfig.getDbuser()!=null||dbconfig.getDbuser().trim().length()>0||dbconfig.getDbpass()!=null||dbconfig.getDbpass().trim().length()>0){
						conn=DriverManager.getConnection(dbconfig.getDburl(),dbconfig.getDbuser(),dbconfig.getDbpass());
					}else{
						conn=DriverManager.getConnection(dbconfig.getDburl());
					}
				}
				if(conn==null){
					System.out.println("���ݿ�����ʧ�ܣ���������ǰ�����ã�\r\n����:"
							+ dbconfig.getDriverclazz() + "\r\n����url:"
							+ dbconfig.getDburl() + "\r\n�û���:"
							+ dbconfig.getDbuser() + "\r\n����:"
							+ dbconfig.getDbpass());
					ControlAction.addXdmessage("���ݿ�����ʧ�ܣ���������ǰ�����ã�\r\n����:"
							+ dbconfig.getDriverclazz() + "\r\n����url:"
							+ dbconfig.getDburl() + "\r\n�û���:"
							+ dbconfig.getDbuser() + "\r\n����:"
							+ dbconfig.getDbpass());
				}
				dbconfig.setConnection(conn);
			} catch (Exception ex) {
				System.out.println("���ݿ�����ʧ�ܣ���������ǰ�����ã�\r\n����:"
						+ dbconfig.getDriverclazz() + "\r\n����url:"
						+ dbconfig.getDburl() + "\r\n�û���:"
						+ dbconfig.getDbuser() + "\r\n����:"
						+ dbconfig.getDbpass());
				ControlAction.addXdmessage("���ݿ�����ʧ�ܣ���������ǰ�����ã�\r\n����:"
						+ dbconfig.getDriverclazz() + "\r\n����url:"
						+ dbconfig.getDburl() + "\r\n�û���:"
						+ dbconfig.getDbuser() + "\r\n����:"
						+ dbconfig.getDbpass());
			}
		}
	}

	private List<Map<String, String>> getData(Dbtable dbtable) {
		Statement statement = null;
		ResultSet rs = null;
		List dataList = new ArrayList();

		String hasDate1 = null;
		String hasDate2 = null;
		String sql = "";

		boolean hasft = false;//�Ƿ���������ֶ�,�����ھͲ����κδ���.
		boolean hasasoft = false;//�Ƿ�������������ֶ�
		for (Tableproperty prop : dbtable.getTableproperty()) {
			if (prop.getIndexfield() != null
					&& !"".equals(prop.getIndexfield()))
				hasft = true;
			if (prop.getAsoindexfield() != null
					&& !prop.getAsoindexfield().equals(""))
				hasasoft = true;
		}
		if (!hasft && !hasasoft)//��������ڶ�Ӧ�������Ҳ����ڶ�Ӧ�����������ֶΣ���ֱ�ӷ���
		{
			ControlAction.addXdmessage("��" + dbtable.getName()
					+ "û�ж�Ӧ�κ������������Ա�" + dbtable.getName() + "�Ĳɼ���");
			return dataList;
		}
		/**
		if (dbtable.getDbid().getDbtype() != null &&
		        dbtable.getDbid().getDbtype().equals("mssqlserver")) {
			Iterator<Tableproperty> it = dbtable.getTableproperty().iterator();
			Tableproperty property = null;
			String name=null;
			while (it.hasNext()) {
				property = it.next();
				if(property!=null&&property.getUispk().equals(true))
				{
					name=property.getName();
				}
			}
			if(name==null)
			{
				System.out.println("ϵͳ��ʾ:sqlserver����ѡ��һ��Ψһ���������ֶ���Ϊ�����ֶ�");
				return	dataList;
			}
		}
		 **/

		if (dbtable.getDbid().getDbtype() != null
				&& dbtable.getDbid().getDbtype().equals("mssqlserver")) {
			sql = getSQL(dbtable);
			if ("".equals(sql)) {
				System.out.println("��" + dbtable.getName()
						+ "�ɼ�ʧ�ܣ�mssqlserver��Ҫ����һ�����±�־���ܲɼ����ݡ�");
				ControlAction.addXdmessage("��" + dbtable.getName()
						+ "�ɼ�ʧ�ܣ�mssqlserver��Ҫ����һ�����±�־���ܲɼ����ݡ�");
				return dataList;
			}
		} else if (dbtable.getDbid().getDbtype() != null
				&& dbtable.getDbid().getDbtype().equals("mysql")) {
			sql = getMYSQL(dbtable);
		} else if (dbtable.getDbid().getDbtype() != null
				&& dbtable.getDbid().getDbtype().equals("oracle")) {
			sql = getORACLE(dbtable);
		} else if (dbtable.getDbid().getDbtype() != null
				&& dbtable.getDbid().getDbtype().equals("db2")) {
			sql = getDB2(dbtable);
		} else {
			return dataList;
		}
		IbeaProperty.log.info("SQL:" + sql + " database:"
				+ dbtable.getDbid().getName());
		ControlAction.addXdmessage("��ʼ�������ݿ�[" + dbtable.getDbid().getName()
				+ "]���ݱ�[" + dbtable.getName() + "]");
		Date beginTime1 = new Date();
		try {
			try {
				statement = dbtable.getDbid().getConnection().createStatement();
			} catch (Exception e) {
				return dataList;
			}
			rs = statement.executeQuery(sql);
			java.sql.ResultSetMetaData meta = rs.getMetaData();
			int column = meta.getColumnCount();
			int number = 0;//�����Ŀ
			//            Map rowdata = null;
			String indexField = null, dataValue = null;
			StringBuffer strb = new StringBuffer();
			int rNum = 0;
//			NetDirectoryFethListTool tool = new NetDirectoryFethListTool();
			while (rs.next() && !SearchContext.isShutDown) {
				number++;
				Map rowdata = new HashMap<String, String>();
				rowdata.put("docType", dbtable.getCode());

				rowdata.put("docSource", "database");

				String dburl = dbtable.getDbid().getDburl();
				String dbtype = dbtable.getDbid().getDbtype();
				String ip = "";
				String dbname = "";
				try {
					if (dbtype.equals("oracle")) {
						int start = dburl.indexOf("@") + 1;
						int end = dburl.indexOf(":", start);
						ip = dburl.substring(start, end);
						int portend = dburl.indexOf(":", end + 1);
						//dbname= dburl.substring(portend + 1, dburl.length());
						dbname = dbtable.getDbid().getDbuser();
					} else if (dbtype.equals("mysql")) {
						int start = dburl.indexOf("//") + 2;
						int end = dburl.indexOf(":", start);
						ip = dburl.substring(start, end);
						int portend = dburl.indexOf("/", end + 1);
						dbname = dburl.substring(portend + 1);

					} else if (dbtype.equals("mssqlserver")) {
						int start = dburl.indexOf("//") + 2;
						int end = dburl.indexOf(":", start);
						ip = dburl.substring(start, end);
						int portend = dburl.indexOf(";", end + 1);
						int dbend = dburl.indexOf("=", portend + 1);
						dbname = dburl.substring(dbend + 1);
					} else if (dbtype.equals("db2")) {
						int start = dburl.indexOf("//") + 2;
						int end = dburl.indexOf(":", start);
						ip = dburl.substring(start, end);
						int portend = dburl.indexOf("/", end + 1);
						dbname = dburl.substring(portend + 1);
					} else if (dbtype.equals("sybase")) {
						int start = dburl.indexOf("Tds:") + 4;
						int end = dburl.indexOf(":", start);
						ip = dburl.substring(start, end);
						int portend = dburl.indexOf("?", end + 1);
						int dbstart = dburl.indexOf("=", portend + 1);
						dbname = dburl.substring(dbstart + 1);
					}
				} catch (Exception e) {
					dbname = "";
					ip = "";
					//�û�����Ƿ��Ĳ���
				}
				rowdata.put("dataSource", dbname);

				rowdata.put("url", ip);

				String userfilename = "";//�洢���ݿ��ļ������͵�����
				String userfilepath = "";//�洢���ݿ��ļ�����Ŀ¼���͵�����

				for (int i = 1; i <= column; i++) {
					String typeName = meta.getColumnTypeName(i);
					String colName = meta.getColumnName(i);
					String isod = "";
					String isfiletype = "";//�ñ��ֶ��Ƿ�洢�ļ���Ϣhj
					isfiletype = getIsfiletype(colName.toUpperCase(), dbtable,
							i);
					indexField = getIndexFieldCode(colName.toUpperCase(),
							dbtable, i);
					isod = getIsorderby(colName.toUpperCase(), dbtable, i);

					if ((indexField != null && !"".equals(indexField))
							|| "1".equals(isod) || "2".equals(isod)
							|| (isfiletype != null && !isfiletype.equals("0"))) {
						dataValue = null;
						String text = null;
						if (typeName.equalsIgnoreCase("clob")) {
							Clob clob = rs.getClob(i);
							if (clob == null) {
								text = "";
							} else {
								text = clob.getSubString(1L, (int) clob
										.length());
							}
							// if (text != null)
							//rowdata.put(meta.getColumnName(i).toUpperCase(),text);
						} else if (typeName.equalsIgnoreCase("blob")) {
							isblob = true;
							//System.out.println("row:"+rs.getRow());
							Blob blob = rs.getBlob(i);
							if (blob == null) {
								text = "";
							} else {
								byte[] content = null;
								java.io.InputStream ins = blob
										.getBinaryStream();
								java.io.BufferedInputStream bfin = null;
								try {
									content = new byte[(int) blob.length()];
									ins.read(content);
									bfin = new BufferedInputStream(
											new java.io.ByteArrayInputStream(
													content));

								} finally {
									if (ins != null) {
										ins.close();
									}
								}
								RAM_USED += blob.length();
								BlobThread bt = new BlobThread(group, groupName
										+ rNum, rowdata, indexField, bfin);
								bt.start();
								//                                BlobTool bt = new BlobTool(blob.getBinaryStream(),
								//                                        "");
								//              BlobTool bt = new BlobTool(new java.io.ByteArrayInputStream(clob.getBytes(0,(int)clob.length())));
								//                                text = bt.extract();
								//if (text != null)
								//rowdata.put(meta.getColumnName(i).toUpperCase(),text);
							}
						}
						//image�ֶν�������blob��ʽ
						else if (typeName.equalsIgnoreCase("image")) {
							isblob = true;
							java.io.InputStream fis = rs.getBinaryStream(i);
							byte[] b = null;
							int len = 0;
							int lens = 0;
							java.io.ByteArrayOutputStream bs = new java.io.ByteArrayOutputStream();
							if (fis != null) {
								try {
									byte[] buffer = new byte[1024];

									while ((len = fis.read(buffer)) > 0) {
										//								System.out.println("�ļ�While");
										lens += len;
										if (len > 0) {
											bs.write(buffer, 0, len);
											bs.flush();
										}
									}
									b = bs.toByteArray();
								} finally {
									bs.close();
								}

								BlobThread bt = new BlobThread(group, groupName
										+ rNum, rowdata, indexField,
										new java.io.ByteArrayInputStream(b));
								bt.start();
							}

						}

						else if (typeName.equalsIgnoreCase("date")
								|| typeName.equalsIgnoreCase("datetime")
								|| typeName.equalsIgnoreCase("smalldatetime")
								|| (typeName.equalsIgnoreCase("timestamp") && !dbtable
										.getDbid().getDbtype()
										.equalsIgnoreCase("mssqlserver"))) {
							Tableproperty tp = getUserDefineDateFormatd(meta
									.getColumnName(i).toUpperCase(), dbtable, i);
							if (tp != null && tp.getMultfuction() != null
									&& !tp.getMultfuction().equals("")) {
								if ((userDefineDateFormat = dateFormatMap
										.get(tp.getId())) == null) {
									userDefineDateFormat = new SimpleDateFormat(
											tp.getMultfuction());
									dateFormatMap.put(tp.getId(),
											userDefineDateFormat);
								}
							}
							Object data = null;
							if (dbtable.getDbid().getDbtype().equalsIgnoreCase(
									"oracle"))
								data = rs.getTimestamp(i);
							else
								data = rs.getObject(i);
							//System.out.println(data);
							if (hasDates != null
									&& isTimeField(hasDates, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDates)) {
									Date lastTime = null;
									lastTime = convert(data, dbtable.getDbid()
											.getDbtype());

									if (lastTime != null
											&& !lastTime.equals("")) {
										dbtable.setLasttime(lastTime);
									}
								}
							} else if (hasDate2s != null
									&& isTimeField(hasDate2s, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDate2s)) {
									Date lastTime = null;
									lastTime = convert(data, dbtable.getDbid()
											.getDbtype());
									if (lastTime != null
											&& !lastTime.equals("")) {
										dbtable.setLasttime(lastTime);
									}
								}
							}

							{
								if (userDefineDateFormat != null) {
									text = convert(data, dbtable.getDbid()
											.getDbtype(), dbtable.getDbid()
											.getCode(), userDefineDateFormat);
									userDefineDateFormat = null;
								} else {
									text = convert(data, dbtable.getDbid()
											.getDbtype(), dbtable.getDbid()
											.getCode(), dateFormat);
								}
							}
						} else if (typeName.equalsIgnoreCase("timestamp")
								&& dbtable.getDbid().getDbtype()
										.equalsIgnoreCase("mssqlserver")) {

							byte[] data = null;
							data = rs.getBytes(i);
							String str = "";
							for (int n = 0; n < data.length; n++) {
								if (!String.valueOf(data[n]).equals("0")) {
									String s = String.valueOf(data[n]);
									s = Integer.toHexString(Integer.valueOf(s));//ת����16���Ƶ��ַ���
									if (s.length() > 2)
										s = s.substring(s.length() - 2);
									if (s.length() == 1)
										s = "0" + s;
									str += s;
								} else {
									str += "00";
								}
							}
							str = "0x" + str;
							text = str;

							if (hasDate2s != null
									&& !isTimeField(hasDate2s, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDate2s)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							} else if (hasDates != null
									&& !isTimeField(hasDates, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDates)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							}
						} else if (dbtable.getDbid().getDbtype().equals(
								"oracle")
								&& typeName.equalsIgnoreCase("number")) {

							String numbers = "";
							Object o=null;
							o = rs.getObject(i);
							if (o != null) {
								if(!(o instanceof String)){
									numbers = String.valueOf(o);
								}else{
									numbers = o.toString();
								}
							}
							text = numbers;
							if (hasDate2s != null
									&& !isTimeField(hasDate2s, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDate2s)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							} else if (hasDates != null
									&& !isTimeField(hasDates, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDates)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							}

						} else {

							Object dataO = null;
							try {
								if("odbc".equals(dbtable.getDbid().getConnecttype())){
									dataO = rs.getString(i);
								}else{
									dataO = rs.getObject(i);
								}
							} catch (Exception e) {
								//��Щ�ֶ������null���������������ܻ��״�
							}
							if (dataO != null && !"".equals(dataO))
								text = new String(dataO.toString().getBytes(
										dbtable.getDbid().getCode()), dbtable
										.getDbid().getCode());
							else
								text = "";
							if (hasDate2s != null
									&& !isTimeField(hasDate2s, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDate2s)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							} else if (hasDates != null
									&& !isTimeField(hasDates, dbtable)) {
								if (colName != null
										&& colName.equalsIgnoreCase(hasDates)
										&& !"".equals(text)) {
									dbtable.setIdnum(text);
								}
							}
						}
						if (isfiletype != null && isfiletype.equals("1"))
							userfilename = text;
						if (isfiletype != null && isfiletype.equals("2"))
							userfilepath = text;

						if (text != null && indexField != null
								&& !"".equals(indexField)) {
							if ((dataValue = ((String) rowdata.get(indexField))) != null) {
								rowdata.remove(indexField);
								strb.setLength(0);
								strb.append(dataValue).append(" ").append(text);
								rowdata.put(indexField, strb.toString());
								strb.setLength(0);
							} else {
								rowdata.put(indexField, text);
							}
						}
					}
				}
				for (Tableproperty pro : dbtable.getTableproperty()) {
					indexField = null;
					dataValue = null;
					if (!pro.isDbfield()) {
						if ((dataValue = (String) rowdata.get(pro
								.getIndexfield())) != null) {
							rowdata.remove(pro.getIndexfield());
							strb.setLength(0);
							if (pro.getDefaultvalue() == null
									|| "".equals(pro.getDefaultvalue())) {
								pro.setDefaultvalue("");
							}
							strb.append(dataValue).append(" ").append(
									pro.getDefaultvalue());
							if (pro.getIndexfield() != null
									&& !pro.getIndexfield().equals(""))
								rowdata.put(pro.getIndexfield(), strb
										.toString());
							strb.setLength(0);
						} else {
							if (pro.getIndexfield() != null
									&& !pro.getIndexfield().equals("")
									&& pro.getDefaultvalue() != null
									&& !pro.getDefaultvalue().equals(""))
								rowdata.put(pro.getIndexfield(), pro
										.getDefaultvalue());
						}
						if (pro.getIsfiledata() != null
								&& pro.getIsfiledata().equals("1"))
							userfilename = pro.getDefaultvalue();
						if (pro.getIsfiledata() != null
								&& pro.getIsfiledata().equals("2"))
							userfilepath = pro.getDefaultvalue();

					}
					//                    else{
					//                         //�ж��Ƿ�д��Ĭ��ֵ��1Ϊȫ����д��Ĭ��ֵ��0Ϊ�յ��ֶ�д��Ĭ��ֵ
					//                        if ("1".equals(pro.getLength())) {
					//                                rowdata.remove(pro.getIndexfield());
					//                                rowdata.put(pro.getIndexfield(),pro.getDefaultvalue());
					//                        } else if ("0".equals(pro.getLength())) {
					//                            if (rowdata.get(pro.getIndexfield()) == null ||"".equals(rowdata.get(pro.getIndexfield()))) {
					//                                rowdata.remove(pro.getIndexfield());
					//                                rowdata.put(pro.getIndexfield(),pro.getDefaultvalue());
					//                            }
					//                        }
					//                    }
				}
				if (userfilename != null && userfilepath != null
						&& !userfilename.equals("") && !userfilepath.equals("")) {
					/*
					 * ������������ļ������������ļ�Ŀ¼��������ļ����ݳ�ȡ�������ض�����filecontent  hj
					 */

					if (!userfilepath.valueOf(userfilepath.length() - 1)
							.equals(File.separator))
						userfilepath = userfilepath + File.separator;
					String did = "";
					List<IndexFieldImpl> indexFieldList = SearchContext
							.getDao().findAllByIObjectCType(
									IndexFieldImpl.class);
					String asoid = "";
					for (Tableproperty pro : dbtable.getTableproperty()) {
						if (pro.getAsoindexfield() != null
								&& !pro.getAsoindexfield().equals(""))
							asoid = pro.getAsoindexfield();
					}
					for (IndexFieldImpl d : indexFieldList) {
						if (d.getId().equals(asoid)) {
							did = d.getId();
							break;
						}
					}
					if (!did.equals("")) {
						String filecontent = "";
						try {
							ControlAction.addXdmessage("���ϲɼ��ɼ��ļ���"
									+ userfilepath + userfilename);
//							filecontent = tool.getDateByPath(userfilepath
//									+ userfilename);
							ControlAction.addXdmessage("�ɼ��ļ��ɹ����ļ�����"
									+ filecontent.length() + "����");
						} catch (Exception e) {
							ControlAction.addXdmessage("���ݿ����ϲɼ�������һ���쳣��"
									+ e.getMessage() + " ��鿴���ļ�·���Ƿ���ȷ��");
							filecontent = "";
						}
						if (filecontent != null
								&& !filecontent.replace(" ", "").equals(""))
							if ((dataValue = (String) rowdata.get(did)) != null) {
								rowdata.remove(did);
								strb.setLength(0);
								strb.append(dataValue).append(" ").append(
										filecontent);
								rowdata.put(did, strb.toString());
								strb.setLength(0);
							} else
								rowdata.put(did, filecontent);
					}
				}

				dataList.add(rowdata);
				rNum++;
				//System.out.println("********************************************"+rNum);
				if (RAM_USED >= MAX_RAM_SIZE * 0.9) {
					useRAM = true;
					List<String> stopThreads = new ArrayList<String>();
					while (true) {
						Thread.sleep(1000);
						int n = group.activeCount();
						Thread[] list = new Thread[n];
						group.enumerate(list);
						boolean noMoreThread = true; // assumption
						for (int i = 0; i < n; i++) {
							// this thread may have gone away in the meantime
							if (list[i] == null)
								continue;
							String tname = list[i].getName();
							long currentTime = System.currentTimeMillis();
							if (tname.startsWith(groupName)
									&& !stopThreads.contains(tname)) { // prove it
								noMoreThread = false;
								BlobThread blobThread = (BlobThread) list[i];
								long runTime = currentTime
										- blobThread.getStartTime();
								if (blobThread.getStartTime() != 0
										&& runTime > BaseAction.default_parse_time) {
									stopThreads.add(tname);
									blobThread.stop();
								}

							}
						}
						if (noMoreThread) {
							break;
						}
					}

					//                    fetcher.run_1(new FetchListTool(dataList));
					RAM_USED = 0;
					//                    dataList=new ArrayList();
					if (rNum > 0) {
						dbtable.setPagenum(dbtable.getPagenum() + rNum);
						//System.out.println("!!!!!!!!!!!!!!!!!"+rNum);
					} else {
						dbtable.setPagenum(dbtable.getPagenum());
						//System.out.println("@@@@@@@@@@@@@@@@@@@"+rNum);
					}
					rNum = 0;
					isblob = false;
				}
			}
			//��ĩһ��

			if (isblob) {
				isblob = false;
				useRAM = true;
				List<String> stopThreads = new ArrayList<String>();
				while (true) {
					Thread.sleep(1000);
					int n = group.activeCount();
					Thread[] list = new Thread[n];
					group.enumerate(list);
					boolean noMoreThread = true; // assumption
					for (int i = 0; i < n; i++) {
						// this thread may have gone away in the meantime
						if (list[i] == null)
							continue;
						String tname = list[i].getName();
						long currentTime = System.currentTimeMillis();
						if (tname.startsWith(groupName)
								&& !stopThreads.contains(tname)) { // prove it &&!stopThreads.contains(tname)
							noMoreThread = false;
							BlobThread blobThread = (BlobThread) list[i];
							long runTime = currentTime
									- blobThread.getStartTime();
							if (blobThread.getStartTime() != 0
									&& runTime > BaseAction.default_parse_time) {
								stopThreads.add(tname);
								blobThread.stop();
							}

						}
					}
					if (noMoreThread) {
						break;
					}
				}
				//System.out.println("if1............................if1==="+dbtable.getName()+" : "+fetcher);
				//                  if(useRAM && dataList.size()>0){
				//                       fetcher.run_1(new FetchListTool(dataList));
				//                  }
				//System.out.println("if2............................if2==="+dbtable.getName());
				RAM_USED = 0;

			}
			if (rNum > 0) {
				dbtable.setPagenum(dbtable.getPagenum() + rNum);
			} else {
				dbtable.setPagenum(dbtable.getPagenum());
			}
			Date endTime = new Date();
			double timeOfSearch1 = (endTime.getTime() - beginTime1.getTime()) / 1000.0;
			ControlAction.addXdmessage("�������ݿ�[" + dbtable.getDbid().getName()
					+ "]���ݱ�[" + dbtable.getName() + "]��ϣ��õ�����"
					+ dataList.size() + "������ʱ��" + timeOfSearch1 + "��");
		} catch (Exception ex) {
			ex.printStackTrace();
			if (ex.getMessage() != null) {
				ControlAction.addXdmessage("���ݿ�ɼ�������һ���쳣��" + ex.getMessage()
						+ " ���鿴����������á�");
				if (sql != null && !sql.equals(""))
					ControlAction.addXdmessage("SQL���ű�" + sql);
			} else
				ControlAction.addXdmessage("���ݿ�ɼ�������һ���쳣,���鿴����������á�");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (dbtable.getDbid().getConnection() != null) {
					dbtable.getDbid().getConnection().close();
					dbtable.getDbid().setConnection(null);
				}
				//               if(useRAM){
				//                   fetcher.closeWriter();
				//                   fetcher = null;
				//               }
			} catch (Exception ex1) {
				ex1.printStackTrace();
				if (ex1.getMessage() != null) {
					ControlAction.addXdmessage("���ݿ�ɼ�������һ���쳣��"
							+ ex1.getMessage() + "���鿴����������á�");
					if (sql != null && !sql.equals(""))
						ControlAction.addXdmessage("SQL���ű�" + sql);
				} else
					ControlAction.addXdmessage("���ݿ�ɼ�������һ���쳣�����鿴����������á�");
			}
		}

		long totalnumber = 0;
		try {
			for (int i = 0; i < dataList.size(); i++) {
				Map m = (Map) dataList.get(i);
				Iterator it = m.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					Object value = entry.getValue();
					totalnumber += value.toString().length();
				}
			}
		} catch (Exception e) {
			totalnumber = 0;
		}
		if(RuntimeDataCollect.diserver!=0)//��������ֲ�ʽ
		{
		ControlAction.addXdmessage("�����ַ�ͳ��:" + totalnumber + "����������ڵ㷢�����ݡ�");
		ControlAction.addXdmessage("   ");
		}
		else
		ControlAction.addXdmessage("�����ַ�ͳ��:" + totalnumber + "�������ڽ�������������");
		return dataList;
	}

	private String getIndexFieldCode(String columnName, Dbtable dbtable,
			int index) {
		String indexField = null;
		int i = 1;
		for (Tableproperty tp : dbtable.getTableproperty()) {
			if (tp.getCode().equalsIgnoreCase(columnName)) {
				indexField = tp.getIndexfield();
				if (i == index) {
					break;
				}
			}
			i++;
		}
		return indexField;
	}

	private String getIsorderby(String columnName, Dbtable dbtable, int index) {
		String isorderby = null;
		int i = 1;
		for (Tableproperty tp : dbtable.getTableproperty()) {
			if (tp.getCode().equalsIgnoreCase(columnName)) {
				isorderby = tp.getIsorderby();
				//if (i == index) {
				break;
				//}
			}
			i++;
		}
		return isorderby;
	}

	private String getIsfiletype(String columnName, Dbtable dbtable, int index) {
		String isfiletype = null;
		int i = 1;
		for (Tableproperty tp : dbtable.getTableproperty()) {
			if (tp.getCode().equalsIgnoreCase(columnName)) {
				isfiletype = tp.getIsfiledata();
				//if (i == index) {
				break;
				//}
			}
			i++;
		}
		return isfiletype;
	}

	private Tableproperty getUserDefineDateFormatd(String columnName,
			Dbtable dbtable, int index) {
		Tableproperty dateFormat = null;
		int i = 1;
		for (Tableproperty tp : dbtable.getTableproperty()) {
			if (tp.getCode().equalsIgnoreCase(columnName)) {
				dateFormat = tp;
				if (i == index) {
					break;
				}
			}
			i++;
		}
		return dateFormat;
	}

	private String convert(Object data, String dbType, String encode,
			SimpleDateFormat dateFormat) throws Exception {
		if (data == null)
			return "";
		if (encode == null || encode.equals("")) {
			encode = "ISO8859-1";
		}
		if (dbType.equals("oracle")
				&& data.getClass().isAssignableFrom(oracle.sql.Datum.class)) {
			return dateFormat.format(((oracle.sql.Datum) data).dateValue());
		} else if (dbType.equals("oracle")
				&& data.getClass().isAssignableFrom(oracle.sql.DATE.class)) {
			return dateFormat.format(((oracle.sql.DATE) data).dateValue());
		} else if (data.getClass().isAssignableFrom(java.sql.Date.class)) {
			return dateFormat.format(new java.util.Date(((java.sql.Date) data)
					.getTime()));
		} else if (data.getClass().isAssignableFrom(java.util.Date.class)) {
			return dateFormat.format(((java.util.Date) data));
		} else if (data.getClass().isAssignableFrom(java.sql.Timestamp.class)) {
			return dateFormat.format(new java.util.Date(
					((java.sql.Timestamp) data).getTime()));
		} else if (data.getClass().isAssignableFrom(oracle.sql.TIMESTAMP.class)) {
			return dateFormat.format(new java.util.Date(
					((oracle.sql.TIMESTAMP) data).timestampValue().getTime()));
		} else {
			return new String(data.toString().getBytes(encode), encode);
		}
	}

	private java.util.Date convert(Object data, String dbType) throws Exception {
		if (data == null)
			return null;
		if (dbType.equals("oracle")
				&& data.getClass().isAssignableFrom(oracle.sql.Datum.class)) {
			return ((oracle.sql.Datum) data).dateValue();

		} else if (data.getClass().isAssignableFrom(java.sql.Date.class)) {
			return new java.util.Date(((java.sql.Date) data).getTime());

		} else if (data.getClass().isAssignableFrom(java.util.Date.class)) {
			return ((java.util.Date) data);

		} else if (data.getClass().isAssignableFrom(java.sql.Timestamp.class)) {
			return new java.util.Date(((java.sql.Timestamp) data).getTime());

		} else if (data.getClass().isAssignableFrom(oracle.sql.TIMESTAMP.class)) {
			return new java.util.Date(((oracle.sql.TIMESTAMP) data)
					.timestampValue().getTime());

		}
		return null;
	}

	private boolean isTimeField(String code, Dbtable dbtable) {
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield() && pro.getCode().equals(code)) {
				if ("java.util.Date".equalsIgnoreCase(pro.getDatatype())) {
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	private boolean isStringField(String code, Dbtable dbtable) {
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield() && pro.getCode().equals(code)) {
				if ("String".equalsIgnoreCase(pro.getDatatype())) {
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	private String getSQL(Dbtable dbtable) {
		String hasDate1 = null;
		String hasDate2 = null;
		StringBuffer sql = new StringBuffer("SELECT TOP " + pernum + " ");
		StringBuffer proSQL = new StringBuffer();
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield()) {
				if (proSQL.length() > 0) {
					proSQL.append(",");
				}
				proSQL.append("[" + pro.getCode() + "]");
				if ("1".equals(pro.getIsorderby())) {
					hasDate1 = pro.getCode();
					hasDates = hasDate1;
				} else if ("2".equals(pro.getIsorderby())) {
					hasDate2 = pro.getCode();
					hasDate2s = hasDate2;
				}
			}
		}

		if (hasDate1 == null && hasDate2 == null) {
			return "";
		}

		if (hasDate1 == null && hasDate2 != null) {
			hasDate1 = hasDate2;
			hasDate2 = null;
		}

		boolean Isbinary = false;
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield() && pro.getCode().equals(hasDate1)) {
				if ("binary".equalsIgnoreCase(pro.getDatatype())) {
					Isbinary = true;
				}
			}
		}

		sql.append(proSQL.toString());
		sql.append(" FROM [").append(dbtable.getCode()).append("]");

		if ((dbtable.getLasttime() == null || "".equals(dbtable.getLasttime()))
				&& (dbtable.getIdnum() == null || "".equals(dbtable.getIdnum()))) {
			sql.append(" WHERE [" + hasDate1 + "] NOT IN (").append(
					"SELECT TOP ").append("0").append(
					" [" + hasDate1 + "] FROM [").append(dbtable.getCode())
					.append("]");

			if (hasDate2 != null && !"".equals(hasDate2.trim())) {
				sql.append("  ORDER BY [").append(hasDate1).append("],[")
						.append(hasDate2).append("]  ASC ) ORDER BY [").append(
								hasDate1 + "],[" + hasDate2 + "]  ASC");
			} else if (hasDate2 == null || "".equals(hasDate2.trim())) {
				sql.append("  ORDER BY [").append(hasDate1).append(
						"] ASC ) ORDER BY [").append(hasDate1 + "]  ASC");
			}

		} else if ((dbtable.getLasttime() != null && !"".equals(dbtable
				.getLasttime()))
				|| (dbtable.getIdnum() != null && !""
						.equals(dbtable.getIdnum()))) {

			if (hasDate2 != null && !"".equals(hasDate2.trim())) {
				if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& (dbtable.getIdnum() == null || "".equals(dbtable
								.getIdnum()))) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append(" where [" + hasDate1 + "] >'"
								+ dateFormat.format(dbtable.getLasttime())
								+ "'  ORDER BY  [" + hasDate1 + "]  ASC");
					} else {
						sql.append(" where [" + hasDate2 + "] >'"
								+ dateFormat.format(dbtable.getLasttime())
								+ "'  ORDER BY  [" + hasDate2 + "]  ASC");
					}
				} else if ((dbtable.getLasttime() == null || "".equals(dbtable
						.getLasttime()))
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (!isTimeField(hasDate1, dbtable)) {
						if (!isStringField(hasDate1, dbtable))
							sql.append(" where [" + hasDate1 + "] >"
									+ dbtable.getIdnum() + "  ORDER BY ["
									+ hasDate1 + "]  ASC");
						else
							sql.append(" where [" + hasDate1 + "] >'"
									+ dbtable.getIdnum() + "'  ORDER BY ["
									+ hasDate1 + "]  ASC");

					} else {
						if (!isStringField(hasDate2, dbtable)) {
							sql.append(" where [" + hasDate2 + "] >"
									+ dbtable.getIdnum() + "  ORDER BY ["
									+ hasDate2 + "]  ASC");
						} else {
							sql.append(" where [" + hasDate2 + "] >'"
									+ dbtable.getIdnum() + "'  ORDER BY ["
									+ hasDate2 + "]  ASC");
						}

					}
				} else if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						if (!isStringField(hasDate2, dbtable)) {
							sql.append(" where [" + hasDate1 + "] >'"
									+ dateFormat.format(dbtable.getLasttime())
									+ "'  or  ([" + hasDate1 + "] ='"
									+ dateFormat.format(dbtable.getLasttime())
									+ "' and [" + hasDate2 + "] >"
									+ dbtable.getIdnum() + " ) ORDER BY ["
									+ hasDate1 + "], [" + hasDate2 + "]  ASC");
						} else {
							sql.append(" where [" + hasDate1 + "] >'"
									+ dateFormat.format(dbtable.getLasttime())
									+ "'  or  ([" + hasDate1 + "] ='"
									+ dateFormat.format(dbtable.getLasttime())
									+ "' and [" + hasDate2 + "] >'"
									+ dbtable.getIdnum() + "' ) ORDER BY ["
									+ hasDate1 + "], [" + hasDate2 + "]  ASC");
						}
					} else {
						if (!isStringField(hasDate1, dbtable)) {
							sql.append(" where [" + hasDate1 + "] >"
									+ dbtable.getIdnum() + "  or  (["
									+ hasDate1 + "] =" + dbtable.getIdnum()
									+ " and [" + hasDate2 + "] >'"
									+ dateFormat.format(dbtable.getLasttime())
									+ "' )  ORDER BY [" + hasDate1 + "], ["
									+ hasDate2 + "]  ASC");
						} else {
							sql.append(" where [" + hasDate1 + "] >'"
									+ dbtable.getIdnum() + "'  or  (["
									+ hasDate1 + "] ='" + dbtable.getIdnum()
									+ "' and [" + hasDate2 + "] >'"
									+ dateFormat.format(dbtable.getLasttime())
									+ "' )  ORDER BY [" + hasDate1 + "], ["
									+ hasDate2 + "]  ASC");
						}

					}
				}
			} else {//���ֻ������hasDate1
				if (isTimeField(hasDate1, dbtable)) {
					sql.append(" where [" + hasDate1 + "] >'"
							+ dateFormat.format(dbtable.getLasttime())
							+ "'  ORDER BY  [" + hasDate1 + "]  ASC");
				} else {
					if (!isStringField(hasDate1, dbtable)) {
						sql.append(" where [" + hasDate1 + "] >"
								+ dbtable.getIdnum() + "  ORDER BY  ["
								+ hasDate1 + "]  ASC");
					} else {
						sql.append(" where [" + hasDate1 + "] >'"
								+ dbtable.getIdnum() + "'  ORDER BY  ["
								+ hasDate1 + "]  ASC");
					}
				}
			}

		}

		return sql.toString();
	}

	private String getMYSQL(Dbtable dbtable) {
		StringBuffer sql = new StringBuffer("SELECT ");
		StringBuffer proSQL = new StringBuffer();
		String hasDate1 = null;
		String hasDate2 = null;
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield()) {
				if (proSQL.length() > 0) {
					proSQL.append(",");
				}
				proSQL.append("`" + pro.getCode() + "`");
				if ("1".equals(pro.getIsorderby())) {
					hasDate1 = pro.getCode();
					hasDates = hasDate1;
				} else if ("2".equals(pro.getIsorderby())) {
					hasDate2 = pro.getCode();
					hasDate2s = hasDate2;
				}
			}
		}
		sql.append(proSQL.toString());
		sql.append(" FROM `").append(dbtable.getCode()).append("`");
		if ((dbtable.getLasttime() == null || "".equals(dbtable.getLasttime()))
				&& (dbtable.getIdnum() == null || "".equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				sql.append("  ORDER BY `").append(hasDate1).append("`,`")
						.append(hasDate2).append("` ASC ");
			} else if (hasDate1 != null && hasDate2 == null) {
				sql.append("  ORDER BY `").append(hasDate1).append("` ASC ");
			} else if (hasDate1 == null && hasDate2 != null) {
				sql.append("  ORDER BY `").append(hasDate2).append("` ASC ");
			}
			sql.append(" LIMIT ")
					.append(
							dbtable.getPagenum() != null
									&& dbtable.getPagenum() > 0 ? dbtable
									.getPagenum() : 0).append(",").append(
							pernum);
		} else if ((dbtable.getLasttime() != null && !"".equals(dbtable
				.getLasttime()))
				|| (dbtable.getIdnum() != null && !""
						.equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& (dbtable.getIdnum() == null || "".equals(dbtable
								.getIdnum()))) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where `").append(hasDate1).append("`>'")
								.append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' ORDER BY `").append(hasDate1)
								.append("` ASC ");
					} else {
						sql.append("  where `").append(hasDate2).append("`>'")
								.append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' ORDER BY `").append(hasDate2)
								.append("` ASC ");
					}
				} else if ((dbtable.getLasttime() == null || "".equals(dbtable
						.getLasttime()))
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where `").append(hasDate2).append("`> '")
								.append(dbtable.getIdnum()).append(
										"' ORDER BY `").append(hasDate2)
								.append("` ASC ");
					} else {
						sql.append("  where `").append(hasDate1).append("`> '")
								.append(dbtable.getIdnum()).append(
										"' ORDER BY `").append(hasDate1)
								.append("` ASC ");
					}
				} else if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where `").append(hasDate1).append("`>'")
								.append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' or (`").append(hasDate1).append(
										"`='").append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' and `").append(hasDate2).append(
										"`> '").append(dbtable.getIdnum())
								.append("' )").append(" ORDER BY `").append(
										hasDate1).append("`,`")
								.append(hasDate2).append("` ASC ");
					} else {
						sql.append("  where `").append(hasDate1).append("`>'")
								.append(dbtable.getIdnum()).append("' or (`")
								.append(hasDate1).append("`='").append(
										dbtable.getIdnum()).append("' and `")
								.append(hasDate2).append("`>'").append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' )").append(" ORDER BY `").append(
										hasDate1).append("`,`")
								.append(hasDate2).append("` ASC ");
					}
				}
			} else if (hasDate1 != null && hasDate2 == null) {
				if (isTimeField(hasDate1, dbtable)) {
					sql.append("  where `").append(hasDate1).append("`>'")
							.append(dateFormat.format(dbtable.getLasttime()))
							.append("' ORDER BY `").append(hasDate1).append(
									"` ASC ");
				} else {
					sql.append("  where `").append(hasDate1).append("`>'")
							.append(dbtable.getIdnum()).append("' ORDER BY `")
							.append(hasDate1).append("` ASC ");
				}
			} else if (hasDate1 == null && hasDate2 != null) {
				if (!isTimeField(hasDate2, dbtable)) {
					sql.append("  where `").append(hasDate2).append("`> '")
							.append(dbtable.getIdnum()).append("' ORDER BY `")
							.append(hasDate2).append("` ASC ");
				} else {
					sql.append("  where `").append(hasDate2).append("`> '")
							.append(dateFormat.format(dbtable.getLasttime()))
							.append("' ORDER BY `").append(hasDate2).append(
									"` ASC ");
				}
			}
			if (hasDate1 == null && hasDate2 == null) {
				sql.append(" LIMIT ").append(
						dbtable.getPagenum() != null
								&& dbtable.getPagenum() > 0 ? dbtable
								.getPagenum() : 0).append(",").append(pernum);
			} else {
				sql.append(" LIMIT ").append(0).append(",").append(pernum);
			}

		}

		return sql.toString();
	}

	/**
	 * db2
	 * @param dbtable Dbtable
	 * @return String
	 */
	private String getDB2(Dbtable dbtable) {
		String hasDate1 = null;
		String hasDate2 = null;
		boolean isString1 = false;
		boolean isString2 = false;
		StringBuffer sql = new StringBuffer("SELECT * FROM (SELECT ");
		StringBuffer proSQL = new StringBuffer();
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield()) {
				if (proSQL.length() > 0) {
					proSQL.append(",");
				}
				proSQL.append(dbtable.getCode()).append(".").append(
						pro.getCode());
				if ("1".equals(pro.getIsorderby())) {
					hasDate1 = pro.getCode();
					isString1 = isStringField(hasDate1, dbtable);
					hasDates = hasDate1;
				} else if ("2".equals(pro.getIsorderby())) {
					hasDate2 = pro.getCode();
					isString2 = isStringField(hasDate2, dbtable);
					hasDate2s = hasDate2;
				}
			}
		}
		sql.append(proSQL.toString()).append(" ,rownumber() over() AS rn ")
				.append(" FROM ").append(dbtable.getCode());

		if ((dbtable.getLasttime() == null || "".equals(dbtable.getLasttime()))
				&& (dbtable.getIdnum() == null || "".equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				sql.append("  ORDER BY ").append(hasDate1).append(",").append(
						hasDate2).append(" ASC ");
			} else if (hasDate1 != null && hasDate2 == null) {
				sql.append("  ORDER BY ").append(hasDate1).append(" ASC ");
			} else if (hasDate1 == null && hasDate2 != null) {
				sql.append("  ORDER BY ").append(hasDate2).append(" ASC ");
			}
			sql.append(") AS a1 WHERE a1.rn BETWEEN ").append(
					dbtable.getPagenum()).append(" AND ").append(
					dbtable.getPagenum() + pernum);
		} else if ((dbtable.getLasttime() != null && !"".equals(dbtable
				.getLasttime()))
				|| (dbtable.getIdnum() != null && !""
						.equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& (dbtable.getIdnum() == null || "".equals(dbtable
								.getIdnum()))) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where ").append(hasDate1).append(">")
								.append(
										"'"
												+ dateFormat.format(dbtable
														.getLasttime()) + "'")
								.append(" ORDER BY ").append(hasDate1).append(
										" ASC ");
					} else {
						sql.append("  where ").append(hasDate2).append(">")
								.append(
										"'"
												+ dateFormat.format(dbtable
														.getLasttime()) + "'")
								.append(" ORDER BY ").append(hasDate2).append(
										" ASC ");
					}
				} else if ((dbtable.getLasttime() == null || "".equals(dbtable
						.getLasttime()))
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {

						sql.append("  where ").append(hasDate2).append(">")
								.append(isString2 ? "'" : " ").append(
										dbtable.getIdnum()).append(
										isString2 ? "'" : " ").append(
										"  ORDER BY ").append(hasDate2).append(
										" ASC ");
					} else {
						sql.append("  where ").append(hasDate1).append(">")
								.append(isString1 ? "'" : " ").append(
										dbtable.getIdnum()).append(
										isString1 ? "'" : " ").append(
										"  ORDER BY ").append(hasDate1).append(
										" ASC ");
					}
				} else if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where ").append(hasDate1).append(">'")
								.append(
										dateFormat
												.format(dbtable.getLasttime()))
								.append("' or (").append(hasDate1).append("=")
								.append(
										"'"
												+ dateFormat.format(dbtable
														.getLasttime()) + "'")
								.append(" and ").append(hasDate2).append(">")
								.append(isString2 ? "'" : " ").append(
										dbtable.getIdnum()).append(
										isString2 ? "'" : " ").append(" )")
								.append(" ORDER BY ").append(hasDate1).append(
										",").append(hasDate2).append(" ASC ");
					} else {
						sql.append("  where ").append(hasDate1).append(">")
								.append(isString1 ? "'" : " ").append(
										dbtable.getIdnum()).append(
										isString1 ? "'" : " ").append(" or (")
								.append(hasDate1).append("=").append(
										isString1 ? "'" : " ").append(
										dbtable.getIdnum()).append(
										isString1 ? "'" : " ").append(" and ")
								.append(hasDate2).append(">").append(
										"'"
												+ dateFormat.format(dbtable
														.getLasttime()) + "'")
								.append(" )").append(" ORDER BY ").append(
										hasDate1).append(",").append(hasDate2)
								.append(" ASC ");
					}

				}
			} else if (hasDate1 != null && hasDate2 == null) {
				if (isTimeField(hasDate1, dbtable)) {
					sql.append("  where ").append(hasDate1).append(">").append(
							"'" + dateFormat.format(dbtable.getLasttime())
									+ "'").append(" ORDER BY ")
							.append(hasDate1).append(" ASC ");
				} else {
					sql.append("  where ").append(hasDate1).append(">").append(
							isString1 ? "'" : " ").append(dbtable.getIdnum())
							.append(isString1 ? "'" : " ")
							.append("  ORDER BY ").append(hasDate1).append(
									" ASC ");
				}
			} else if (hasDate1 == null && hasDate2 != null) {
				if (!isTimeField(hasDate2, dbtable)) {
					sql.append("  where ").append(hasDate2).append(">").append(
							isString2 ? "'" : " ").append(dbtable.getIdnum())
							.append(isString2 ? "'" : " ")
							.append("  ORDER BY ").append(hasDate2).append(
									" ASC ");
				} else {
					sql.append("  where ").append(hasDate2).append(">").append(
							"'" + dateFormat.format(dbtable.getLasttime())
									+ "'").append(" ORDER BY ")
							.append(hasDate2).append(" ASC ");
				}
			}
			if (hasDate1 == null && hasDate2 == null) {
				sql.append(") AS a1 WHERE a1.rn BETWEEN ").append(
						dbtable.getPagenum()).append(" AND ").append(
						dbtable.getPagenum() + pernum);
			} else {
				sql.append(") AS a1 WHERE a1.rn BETWEEN ").append("0").append(
						" AND ").append(pernum);
			}

		}

		return sql.toString();
	}

	private String getORACLE(Dbtable dbtable) {
		String hasDate1 = null;
		String hasDate2 = null;
		StringBuffer sql = new StringBuffer("SELECT * FROM(");
		StringBuffer proSQL = new StringBuffer();
		for (Tableproperty pro : dbtable.getTableproperty()) {
			if (pro.isDbfield()) {
				if (proSQL.length() > 0) {
					proSQL.append(",");
				}
				proSQL.append(pro.getCode());
				if ("1".equals(pro.getIsorderby())) {
					hasDate1 = pro.getCode();
					hasDates = hasDate1;
				} else if ("2".equals(pro.getIsorderby())) {
					hasDate2 = pro.getCode();
					hasDate2s = hasDate2;
				}
			}
		}
		sql.append(" SELECT row_.*, rownum rownum_ FROM( SELECT ");
		sql.append(proSQL.toString());
		sql.append(" FROM ").append(dbtable.getCode());
		if ((dbtable.getLasttime() == null || "".equals(dbtable.getLasttime()))
				&& (dbtable.getIdnum() == null || "".equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				sql.append("  ORDER BY ").append(hasDate1).append(",").append(
						hasDate2).append(" ASC ");
			} else if (hasDate1 != null && hasDate2 == null) {
				sql.append("  ORDER BY ").append(hasDate1).append(" ASC ");
			} else if (hasDate1 == null && hasDate2 != null) {
				sql.append("  ORDER BY ").append(hasDate2).append(" ASC ");
			}
			sql.append(") row_ WHERE rownum <=").append(
					(dbtable.getPagenum() + pernum));
			sql.append(") WHERE rownum_>").append(dbtable.getPagenum());
		} else if ((dbtable.getLasttime() != null && !"".equals(dbtable
				.getLasttime()))
				|| (dbtable.getIdnum() != null && !""
						.equals(dbtable.getIdnum()))) {
			if (hasDate1 != null && hasDate2 != null) {
				if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& (dbtable.getIdnum() == null || "".equals(dbtable
								.getIdnum()))) {
					if (isTimeField(hasDate1, dbtable)) {
						sql
								.append("  where ")
								.append(hasDate1)
								.append(">")
								.append(
										"TO_DATE('"
												+ dateFormat_oracle
														.format(dbtable
																.getLasttime())
												+ "', 'YYYY-MM-DD HH24:MI:SS')")
								.append(" ORDER BY ").append(hasDate1).append(
										" ASC ");
					} else {
						sql
								.append("  where ")
								.append(hasDate2)
								.append(">")
								.append(
										"TO_DATE('"
												+ dateFormat_oracle
														.format(dbtable
																.getLasttime())
												+ "', 'YYYY-MM-DD HH24:MI:SS')")
								.append(" ORDER BY ").append(hasDate2).append(
										" ASC ");
					}
				} else if ((dbtable.getLasttime() == null || "".equals(dbtable
						.getLasttime()))
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						sql.append("  where ").append(hasDate2).append(">'")
								.append(dbtable.getIdnum()).append(
										"' ORDER BY ").append(hasDate2).append(
										" ASC ");
					} else {
						sql.append("  where ").append(hasDate1).append(">'")
								.append(dbtable.getIdnum()).append(
										"' ORDER BY ").append(hasDate1).append(
										" ASC ");
					}
				} else if (dbtable.getLasttime() != null
						&& !"".equals(dbtable.getLasttime())
						&& dbtable.getIdnum() != null
						&& !"".equals(dbtable.getIdnum())) {
					if (isTimeField(hasDate1, dbtable)) {
						sql
								.append("  where ")
								.append(hasDate1)
								.append(">")
								.append(
										"TO_DATE('"
												+ dateFormat_oracle
														.format(dbtable
																.getLasttime())
												+ "', 'YYYY-MM-DD HH24:MI:SS')")
								.append(" or (")
								.append(hasDate1)
								.append("=")
								.append(
										"TO_DATE('"
												+ dateFormat_oracle
														.format(dbtable
																.getLasttime())
												+ "', 'YYYY-MM-DD HH24:MI:SS')")
								.append(" and ").append(hasDate2).append("> '")
								.append(dbtable.getIdnum()).append("' )")
								.append(" ORDER BY ").append(hasDate1).append(
										",").append(hasDate2).append(" ASC ");
					} else {
						sql
								.append("  where ")
								.append(hasDate1)
								.append(">'")
								.append(dbtable.getIdnum())
								.append("' or (")
								.append(hasDate1)
								.append("='")
								.append(dbtable.getIdnum())
								.append("' and ")
								.append(hasDate2)
								.append("> ")
								.append(
										"TO_DATE('"
												+ dateFormat_oracle
														.format(dbtable
																.getLasttime())
												+ "', 'YYYY-MM-DD HH24:MI:SS')")
								.append(" )").append(" ORDER BY ").append(
										hasDate1).append(",").append(hasDate2)
								.append(" ASC ");
					}

				}
			} else if (hasDate1 != null && hasDate2 == null) {
				if (isTimeField(hasDate1, dbtable)) {
					sql.append("  where ").append(hasDate1).append(">").append(
							"TO_DATE('"
									+ dateFormat_oracle.format(dbtable
											.getLasttime())
									+ "', 'YYYY-MM-DD HH24:MI:SS')").append(
							" ORDER BY ").append(hasDate1).append(" ASC ");
				} else {
					sql.append("  where ").append(hasDate1).append(">'")
							.append(dbtable.getIdnum()).append("' ORDER BY ")
							.append(hasDate1).append(" ASC ");
				}
			} else if (hasDate1 == null && hasDate2 != null) {
				if (!isTimeField(hasDate2, dbtable)) {
					sql.append("  where ").append(hasDate2).append(">'")
							.append(dbtable.getIdnum()).append("' ORDER BY ")
							.append(hasDate2).append(" ASC ");
				} else {
					sql.append("  where ").append(hasDate2).append(">").append(
							"TO_DATE('"
									+ dateFormat_oracle.format(dbtable
											.getLasttime())
									+ "', 'YYYY-MM-DD HH24:MI:SS')").append(
							" ORDER BY ").append(hasDate2).append(" ASC ");
				}
			}
			if (hasDate1 == null && hasDate2 == null) {
				sql.append(") row_ WHERE rownum <=").append(
						(dbtable.getPagenum() + pernum));
				sql.append(") WHERE rownum_>").append(dbtable.getPagenum());
			} else {
				sql.append(") row_ WHERE rownum <=").append(pernum);
				sql.append(") WHERE rownum_>").append(0);
			}

		}

		return sql.toString();
	}

	public synchronized void update(WebDB webDb) throws Exception {

	}

	/**
	 * ����ҳ��
	 * @param webDb WebDB
	 * @throws Exception
	 */
	public synchronized void updateError(WebDB webDb) throws Exception {

	}

	public synchronized Map next(Map dataMap) throws MalformedURLException {
		if (pdfurl != null && pdfurl.size() > 0) {
			dataMap = pdfurl.remove(0);
		} else if (dbList != null && dbList.size() > 0) {
			dataMap = dbList.remove(0);
		} else {
			dataMap = null;
		}
		return dataMap;
	}

	public void add(List<Map<String, String>> dataMapList)
			throws MalformedURLException {
		synchronized (obj) {
			if (dbList != null && dbList.size() > 0) {
				dbList.addAll(dataMapList);
			} else {
				dbList = dataMapList;
			}
		}
	}

	public List getDbList() {
		return dbList;
	}

	public Dbtable getDbtable() {
		return dbtable;
	}

	public void setDbList(List dbList) {
		this.dbList = dbList;
	}

	public void setDbtable(Dbtable dbtable) {
		this.dbtable = dbtable;
	}

	public static List<Map<String, String>> getPdfurl() {
		return pdfurl;
	}

	public static boolean isIsfirst() {
		return isfirst;
	}

	public static void setIsfirst(boolean isfirst) {
		FetchListTool.isfirst = isfirst;
	}
}
