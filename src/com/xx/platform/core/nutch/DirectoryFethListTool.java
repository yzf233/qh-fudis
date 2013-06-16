package com.xx.platform.core.nutch;

import java.io.*;
import java.util.*;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.domain.model.database.FileDirectory;
import com.xx.platform.util.tools.MD5;
import com.xx.platform.util.tools.blob.PDFParserTool;
import com.xx.platform.util.tools.ms.RTFExtractor;
import com.xx.platform.util.tools.ms.WordExtractor;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class DirectoryFethListTool {
//    public static String[] fields = new String[]{"content","name"};
    private static Object obj = new Object(); //同步锁
    public static String[] titleFields = new String[4];
    
    public static final String path = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace("/WEB-INF/classes", "/upload");
   
    
    public static String[] fields ;
    static{
        try{
            List<IndexFieldImpl> fieldsSet = SearchContext.getDataHandler().findAllByIObjectCType(IndexFieldImpl.class);
            List<String> fieldList = new ArrayList<String>();
            for (IndexFieldImpl field : fieldsSet) {
                if(field.getCode()!=null && field.getCode().equals("a")){
                    titleFields[0] = field.getId();
                }else if(field.getCode()!=null && field.getCode().equals("b")){
                    titleFields[1] = field.getId();
                }else if(field.getCode()!=null && field.getCode().equals("c")){
                    titleFields[2] = field.getId();
                }else if(field.getCode()!=null && field.getCode().equals("d")){
                    titleFields[3] = field.getId();
                }else if(field.getCode()!=null && field.getCode().length()>1){
                    fieldList.add(field.getId());
                    
                }
            }
            fields = new String[fieldList.size()];
           fieldList.toArray(fields);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("init fields failed["+e.getMessage()+"]");
            fields = null;
        }
    }
    private WebDbAdminTool webDbAdminTool = new WebDbAdminTool();
    public static final int count = 50000;
    public int num = 0; //已取得的数据数量
    public static String url = "http://www.www.com";
    public static String SIGN_FILE="_XD-TECH_QUHUAN_FILE_SIGN_12345DLKFJ234_!@#&%^"; //文件标志,希望不要和某个字段内容重复
    private List<FileDirectory> directoryList;  //需要索引的目录
    private List<Map<String,Object>> dataList; //最终取到的数据
    private List<Map<String,String>> dataList1; //最终取到的数据
//    private List<File> fileList;                //目录下所有的文件

    public DirectoryFethListTool(){
        directoryList = SearchContext.getDataHandler().findAllByCriteria(DetachedCriteria.forClass(FileDirectory.class));
        //移除不是本机目录的本地目录
        List<FileDirectory> remove=new ArrayList<FileDirectory>();
        String ip=SearchContext.getXdtechsite().getLocalip();
        for(FileDirectory file:directoryList){
        	if("local".equals(file.getDirtype())){
        		if(!ip.equals(file.getLocalip())){
        			remove.add(file);
        		}
        	}
        }
        directoryList.removeAll(remove);
        FileDirectory fd = new FileDirectory(); //上传目录
        fd.setPath(path);
        //System.out.println(path);
        directoryList.add(fd);
        dataList1 = new ArrayList<Map<String,String>>();
//        fileList = new ArrayList<File>();
//        System.out.println("DirectoryFethListTool");
    }
/*
    public synchronized List<Map<String,Object>> getDbList(){
        FileDirectory f = null;
        while(directoryList.size()>0){
            f = directoryList.remove(0);
            File file = new File(f.getPath());
            getFileFromD(file);
        }
        return dataList;
    }
    private void getFileFromD(File file){
        if(file.isFile()){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put(SIGN_FILE,file);
            if (map != null)
                dataList.add(map);
        }else if (file.isDirectory()){
            File[] childs = file.listFiles();
            if(childs!=null){
                for(File f:childs)
                getFileFromD(f);
            }
        }
    }



   //入口方法_文件已经被解析
    public List<Map<String, String>> getDbList() {
        FileDirectory f = null;
        while (directoryList.size() > 0 && num < count) {
            f = directoryList.remove(0);
            File file = new File(f.getPath());
//            System.out.println(file.getAbsolutePath());
            getFileFromD_new(file);
        }
        return dataList1;
    }
*/
//入口方法_返回文件路径
public List<Map<String, String>> getDbList() {
        FileDirectory f = null;
        while (directoryList.size() > 0 ) {
            f = directoryList.remove(0);
            File file = new File(f.getPath());
            getFileFromD(file,f);
        }
        return dataList1;
    }
/*
    private void getFileFromD(File file){
       if(file.isFile()){
           Map<String,String> map = new HashMap<String,String>();
           map.put(SIGN_FILE,file.getAbsolutePath());
           if (map != null)
               dataList1.add(map);
       }else if (file.isDirectory()){
           File[] childs = file.listFiles();
           if(childs!=null){
               for(File f:childs)
               getFileFromD(f);
           }
       }
   }
   */
    private void getFileFromD(File file,FileDirectory fd){
    	String islayers=fd.getIslayers();
        if(file.isFile()){
        	Map<String,String> map = new HashMap<String,String>();
        	String file_kind=(file.getAbsolutePath()).substring((file.getAbsolutePath().lastIndexOf("."))+1);
//        	if(fd.getIsexcel().equals("true")&&file_kind.equals("xls"))
//        	{
//        		map.put(SIGN_FILE,file.getAbsolutePath());
//        	}
//         	if(fd.getIspdf().equals("true")&&file_kind.equals("pdf"))
//        	{
//        		map.put(SIGN_FILE,file.getAbsolutePath());
//        	}
//         	if(fd.getIsword().equals("true")&&file_kind.equals("doc"))
//        	{
//        		map.put(SIGN_FILE,file.getAbsolutePath());
//        	} 	
//         	if(fd.getIstxt().equals("true")&&file_kind.equals("txt"))
//        	{
//        		map.put(SIGN_FILE,file.getAbsolutePath());
//        	} 	
//         	if(fd.getIsrtf().equals("true")&&file_kind.equals("rtf"))
//        	{
//        		map.put(SIGN_FILE,file.getAbsolutePath());
//        	}
			if (fd.getFileTypes().indexOf(file_kind) > -1
					&& fd.getDirtype().equals("local")) {
				map.put(SIGN_FILE, file.getAbsolutePath());
			}
            if (map != null)
                dataList1.add(map);
        }else if (file.isDirectory()&&islayers!=null&&!islayers.equals("end")){
            File[] childs = file.listFiles();
            if(childs!=null){
              if(islayers!=null&&islayers.equals("false"))
            	fd.setIslayers("end");
            	for(File f:childs)
                getFileFromD(f,fd);
            }
        }
    }

    /**
     * 从目录取文件
     *
     * @param file File
     */
    private void getFileFromD_new(File file) {
        if(file.isFile()){
            List<Map<String,String>> list = getDateFromFile(file);
            if (list!=null && list.size()>0 )
                dataList1.addAll(list);
        }else if (file.isDirectory()){
            File[] childs = file.listFiles();
            if(childs!=null){
                for(File f:childs){
                    if(num<count)
                        getFileFromD_new(f);
                    else break;
                }
            }
        }

    }

    /**
     * 解析文件
     *
     * @param file File
     * @return List
     */
    private List<Map<String,String>> getDateFromFile(File file) {
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        if(!index(file)){
            int i = file.getName().lastIndexOf(".");
            String type = "";
            Map<String, String> rowdata;
            if(i>0)
                type = file.getName().substring(i);
            if(type.equals(".doc")){
                rowdata = processWORD(file);
                if(rowdata!=null){
                    list.add(rowdata);
                    num++;
                }
            }else if(type.equals(".pdf")){
                rowdata = processPDF(file);
                if(rowdata!=null){
                    list.add(rowdata);
                    num++;
                }
            }else if(type.equals(".rtf")){
                rowdata = processRTF(file);
                if(rowdata!=null){
                    list.add(rowdata);
                    num++;
                }
            }else if(type.equalsIgnoreCase(".xls")){
                rowdata =processXLS(file);
                System.out.println("采集前=="+file.getName());
                if(rowdata!=null){
                  list.add(rowdata);
                  num++;
                  System.out.println("采集后=="+file.getName());
              }
            }else if(type.equalsIgnoreCase(".exe")||type.equalsIgnoreCase(".jar") || type.equalsIgnoreCase(".msi")|| type.equalsIgnoreCase(".rar")|| type.equalsIgnoreCase(".zip")|| type.equalsIgnoreCase(".ppt")|| type.equalsIgnoreCase(".pps")|| type.equalsIgnoreCase(".ini")|| type.equalsIgnoreCase(".gif")|| type.equalsIgnoreCase(".bmp")|| type.equalsIgnoreCase(".backup")|| type.equalsIgnoreCase(".bat")|| type.equalsIgnoreCase(".dll")){

            }else{
                rowdata = processTXT(file);
                if(rowdata!=null){
                   list.add(rowdata);
                   num++;
               }
            }

        }
        return list;
    }

    //rtf 解析
    private Map processRTF(File file) {
        FileInputStream inputStream = null ;
        try{
             Map<String, String> rowdata = new HashMap<String, String>();
             rowdata.put("docType", "file");
             rowdata.put("docSource",file.getAbsolutePath() != null ? file.getAbsolutePath() :"");
             rowdata.put("title", file.getName());
             RTFExtractor tool = new RTFExtractor();
             rowdata.put("content", tool.parse(inputStream = new FileInputStream(file)));
             return rowdata;
         }catch(Exception e){
             System.out.println("extractor "+file.getAbsolutePath() +"faild!["+e.getMessage()+"]");
             return null;
         }finally{
          try {
            inputStream.close();//tool中已经关闭
          }
          catch (IOException ex) {
          }
         }
    }

    //文本解析（标准的格式）
     private Map processTXT(File file) {
         List<Map<String, String>> list = new ArrayList<Map<String, String>>();
     BufferedReader  in = null ;
        Map<String, String> rowdata = new HashMap<String, String>();
     try{
         BufferedInputStream bin = new BufferedInputStream(
					new FileInputStream(file));
			int p = (bin.read() << 8) + bin.read();
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
			bin.close();
			in =new BufferedReader(new InputStreamReader(new FileInputStream(file),code));
         /***
          * 修改一个文本只存入一个字段
          */

         rowdata.put("docType", "file");
         rowdata.put("docSource",file.getAbsolutePath() != null ? file.getAbsolutePath() :"");
         rowdata.put("title", file.getName());
         StringBuffer sb = new StringBuffer();
         String temp   =   null;
         while((temp =in.readLine())!= null)
              sb.append(temp);
         if(isSplit){
        	 rowdata.put("content", sb.toString().substring(1));
         }else{
         rowdata.put("content", sb.toString());
         }
         return rowdata;
     } catch (Exception ex) {
         ex.printStackTrace();
     }finally{
       try {
         in.close();
       }
       catch (IOException ex1) {
       }
     }

     return rowdata;

    }

    private Map<String, String> processXLS(File file) {
     Map<String, String> rowdata = new HashMap<String, String>();
     try {
         POIFSFileSystem poifs = new POIFSFileSystem(new java.io.
                 FileInputStream(file));
         HSSFWorkbook wb = new HSSFWorkbook(poifs, true);

         if (wb == null) {
             return null;
         }

         String fileName = file.getName();
         String filePath = file.getAbsolutePath()==null?"":file.getAbsolutePath();
         HSSFSheet sheet;
         HSSFRow row;
         HSSFCell cell;
         int sNum = 0;
         int rNum = 0;
         int cNum = 0;

         sNum = wb.getNumberOfSheets();
         rowdata.put("docType", "file");
         rowdata.put("docSource", filePath);
         rowdata.put("title", fileName);
         StringBuffer content = new StringBuffer();
         for (int i = 0; i < sNum; i++) {
             if ((sheet = wb.getSheetAt(i)) == null) {
                 continue;
             }
             String[] key = null; //对应field
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
                             value = cell.getStringCellValue();
                         } else if (cell.getCellType() ==
                                    HSSFCell.CELL_TYPE_NUMERIC) {
                             Double d = new Double(cell.getNumericCellValue());
                             value = d.toString();
                         }else{
                             value = "";
                         }
                        content.append(value);

                     }
                 }

             }
         }
         System.out.println(content.toString());
         rowdata.put("content", content.toString());
      }catch(Exception e){
          System.out.println("extractor "+file.getAbsolutePath() +"faild!["+e.getMessage()+"]");
          return rowdata;
      }

     return rowdata;
    }

    //解析pdf
    private Map<String, String> processPDF(File file) {
      FileInputStream inputStream = null ;
        try{
             Map<String, String> rowdata = new HashMap<String, String>();
             rowdata.put("docType", "file");
             rowdata.put("docSource",file.getAbsolutePath() != null ? file.getAbsolutePath() :"");
             rowdata.put("title", file.getName());
             PDFParserTool tool = new PDFParserTool();
             rowdata.put("content", tool.extract(inputStream = new FileInputStream(file)));
             return rowdata;
         }catch(Exception e){
             System.out.println("extractor "+file.getAbsolutePath() +"faild!["+e.getMessage()+"]");
             return null;
         }finally{
          try {
            inputStream.close();
          }
          catch (IOException ex) {
          }
         }

    }
    //解析word
    public Map<String, String> processWORD(File file) {
      FileInputStream inputStream = null ;
        try{
            Map<String, String> rowdata = new HashMap<String, String>();
            rowdata.put("docType", "file");
            
            rowdata.put("lastModified",((Long)file.lastModified()).toString());
            rowdata.put("docSource",file.getAbsolutePath() != null ? file.getAbsolutePath() :"");
            rowdata.put("title", file.getName());
            WordExtractor extractor = new WordExtractor(inputStream = new FileInputStream(file));
            rowdata.put("content", extractor.getText());
            return rowdata;
        }catch(Exception e){
            System.out.println("extractor "+file.getAbsolutePath() +" faild!["+e.getMessage()+"]");
            return null;
        }finally{
          try {
            inputStream.close();
          }
          catch (IOException ex) {
          }
        }
    }

    //检查文件是否已经被索引
    private boolean index(File file) {
        try {
            if (webDbAdminTool.addContents(MD5.encoding(file.getAbsolutePath()+file.lastModified())))
                    return false;
        } catch (Exception ex) {
            return true;
        }
        return true;
    }

    /*
    private void getDateFromFile(File file){
        Map<String, String> map = null;
        if(file.isFile()){
            map = getData(file);
            if (map != null)
                dataList.add(map);
        }else if (file.isDirectory()){
            File[] childs = file.listFiles();
            for(File f:childs)
                getDateFromFile(f);
        }
    }

    public static Map<String, String> getData(File file) {
        Map<String, String> rowdata = new HashMap<String, String>();
        rowdata.put("docType", "file");
        rowdata.put("docSource", file.getPath()!=null?file.getPath():"");
        String filename = file.getName();
        rowdata.put("title", filename);
        //System.out.println("fileName:"+filename +" path:"+file.getPath()!=null?file.getPath():"");
        BlobTool bt = null;
        try {
            bt = new BlobTool(new java.io.FileInputStream(file.getPath()),file.getName());
        } catch (FileNotFoundException ex) {
            return null;
        }
        String content = bt.extract();
        rowdata.put("content", content);
        return rowdata;
     }
     /*
    private Map<String, String> getData1(File file){
       String filename = file.getName();
        String filetype = filename.substring(filename.lastIndexOf(".")+1,filename.length());
        if(filetype.equals("pdf")){
            return parse(file,"application/pdf");
        }else if(filetype.equals("doc")){
            return parse(file,"application/msword");
        }else{
            //非pdf和doc 不处理
            return null;
        }
    }

    private Map<String, String> parse(File file, String contentType) {
         Map<String, String> rowdata = new HashMap<String, String> ();
        Parser parser = null;
        Parse parse = null;
        ParseStatus status = null;
        try {
            parser = ParserFactory.getParser(contentType, file.getName());
            parse = parser.getParse(getContent(file,contentType));
            status = parse.getData().getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            status = new ParseStatus(e);
        }
        if (status.isSuccess()) {
            rowdata.put("name",file.getName());
            rowdata.put("content",parse.getText());
            rowdata.put("title",parse.getData().getTitle());
         } else {
             System.out.println("解析文件["+file.getAbsolutePath()+"]发生错误！--"+status.toString());
             return null;
         }
         return rowdata;
    }

    ///从文件构造Content
    private Content getContent(File file,String contentType){
        return new Content(url,url,getFileContent(file),contentType,new Properties());
    }

    ///file是文件
    private byte[] getFileContent(File file) {
        byte[] bytes = new byte[(int) file.length()];
        DataInputStream in = null;
        FileInputStream fin = null;
        try {

            in = new DataInputStream(fin = new FileInputStream(file));
        } catch (FileNotFoundException ex) {
        }
        try {
            in.readFully(bytes);
            if (fin != null)
                fin.close();
            if (in != null)
                in.close();

        } catch (IOException ex1) {
            ex1.printStackTrace();
        }


        return bytes;

    }*/
     public static void main(String[] args) throws Exception{
         DirectoryFethListTool tool = new DirectoryFethListTool();
         tool.processXLS(new File("F:\\新建文件夹\\问题\\流程测试－－胡俊.xls"));
         System.out.println(tool.getDbList());
     }

    public List<Map<String, String>> getData(String filePath) {
        synchronized (obj) {
            if (SearchContext.getXdtechsite().getSudis())
                return null;
            else
                return getDateFromFile(new File(filePath));
        }
    }
}
