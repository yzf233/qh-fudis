package com.xx.platform.util.tools;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BackUnParseFile extends Thread {

	private InputStream is;
	private String docSource;
	
	public BackUnParseFile(InputStream is,String docSource) {
		this.is = is;
		this.docSource = docSource;
	}
	public BackUnParseFile(String filepath){
		try {
			this.docSource = filepath;
			this.is = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
		}
	}
	
	@Override
	public void run() {
		 try{
//		System.out.println("开始备份文件..........."+docSource);
		String filename = docSource.substring(docSource.lastIndexOf("."));
		
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath().replace(
				"/WEB-INF/classes", "/unParseFile");
//		System.out.println(cpath+"------");
		if(path.startsWith("/")&&path.indexOf(":")>=2){
			path = path.substring(1);
		}
		if(!path.endsWith("/")){
			path = path+"/";
		}
//		System.out.println(path+System.currentTimeMillis()+filename);
//		System.out.println(is.available());
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path+System.currentTimeMillis()+filename));
		byte [] bb = new byte[512];
		int len = -1;
		while((len=is.read(bb))!=-1){
			bos.write(bb, 0, len);
			bos.flush();
		}
		bos.close();
		is.close();
		 }catch (Exception e) {
			 if(is!=null){
				 try {
					is.close();
				} catch (IOException e1) {
				}
			 }
		 }
	}
}
