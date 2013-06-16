package com.xx.platform.web.interceptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordException {

	public static void log(String message){
		File file = new File("../logs/error.txt");
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
			bw.write(sdf.format(new Date()));
			bw.newLine();
			bw.write(message);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (Exception e) {
		 
		}
		
	}
}
