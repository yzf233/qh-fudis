package com.xx.platform.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.xx.platform.util.tools.BackUnParseFile;
import com.xx.platform.util.tools.blob.PDFParserTool;

import jcifs.smb.SmbFile;

public class ParseTest {

	/**
	 * @param args
	 */
	private static BufferedWriter bw = null;
//	static{
//		try{
//		bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("F:\\parse.txt")));
//		}catch (Exception e) {
//			e.printStackTrace();// : handle exception
//		}
//	}
	public static void main(String[] args) {
//		List<String> filelist = new ArrayList<String>();
		try{ 
//			getFileList(filelist);
//		String file = "F:\\测试文件\\018.PDF";
////		System.out.println(filelist.size());
		 long  start = System.currentTimeMillis();
		 QueryParser qp = new QueryParser("e,f,g",
                 new org.apache.lucene.analysis.standard.StandardAnalyzer());
		 Query q = qp.parse("\"askld asfd\"~3 AND b:aslkf OR c:a [1 TO 2] -中国*");
		 System.out.println(q);
//		 for (Iterator it = filelist.iterator(); it.hasNext();) {
////			 InputStream is = new FileInputStream(it.next().toString());
////			 String result = processTXT(is);
//			 String file = it.next().toString();
//			 System.out.println(file);
//			 System.out.println(processPDF(new FileInputStream(file)));
//		}
//		 String filepath = "F:\\testXLS";
//		System.out.println(processPDF(filepath));
//		 System.out.println(ParseTest.class.getResource("/").getPath());
//		 System.out.println(ClassLoader.getSystemResource("").getPath());
//		 System.out.println(
//		 ParseTest.class.getProtectionDomain().getCodeSource().getLocation().getPath());
//		 System.out.println(new ParseTest().getPath());
//		 File[] files = new File(filepath).listFiles();
//		 for (int i = 0; i < files.length; i++) {
//			if(files[i].getName().endsWith(".xls")){
//				 System.out.println(processXLS(new FileInputStream(files[i].getAbsoluteFile()), files[i].getAbsolutePath()));
//			}
//		}
//		
//		 
//		String url = "smb://administrator:123456@192.168.1.2/a/";
//		SmbFile file = new SmbFile(url);
//		System.out.println(file.getDfsPath());
//		System.out.println(file.getDfsPath());
//		SmbFile[] files = file.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			System.out.println(files[i].getDfsPath());
//		}
		 long end = System.currentTimeMillis();
		 System.out.println(end-start);
		}catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
	}

	private static String processPDF(String file){
		try{
			Runtime runtime = Runtime.getRuntime();
			String cmd = "E:/workspace/insitese/web/xpdf/pdftotext -enc GBK   F:\\测试文件\\pdf\\011.PDF  -";
			System.out.println(cmd);
			Process process  = runtime.exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))	;
			String str = null;
			StringBuffer sb = new StringBuffer();
			while((str = br.readLine())!=null){
				System.out.println(str);
				sb.append(str);
			}
//			System.out.println(sb.toString());
			return sb.toString();
		}catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
		return null;
	}
	private static void getFileList(List<String> filelist) {
		String file = "F:\\测试文件\\pdf";
		File file1 = new File(file);
		File [] files = file1.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].getAbsolutePath().toUpperCase().endsWith("PDF"))
			filelist.add(files[i].getAbsolutePath());
		}
	}

	// 文本解析（标准的格式）
	private static  String processTXT(InputStream is) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// Map<String, String> rowdata = new HashMap<String, String>();
		String r = "";
		try {

			/*******************************************************************
			 * 修改一个文本只存入一个字段
			 */
			InputStream in = is;
			PushbackInputStream pin = new PushbackInputStream(is, 10);
			byte[] bb = new byte[2];

			int len = pin.read(bb, 0, 2);
			int c = bb[0] & 0xff;
			int d = bb[1] & 0xff;
			int p = (c << 8) + d;
			String code = null;
			boolean isSplit = false;
			switch (p) {
			case 0xefbb:
				isSplit = true;
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				isSplit = true;
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
			}
			pin.unread(bb, 0, len);
			in = pin;
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					code));
			StringBuffer sb = new StringBuffer();
			String temp = null;
			while ((temp = br.readLine()) != null)
				sb.append(temp);
			if (isSplit) {
				r = sb.toString().substring(1);
			} else {
				r = sb.toString();
			}
			return r;
		} catch (Exception ex) {
			
		} finally {
			try {
				is.close();
			} catch (IOException ex1) {
			}
		}

		return r;
	}
	
	private static  String processTXT1(InputStream is) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		// Map<String, String> rowdata = new HashMap<String, String>();
		String r = "";
		try {

			/*******************************************************************
			 * 修改一个文本只存入一个字段
			 */
			InputStream in = is;
			PushbackInputStream pin = new PushbackInputStream(is, 10);
			byte[] bb = new byte[2];

			int len = pin.read(bb, 0, 2);
			int c = bb[0] & 0xff;
			int d = bb[1] & 0xff;
			int p = (c << 8) + d;
			String code = null;
			boolean isSplit = false;
			switch (p) {
			case 0xefbb:
				isSplit = true;
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				isSplit = true;
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
			}
			pin.unread(bb, 0, len);
			in = pin;
			BufferedInputStream bis = new BufferedInputStream(in);
			byte [] bb1= new byte[1024];
			int len1 = 0;
			StringBuffer sb = new StringBuffer();
			while ((len = bis.read(bb1))!=-1)
				sb.append(new String(bb1,code));
			if (isSplit) {
				r = sb.toString().substring(1);
			} else {
				r = sb.toString();
			}
			return r;
		} catch (Exception ex) {
			
		} finally {
			try {
				is.close();
			} catch (IOException ex1) {
			}
		}

		return r;
	}
	private static String processPDF(InputStream is) {
		FileInputStream inputStream = null;
		try {
			PDFParserTool tool = new PDFParserTool();
			return tool.extract(is);
		} catch (Exception e) {
			// System.out.println("extractor " + file.getAbsolutePath()
			// + "faild![" + e.getMessage() + "]");
			return "";
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
			}
		}

	}
	public static void log(String msg){
		try{
			
			bw.write(msg);
			bw.newLine();
			bw.flush();
		}catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
	}
	
	private String getPath(){
		return getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	
	private static String processXLS(InputStream is,String filepath) {
		String r = "";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte bb[] = new byte[512];
			int len = 0;
			while ((len = is.read(bb)) > 0) {
				baos.write(bb, 0, 512);
				baos.flush();
			}
		
			InputStream bis = new ByteArrayInputStream(baos.toByteArray(), 0,
					baos.size());

			HSSFWorkbook wb = new HSSFWorkbook(bis);

			if (wb == null) {
				return r;
			}

			HSSFSheet sheet;
			HSSFRow row;
			HSSFCell cell;
			int sNum = 0;
			int rNum = 0;
			int cNum = 0;

			sNum = wb.getNumberOfSheets();
			StringBuffer content = new StringBuffer();
			for (int i = 0; i < sNum; i++) {
				if ((sheet = wb.getSheetAt(i)) == null) {
					continue;
				}
				String[] key = null; // 对应field
				rNum = sheet.getLastRowNum();
				for (int j = 0; j <= rNum; j++) {
					if ((row = sheet.getRow(j)) == null) {
						continue;
					}
					cNum = row.getLastCellNum();
					String value = "";

					for (int k = 0; k < cNum; k++) {
						if ((cell = row.getCell((short) k)) != null) {
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
								content.append(cell.getStringCellValue());
							} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
								content.append(cell.getNumericCellValue());
							}
						}
					}

				}
			}

			r = content.toString();
		} catch (Exception e) {
			return r;
		}

		return r;

	}
}
