package com.xx.platform.core.nutch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import jcifs.smb.SmbFileInputStream;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.nutch.ipc.RPC;
import org.apache.nutch.util.NutchConf;
import org.apache.nutch.util.StringUtil;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.hibernate.criterion.DetachedCriteria;
import org.mozilla.universalchardet.UniversalDetector;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.core.nutch.EMLAnalyzer.Attachment;
import com.xx.platform.core.nutch.EMLAnalyzer.MailUser;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.database.FileDirectory;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.BackUnParseFile;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.util.tools.blob.PDFParserTool;
import com.xx.platform.util.tools.ipcheck.GetLocalIp;
import com.xx.platform.util.tools.ms.RTFExtractor;

public class NetDirectoryFethListTool {
	private static boolean fetchAttachment = NutchConf.get().getBoolean("nutch.crawler.eml", false);
	private DateFormat df = new SimpleDateFormat("yyyyMMdd");
	private StringBuffer sbTemp = new StringBuffer();
	private static Object obj = new Object(); // 同步锁
	public static String[] titleFields = new String[4];

	public static final String path = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace(
			"/WEB-INF/classes", "/upload");
	private static int wordlength = NutchConf.get().getInt("textfilter.wordlength", 3);// private
																						// int
																						// threadCount
																						// =
																						// //
																						// max
	// number of threads

	private static boolean isback = false; // 是否备份不能解析的文件
	public static String[] fields;
	private List<String> fileCache = null; // 文件地址缓存，避免遍历文件重复现象
	static {
		try {
			List<IndexFieldImpl> fieldsSet = SearchContext.getDataHandler().findAllByIObjectCType(IndexFieldImpl.class);
			List<String> fieldList = new ArrayList<String>();
			for (IndexFieldImpl field : fieldsSet) {
				if (field.getCode() != null && field.getCode().equals("a")) {
					titleFields[0] = field.getId();
				} else if (field.getCode() != null && field.getCode().equals("b")) {
					titleFields[1] = field.getId();
				} else if (field.getCode() != null && field.getCode().equals("c")) {
					titleFields[2] = field.getId();
				} else if (field.getCode() != null && field.getCode().equals("d")) {
					titleFields[3] = field.getId();
				} else if (field.getCode() != null && field.getCode().length() > 1) {
					fieldList.add(field.getId());

				}
			}
			fields = new String[fieldList.size()];
			fieldList.toArray(fields);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("init fields failed[" + e.getMessage() + "]");
			fields = null;
		}
	}
	private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
	public static final int count = 50000;
	public int num = 0; // 已取得的数据数量
	public static String url = "http://www.www.com";
	public static String NET_FILE = "_XD-TECH_QUHUAN_FILE_NET_12345DLKFJ234_!@#&%^"; // 文件标志,希望不要和某个字段内容重复
	public static String SIGN_FILE = "_XD-TECH_QUHUAN_FILE_SIGN_12345DLKFJ234_!@#&%^"; // 文件标志,希望不要和某个字段内容重复
	private List<FileDirectory> directoryList; // 需要索引的目录
	private List<Map<String, Object>> dataList; // 最终取到的数据
	private static List<Map<String, String>> dataList1; // 最终取到的数据
	private static List<Map<String, String>> fileurl;
	private List<Map<String, String>> pdfurl;

	// private FileDirectory fd;

	public NetDirectoryFethListTool() {
		directoryList = SearchContext.getDataHandler()
				.findAllByCriteria(DetachedCriteria.forClass(FileDirectory.class));
		// 移除不是本机目录的本地目录和网络目录
		List<FileDirectory> remove = new ArrayList<FileDirectory>();
		String ip = SearchContext.getXdtechsite().getLocalip();
		for (FileDirectory file : directoryList) {
			// if("local".equals(file.getDirtype())){
			if (RuntimeDataCollect.isSynchroIsRunning() && !ip.equals(file.getLocalip())) {
				remove.add(file);
			}
			// }
		}

		directoryList.removeAll(remove);
		dataList1 = new ArrayList<Map<String, String>>();
		fileurl = new ArrayList<Map<String, String>>();
		pdfurl = new ArrayList<Map<String, String>>();
		fileCache = new ArrayList<String>();
	}

	public void clearFileCache() {
		fileCache.clear();
	}

	// private List<File> fileList; //目录下所有的文件
	public List<Map<String, String>> getDbList() {
		FileDirectory f = null;
		int pre = 0;
		while (directoryList.size() > 0) {

			f = directoryList.remove(0);

			if (f.getDirtype().equalsIgnoreCase("local")) {
				File file = new File(f.getPath());
				if (f.getFileTypes() != null) {
					getFileFromD(file, f);
				} else {
					// System.out.println("为选择采集文件类型，终止");
				}
			} else if (f.getDirtype().equalsIgnoreCase("remote")) {
				String url = "smb://" + f.getUName() + ":" + f.getUPwd() + "@" + f.getRemoteIPaddress()
						+ f.getRemoteFiledir();
				if (f.getFileTypes() != null) {
					getFileUrl(f.getFileTypes(), f.getIslayers(), url);
				} else {
					// System.out.println("未选择采集文件类型，终止！");
				}
			}
			// System.out.println("目录："+f.getPath()+" 文件数："+(fileurl.size()-pre));
			pre = fileurl.size();
		}
		return fileurl;
	}

	private List<Map<String, String>> getFileFromD(File file, FileDirectory fd) {
		String islayers = fd.getIslayers();
		if (file.isFile()) {
			String md5 = MD5.encoding(file.getAbsolutePath());
			if (!fileCache.contains(md5)) {
				String filepath = file.getName();
				if (filepath.indexOf(".") >= 0) {
					String file_kind = filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length())
							.toLowerCase();
					if (file_kind.equals("htm")) {
						String filetype = fd.getFileTypes().toLowerCase();
						if ((filetype.indexOf(file_kind) >= 0 && filetype.indexOf("html") == -1)
								|| (filetype.indexOf(file_kind) >= 0 && filetype.indexOf("html") >= 0 && filetype
										.indexOf(file_kind) != filetype.indexOf("html"))) {
							Map<String, String> map = new HashMap<String, String>();
							map.put(SIGN_FILE, file.getAbsolutePath());
							fileurl.add(map);
							fileCache.add(md5);
						}
					} else if (fd.getFileTypes().toLowerCase().indexOf(file_kind) > -1 && file_kind.equals("pdf")) {
						Map<String, String> map = new HashMap<String, String>();
						map.put(SIGN_FILE, file.getAbsolutePath());
						fileCache.add(MD5.encoding(file.getAbsolutePath()));
						pdfurl.add(map);
						fileCache.add(md5);
					} else if (fd.getFileTypes().toLowerCase().indexOf(file_kind) > -1
							&& fd.getDirtype().equals("local")) {
						Map<String, String> map = new HashMap<String, String>();
						fileCache.add(MD5.encoding(file.getAbsolutePath()));
						map.put(SIGN_FILE, file.getAbsolutePath());
						fileurl.add(map);
						fileCache.add(md5);
					}
				}
			}
		} else if (file.isDirectory() && islayers != null && !islayers.equals("end")) {
			File[] childs = file.listFiles();
			if (childs != null) {
				if (islayers != null && islayers.equals("false"))
					fd.setIslayers("end");
				for (File f : childs)
					getFileFromD(f, fd);
			}
		}
		return fileurl;

	}

	// 获取网络文件开始
	public List<Map<String, String>> getFileUrl(String FileType, String dirNext, String URL) {
		// List<String> urlList = new ArrayList();

		String url = URL;

		/***********************************************************************
		 * 
		 * 定义返回的结果集
		 **********************************************************************/
		Map<String, String> map = null;
		try {
			SmbFile file = new SmbFile(url);
			SmbFile[] list = findSharefiles(file, dirNext);
			for (int j = 0; j < list.length; j++) {
				map = new HashMap<String, String>();
				if (list[j].isDirectory()) {
				}
				if (list[j].isFile()) {
					String md5 = MD5.encoding(list[j].getPath());
					if (!fileCache.contains(md5)) {
						String file_kind = list[j].getName().substring(list[j].getName().lastIndexOf(".") + 1)
								.toLowerCase().trim();
						String filetype = FileType.toLowerCase();
						if (file_kind.equals("htm")) {
							if ((filetype.indexOf(file_kind) >= 0 && filetype.indexOf("html") == -1)
									|| (filetype.indexOf(file_kind) >= 0 && filetype.indexOf("html") >= 0 && filetype
											.indexOf(file_kind) != filetype.indexOf("html"))) {
								map.put(NET_FILE, list[j].getPath());
								fileurl.add(map);
								fileCache.add(md5);
							}
						} else if (filetype.indexOf(file_kind) > -1) {
							map.put(NET_FILE, list[j].getPath());
							fileurl.add(map);
							fileCache.add(md5);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileurl;
	}

	public static SmbFile[] findSharefiles(SmbFile dir, String dirnext) {
		Stack curPath = new Stack();
		curPath.push(dir);
		return findFiles(curPath, dirnext);
	}

	public static final int FIND_SUB = 0; // 找子节点
	public static final int FIND_SIB = 1; // 找同级节点
	public static final int FIND_END = 2; // 结束

	public static SmbFile[] findFiles(Stack curPath, String dirNext) {
		/** 检测目录 */
		final String dirNext1 = dirNext;
		class MyDirFilter implements SmbFileFilter {
			public boolean accept(SmbFile pathname) {
				try {
					if (dirNext1.equalsIgnoreCase("true")) {
						return (pathname != null) && pathname.isDirectory();
					} else {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}

		/** 检测文件 */

		class MyFileFilter implements SmbFileFilter {
			public boolean accept(SmbFile pathname) {
				try {
					return (pathname != null) && pathname.isFile();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		MyDirFilter dirFilter = new MyDirFilter();
		MyFileFilter fileFilter = new MyFileFilter();
		int state = FIND_SUB; // 开始
		LinkedHashSet found = new LinkedHashSet();
		while (state != FIND_END) {
			SmbFile dir = (SmbFile) curPath.pop(); // 当前目录
			if (state == FIND_SUB) { // 查找子节点
				SmbFile[] subDirs = null;
				try {
					subDirs = dir.listFiles(dirFilter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (subDirs == null || subDirs.length == 0) { // 没有子点
					curPath.push(dir);
					state = FIND_SIB; // 下一次需要找同级节点
				} else {
					curPath.push(dir);
					curPath.push(subDirs[0]);
					state = FIND_SUB;
				}
			} else if (state == FIND_SIB) { // 查找同级节点
				SmbFile[] files = null;
				try {
					files = dir.listFiles(fileFilter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						found.add(files[i]);
					}
				}
				if (curPath.isEmpty()) {
					state = FIND_END; // 已经没有可以找的了，需要退出查找过程
				} else {
					SmbFile parentDir = (SmbFile) curPath.peek();
					SmbFile[] sibDirs = null;
					try {
						sibDirs = parentDir.listFiles(dirFilter);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < sibDirs.length; i++) {
						if (dir.equals(sibDirs[i])) { // 找到了当前的位置
							found.add(sibDirs[i]);
							if (i + 1 < sibDirs.length) { // 存在下一个同级节点
								curPath.push(sibDirs[i + 1]);
								state = FIND_SUB; // 需要查找子节点
							} else { // 这就是最后一个同级节点
								state = FIND_SIB;
							}
							break;
						}
					}
				}
			}
		}
		return (SmbFile[]) found.toArray(new SmbFile[found.size()]);
	}

	// 获取网络文件结束

	// 判断文件类型并进行解析
	private List<Map<String, String>> getDateFromFile(InputStream is, String docSource, String title, boolean islocal,
			long lastModify, String serverurl, long length) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (!index(docSource, String.valueOf(lastModify))) {
			Map<String, String> rowdata = new HashMap<String, String>();
			String filepath = docSource;
			docSource = docSource.toLowerCase();
			rowdata.put("subType", MD5.encoding(filepath + String.valueOf(lastModify)));
			rowdata.put("docSource", "file");
			if (length == 0) {
				rowdata.put("contentLength", "0");
			} else {
				long flength = length / 1024;
				if (flength == 0) {
					flength = 1;
				}
				rowdata.put("contentLength", String.valueOf(flength));
			}

			String dataSource = "";
			if (islocal) {
				dataSource = docSource;
			} else {
				String ip = serverurl;
				int start = docSource.indexOf(ip) + ip.length();
				dataSource = docSource.substring(start);
			}
			rowdata.put("dataSource", dataSource);
			rowdata.put("title", title);
			rowdata.put("updataDate", String.valueOf(lastModify));
			rowdata.put("url", serverurl);
			String content = "";
			String doctype = docSource.substring(docSource.lastIndexOf(".") + 1);
			if (doctype == null) {
				doctype = "";
			}
			rowdata.put("docType", doctype);
			if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("doc"))) {
				content = processWORD(is, filepath);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("pdf"))) {
				if (islocal) {
					content = processPDF(filepath);
				} else {
					content = processPDF(is, filepath);
				}
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("rtf"))) {
				content = processRTF(is, filepath);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("xls"))) {
				content = processXLS(is);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("docx"))) {
				content = processDOCX(is);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx"))) {
				content = processXLSX(is);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("mht"))) {
				content = processMHT(is);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("ppt"))) {
				content = processPPT(is);
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("pptx"))) {
				content = processPPTX(is);// 暂时无法解析ppt2007
			} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("eml"))) {
				content = processEML(is, rowdata);// 解析eml
			} else if (docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".exe")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".jar")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".msi")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".rar")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".zip")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".pps")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".ini")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".gif")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".bmp")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".backup")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".bat")
					|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".dll")) {

			} else {
				content = processTXT(is, filepath, length);
			}
			if (content == null) {
				content = "";
			}
			rowdata.put("content", content);
			list.add(rowdata);
			num++;
		}
		return list;
	}

	/**
	 * 解析邮件
	 * 
	 * @param instream
	 * @param rowdata
	 * @return
	 */
	private String processEML(InputStream instream, Map<String, String> rowdata) {
		EMLAnalyzer analyzer = new EMLAnalyzer();
		String content = "";
		try {
			EMLAnalyzer.MessageBean message = analyzer.analyze(instream);
			content = message.getContent();
			Date sendDate = message.getSendDate();// 发送时间
			String subject = message.getSubject();// 标题
			if (subject == null) {
				subject = "";
			}
			List<MailUser> toUsers = message.getToUsers();// 接收人
			List<MailUser> ccUsers = message.getCcUsers();// 抄送人
			List<Attachment> attaList = message.getAttaList();// 附件
			MailUser senduser=message.getSendUser();//发件人
			rowdata.put("subject", subject);
			if (sendDate != null) {
				rowdata.put("senddate", df.format(sendDate));
			} else {
				rowdata.put("senddate", "");
			}
			
			StringBuilder sbUserContent = new StringBuilder();
			if(senduser!=null){
				sbUserContent.append(senduser.getUserName()).append("<").append(senduser.getMailAddr()).append(">");
				rowdata.put("senduser", sbUserContent.toString());
			}
			StringBuilder receive = new StringBuilder();
			/*
			 * 收件人
			 */
			if (toUsers != null) {
				sbUserContent.setLength(0);
				for (MailUser user : toUsers) {
					if (sbUserContent.length() == 0) {
						sbUserContent.append(user.getUserName()).append("<").append(user.getMailAddr()).append(">");
					} else {
						sbUserContent.append(",").append(user.getUserName()).append("<").append(user.getMailAddr())
								.append(">");
					}
				}
				receive.append(sbUserContent.toString());
				rowdata.put("touser", sbUserContent.toString());
			} else {
				rowdata.put("touser", "");
			}
			/*
			 * 抄送人
			 */
			if (ccUsers != null) {
				sbUserContent.setLength(0);
				for (MailUser user : ccUsers) {
					if (sbUserContent.length() == 0) {
						sbUserContent.append(user.getUserName()).append("<").append(user.getMailAddr()).append(">");
					} else {
						sbUserContent.append(",").append(user.getUserName()).append("<").append(user.getMailAddr())
								.append(">");
					}
				}
				if (receive.length() == 0) {
					receive.append(sbUserContent.toString());
				} else {
					receive.append(",").append(sbUserContent.toString());
				}
				rowdata.put("ccuser", sbUserContent.toString());
			} else {
				rowdata.put("ccuser", "");
			}
			rowdata.put("receive", receive.toString());// 收件人跟抄送人

			/*
			 * 附件
			 */
			if (fetchAttachment) {
				StringBuilder sbAttaContent = new StringBuilder();
				if (attaList != null) {
					for (Attachment att : attaList) {
						String filename = att.getFileName();
						InputStream is = att.getIs();
						String attaContent = "";
						if (filename != null && is != null) {
							filename = filename.trim().toLowerCase();
							if (filename.endsWith(".doc")) {
								attaContent = processWORD(is, "");
							} else if (filename.endsWith(".docx")) {
								attaContent = processDOCX(is);
							} else if (filename.endsWith(".ppt")) {
								attaContent = processPPT(is);
							} else if (filename.endsWith(".pptx")) {
								attaContent = processPPTX(is);// 暂时无法解析ppt2007
							} else if (filename.endsWith(".xls")) {
								attaContent = processXLS(is);
							} else if (filename.endsWith(".xlsx")) {
								attaContent = processXLSX(is);
							} else if (filename.endsWith(".pdf")) {
								attaContent = processPDF(is, "");
							} else if (filename.endsWith(".rtf")) {
								attaContent = processRTF(is, "");
							} else if (filename.endsWith(".mht")) {
								attaContent = processMHT(is);
							} else if (filename.endsWith(".eml")) {
								attaContent = processEML(is, new HashMap<String, String>());
							} else if (filename.endsWith(".txt")) {
								attaContent = processTXT(is, "", 1024);
							} else {
								attaContent = "";
							}
						}
						sbAttaContent.append(System.getProperty("line.separator", "\r\n"));
						sbAttaContent.append(" ").append(filename).append(":");
						sbAttaContent.append(System.getProperty("line.separator", "\r\n"));
						sbAttaContent.append(attaContent);
					}
				}
				rowdata.put("attaContent", sbAttaContent.toString());
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return content;
	}

	/**
	 * 解析PPT
	 * 
	 * @param instream
	 */
	private String processPPT(InputStream instream) {
		StringBuffer content = new StringBuffer("");
		SlideShow ppt;
		try {
			ppt = new SlideShow(instream);
			Slide[] slides = ppt.getSlides();
			for (int i = 0; i < slides.length; i++) {
				TextRun[] t = slides[i].getTextRuns();// 为了取得幻灯片的文字内容，建立TextRun
				for (int j = 0; j < t.length; j++) {
					content.append(t[j].getText());// 这里会将文字内容加到content中去
				}
			}
		} catch (IOException e) {

		} finally {
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
				}
			}
		}
		return content.toString();
	}

	/**
	 * 解析ppt2007暂时没有实现
	 * 
	 * @param instream
	 * @return
	 */
	private String processPPTX(InputStream instream) {
		return "";
	}

	// 文件解析
	private String processRTF(InputStream is, String filepath) {
		try {
			RTFExtractor tool = new RTFExtractor();
			return tool.parse(is);
		} catch (Exception e) {
			if (isback)
				new Thread(new BackUnParseFile(is, filepath)).start();
			return "";
		} finally {
			try {
				is.close();// tool中已经关闭
			} catch (IOException ex) {
			}
		}
	}

	// MHT解析
	public String processMHT(InputStream in) {
		Session mailSession;
		if (in == null)
			return null;
		mailSession = Session.getDefaultInstance(System.getProperties(), null);
		MimeMessage msg;
		try {
			msg = new MimeMessage(mailSession, in);
			Object content = msg.getContent();
			if (content instanceof Multipart)
				return handleMultipart((Multipart) content);

			return handlePart(msg);
		} catch (Exception e1) {
			e1.getMessage();
		}

		return null;
	}

	public String handleMultipart(Multipart multipart) throws MessagingException, IOException {
		StringBuffer strb = new StringBuffer();
		int i = 0;
		for (int n = multipart.getCount(); i < n; i++)
			strb.append(handlePart(multipart.getBodyPart(i)));

		return strb.toString();
	}

	public String handlePart(Part part) throws MessagingException, IOException {
		String contentType = part.getContentType();
		String encode = StringUtil.parseCharacterEncoding(contentType);
		InputStreamReader sbis = null;
		StringBuffer strb = new StringBuffer();
		if (encode != null && !"".equals(encode))
			sbis = new InputStreamReader(part.getInputStream(), encode);
		else
			sbis = new InputStreamReader(part.getInputStream());
		BufferedReader reader = new BufferedReader(sbis);
		if (contentType.length() >= 8 && contentType.toLowerCase().substring(0, 8).equals("text/htm")) {
			String line;
			while ((line = reader.readLine()) != null)
				strb.append(line);
			return html2Text(strb.toString());
		} else {
			return null;
		}
	}

	public String html2Text(String inputString) {
		String htmlStr = inputString;
		String textStr = "";
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>";
			Pattern p_script = Pattern.compile(regEx_script, 2);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");
			Pattern p_style = Pattern.compile(regEx_style, 2);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");
			Pattern p_html = Pattern.compile(regEx_html, 2);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println((new StringBuilder("Html2Text: ")).append(e.getMessage()).toString());
		}
		return textStr;
	}

	// Excel 2007格式解析
	private String processXLSX(InputStream is) {
		OPCPackage opc = null;
		try {
			opc = OPCPackage.open(is);
			XSSFWorkbook xwb = new XSSFWorkbook(opc);
			int sheetCount = xwb.getNumberOfSheets();
			/*
			 * 循环sheet
			 */
			for (int i = 0; i < sheetCount; i++) {
				XSSFSheet xSheet = xwb.getSheetAt(i);
				if (xSheet == null) {
					continue;
				}
				int rowNum = xSheet.getLastRowNum();
				/*
				 * 循环行
				 */
				for (int j = 0; j <= rowNum; j++) {
					XSSFRow xRow = xSheet.getRow(j);
					if (xRow == null) {
						continue;
					}
					int cellNum = xRow.getLastCellNum();
					for (int k = 0; k <= cellNum; k++) {
						XSSFCell xCell = xRow.getCell(k);
						if (xCell == null) {
							continue;
						}
						if (xCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
							sbTemp.append(xCell.getBooleanCellValue());
						} else if (xCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							sbTemp.append(xCell.getNumericCellValue());
						} else {
							String value = xCell.getStringCellValue();
							if (value != null) {
								sbTemp.append(value);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (InvalidFormatException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (opc != null) {
				try {
					opc.close();
				} catch (IOException e) {
				}
			}
		}
		String content = sbTemp.toString();
		sbTemp.setLength(0);
		return content;
	}

	/**
	 * 解析word2007
	 * 
	 * @param is
	 * @return
	 */
	private String processDOCX(InputStream is) {
		String content = "";
		OPCPackage opc = null;
		try {
			opc = OPCPackage.open(is);
			POIXMLTextExtractor ex = new XWPFWordExtractor(opc);
			content = ex.getText();
		} catch (XmlException e) {
		} catch (OpenXML4JException e) {
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (opc != null) {
				try {
					opc.close();
				} catch (IOException e) {
				}
			}
		}
		return content;
	}

	// 文本解析（标准的格式）
	private String processTXT(InputStream is, String filepath, long length) {
		String r = "";
		try {

			/*******************************************************************
			 * 修改一个文本只存入一个字段
			 */
			InputStream in = is;
			long size = is.available() > 0 ? is.available() : length;
			PushbackInputStream pin = new PushbackInputStream(is, (int) size);
			String code = get_charset(pin);
			in = pin;
			BufferedReader br = new BufferedReader(new InputStreamReader(in, code));
			StringBuffer sb = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null)
				sb.append(temp).append("\r\n");
			r = sb.toString();
			return r;
		} catch (Exception ex) {
			if (isback)
				new Thread(new BackUnParseFile(is, filepath)).start();
		} finally {
			try {
				is.close();
			} catch (IOException ex1) {
			}
		}
		System.out.println(r);
		return r;
	}

	/**
	 * 获取txt文档的字符集编码
	 * 
	 * @param bis
	 * @return
	 */
	public static String get_charset(PushbackInputStream bis) {
		String encoding = "utf-8";
		java.io.ByteArrayOutputStream bso = new java.io.ByteArrayOutputStream();
		try {
			byte[] buff = new byte[4096];
			int len = 0;
			UniversalDetector det = new UniversalDetector(null);
			while ((len = bis.read(buff)) > 0 && !det.isDone()) {
				bso.write(buff, 0, len);
				det.handleData(buff, 0, len);
			}
			det.dataEnd();
			encoding = det.getDetectedCharset();
			bis.unread(bso.toByteArray());
			bso.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return encoding == null ? "utf-8" : encoding;
	}

	private String processXLS(InputStream is) {
		StringBuffer content = new StringBuffer();

		try {
			Workbook book = Workbook.getWorkbook(is);
			Sheet sheet = book.getSheet(0);
			int rowNum = sheet.getRows();
			int columnNum = sheet.getColumns();
			for (int i = 1; i < rowNum; i++) {
				for (int j = 0; j < columnNum; j++) {
					Cell cell = sheet.getCell(j, i);
					content.append(cell.getContents());
				}
			}
			book.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return content.toString();

	}

	// 解析pdf
	private String processPDF(InputStream is, String filepath) {
		FileInputStream inputStream = null;
		try {
			PDFParserTool tool = new PDFParserTool();
			String text = tool.extract(is);
			String rt = filterText(text);
			float radio = (float) (text.length() - rt.length()) / text.length();
			//		   
			if (radio >= 0.5) {
				return rt;
			}
			return text;
		} catch (Exception e) {
			if (isback)
				new Thread(new BackUnParseFile(is, filepath)).start();
			return "";
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
			}
		}

	}

	private static String processPDF(String file) {
		try {
			Runtime runtime = Runtime.getRuntime();
			String base = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			String path = base.replace("/WEB-INF/classes", "/plugin/xpdf");
			if (path.startsWith("/") && path.indexOf(":") >= 2) {
				path = path.substring(1);
			}
			String cmd = path + "pdftotext -enc GBK   \"" + file + "\" -";
			Process process = runtime.exec(cmd);

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String str = null;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null) {
				sb.append(str).append("\r\n");
			}
			runtime.runFinalization();
			return sb.toString();
		} catch (Exception e) {
			if (isback)
				new Thread(new BackUnParseFile(file)).start();
			return "";
		}

	}

	// 解析word
	public String processWORD(InputStream in, String filepath) {
		ArrayList text = new ArrayList();
		POIFSFileSystem fsys = null;
		StringBuffer sb = new StringBuffer();
		DocumentInputStream din = null;
		try {
			fsys = new POIFSFileSystem(in);
			DocumentEntry headerProps = (DocumentEntry) fsys.getRoot().getEntry("WordDocument");
			din = fsys.createDocumentInputStream("WordDocument");
			byte[] header = new byte[headerProps.getSize()];
			din.read(header);
			din.close();
			int info = LittleEndian.getShort(header, 0xa);
			boolean useTable1 = (info & 0x200) != 0;
			int complexOffset = LittleEndian.getInt(header, 0x1a2);
			String tableName = null;
			if (useTable1) {
				tableName = "1Table";
			} else {
				tableName = "0Table";
			}

			DocumentEntry table = (DocumentEntry) fsys.getRoot().getEntry(tableName);
			byte[] tableStream = new byte[table.getSize()];

			din = fsys.createDocumentInputStream(tableName);

			din.read(tableStream);
			din.close();

			din = null;
			fsys = null;
			table = null;
			headerProps = null;

			int multiple = findText(tableStream, complexOffset, text);

			int size = text.size();
			tableStream = null;

			for (int x = 0; x < size; x++) {
				WordTextPiece nextPiece = (WordTextPiece) text.get(x);
				int start = nextPiece.getStart();
				int length = nextPiece.getLength();
				boolean unicode = nextPiece.usesUnicode();
				String toStr = null;
				if (unicode) {
					toStr = new String(header, start, length * multiple, "UTF-16LE");
				} else {
					toStr = new String(header, start, length, "ISO-8859-1");
				}
				sb.append(toStr);
			}
		} catch (IOException e) {
		} finally {
			if (din != null) {
				din.close();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	private int findText(byte[] tableStream, int complexOffset, ArrayList text) throws IOException {
		int pos = complexOffset;
		int multiple = 2;
		while (tableStream[pos] == 1) {
			pos++;
			int skip = LittleEndian.getShort(tableStream, pos);
			pos += 2 + skip;
		}
		if (tableStream[pos] != 2) {
			throw new IOException("corrupted Word file");
		} else {
			// parse out the text pieces
			int pieceTableSize = LittleEndian.getInt(tableStream, ++pos);
			pos += 4;
			int pieces = (pieceTableSize - 4) / 12;
			for (int x = 0; x < pieces; x++) {
				int filePos = LittleEndian.getInt(tableStream, pos + ((pieces + 1) * 4) + (x * 8) + 2);
				boolean unicode = false;
				if ((filePos & 0x40000000) == 0) {
					unicode = true;
				} else {
					unicode = false;
					multiple = 1;
					filePos &= ~(0x40000000); // gives me FC in doc stream
					filePos /= 2;
				}
				int totLength = LittleEndian.getInt(tableStream, pos + (x + 1) * 4)
						- LittleEndian.getInt(tableStream, pos + (x * 4));

				WordTextPiece piece = new WordTextPiece(filePos, totLength, unicode);
				text.add(piece);

			}

		}
		return multiple;
	}

	public List<Map<String, String>> getData(Map dataMap) {

		if (dataMap.get(NetDirectoryFethListTool.SIGN_FILE) != null
				&& dataMap.get(NetDirectoryFethListTool.SIGN_FILE).toString().length() > 0) {
			String url = dataMap.get(NetDirectoryFethListTool.SIGN_FILE).toString();

			try {
				// System.out.println("解析："+url);
				File file = new File(url);
				InputStream localis = new FileInputStream(url);
				// InetAddress address = InetAddress.getLocalHost();
				// String ip = address.getHostAddress();
				String ip = GetLocalIp.getIp();
				String localurl = ip;
				return getDateFromFile(localis, url, file.getName(), true, file.lastModified(), localurl, file.length());
			} catch (IOException e) {
			}
		} else if (dataMap.get(NetDirectoryFethListTool.NET_FILE) != null
				&& dataMap.get(NetDirectoryFethListTool.NET_FILE).toString().length() > 0) {
			String url = dataMap.get(NetDirectoryFethListTool.NET_FILE).toString();
			try {
				// System.out.println("解析网络："+url);
				SmbFile smbFile = new SmbFile(url);

				String ip = smbFile.getServer();
				InetAddress address = InetAddress.getByName(ip);
				String neturl = ip;
				long lastModify = smbFile.getLastModified();
				SmbFileInputStream remoteis = new SmbFileInputStream(smbFile);

				// ThreadGroup group = new ThreadGroup("");
				return getDateFromFile(remoteis, url, smbFile.getName(), false, lastModify, neturl, smbFile.length());
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("连接出错，请检查网络连接");
			}
		}

		return null;
	}

	// 检查文件是否已经被索引
	private boolean index(String filepath, String lastModified) {
		try {
			String md5Path = MD5.encoding(filepath + lastModified);
			boolean isOk = webDbAdminTool.addContents(md5Path);
			/**** 去重数据同步 ***/
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (isOk && ImDistributedTool.isRuning && ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0) {
				for (Synchro s : synchro) {// 遍历每个节点
					try {
						((ImInterface) RPC.getProxy(ImInterface.class, ImDistributedTool.getNode(s.getIpaddress()),
								SearchContext.synChroTiomeOut)).addContent(ArraysObjectTool.ObjectToArrays(md5Path));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			/**** 去重数据同步 ***/
			if (isOk) {
				return false;
			}
		} catch (Exception ex) {
			return true;
		}
		return true;
	}

	public List<Map<String, String>> getFileByDirectory(FileDirectory f) {
		if (fileurl.size() > 0) {
			fileurl.clear();
		}
		if (f.getDirtype().equalsIgnoreCase("local")) {
			File file = new File(f.getPath());
			if (f.getFileTypes() != null) {
				getFileFromD(file, f);
			} else {
				// System.out.println("为选择采集文件类型，终止");
			}
		} else if (f.getDirtype().equalsIgnoreCase("remote")) {
			String url = "smb://" + f.getUName() + ":" + f.getUPwd() + "@" + f.getRemoteIPaddress()
					+ f.getRemoteFiledir();
			if (f.getFileTypes() != null) {
				getFileUrl(f.getFileTypes(), f.getIslayers(), url);
			} else {
				// System.out.println("未选择采集文件类型，终止！");
			}
		}
		return fileurl;
	}

	public List<Map<String, String>> getPdfurl() {
		return pdfurl;
	}

	/**
	 * 取一个文件的内容
	 * 
	 * @param is
	 * @param docSource
	 * @param title
	 * @param islocal
	 * @param lastModify
	 * @param serverurl
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public String getDateByPath(String docSource) throws Exception {
		String content = "";

		String filepath = docSource;
		docSource = docSource.toLowerCase();
		String doctype = docSource.substring(docSource.lastIndexOf(".") + 1);
		if (doctype == null) {
			doctype = "";
		}
		if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("doc"))) {
			content = processWORD(new FileInputStream(docSource), filepath);
		} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("pdf"))) {
			content = processPDF(filepath);
		} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("rtf"))) {
			content = processRTF(new FileInputStream(docSource), filepath);
		} else if ((docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase("xls"))) {
			content = processXLS(new FileInputStream(docSource));
		} else if (docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".exe")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".jar")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".msi")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".rar")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".zip")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".ppt")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".pps")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".ini")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".gif")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".bmp")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".backup")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".bat")
				|| docSource.substring(docSource.lastIndexOf(".") + 1).equalsIgnoreCase(".dll")) {

		} else {
			File f = new File(filepath);
			content = processTXT(new FileInputStream(docSource), filepath, f.length());
		}

		return content;
	}

	public String filterText(String text) {
		char[] temps = text.toCharArray();
		StringBuffer sb = new StringBuffer();
		// boolean pre = true;
		// boolean now = false;
		// boolean next = false;
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < temps.length; i++) {
			int c = (int) temps[i];
			// int d = (int)temps[i++];
			// int e = (int)temps[i++];
			if ((c >= 28 && c <= 90) || (c >= 97 && c <= 122) || (c >= 0x8140 && c <= 0xfefe) || (c >= 8 && c <= 13)) {
				// sb.append((char)c);
				temp.append((char) c);
			} else {
				if (temp.length() > wordlength) {
					sb.append(temp.toString());
				}
				temp.setLength(0);
			}
		}
		return sb.toString();
	}
}

// 这个代码还不严谨，我测试的时候当存在有共享打印机大目录时就会抛异常，我就在listFiles(dirFilter)方法上加了捕捉异常但没有处理，这样如果抛了异常，还能运行，只是共享打印机这个目录就找不到了.

