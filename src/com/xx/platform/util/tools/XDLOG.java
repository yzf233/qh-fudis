package com.xx.platform.util.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XDLOG {
	private static Object ob=new Object();
	private static  long filesize = 1024*1024*5;
    public static void out(String  comment) {
    	synchronized(ob)
        {
    	boolean append = true;
     		File file = new File("..\\logs\\xdmessage.log");
     		if(file.length()>filesize){
     			backFile(file);
     			append = false;
     		}
     		try { 
    			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,append)));
    			bw.write(comment);
    			bw.newLine();
    			bw.flush();
    			bw.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
    }
    private static void backFile(File lf) {
		String abpath = lf.getAbsolutePath();
		String basePath = abpath.substring(0, abpath.lastIndexOf(File.separator));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒日志备份");
		lf.renameTo(new File(basePath+File.separator + dateFormat.format(new Date())+ ".txt"));
	}
}