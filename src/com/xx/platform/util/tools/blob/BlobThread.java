package com.xx.platform.util.tools.blob;

import java.io.InputStream;
import java.util.Map;
 
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class BlobThread extends Thread{
 
    private Map<String,String>rowdata;
    private String key;
    private InputStream data;
    private String filename;
    private long startTime=0;
    
    public BlobThread(ThreadGroup group,String name,Map<String, String> rowdata,String key,InputStream data){
    	this(group,name,rowdata,key,data,"");
     
    }
    

    public BlobThread(ThreadGroup group,String name,Map<String, String> rowdata,String key,InputStream data,String filename){
        super(group,name);
        this.rowdata = rowdata;
        this.key = key;
        this.data = data;
        this.filename = filename;
    }


    public void run(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex1) {
        }
//        System.out.println("创建Blob采集线程;"+this.getName());
        this.startTime = System.currentTimeMillis();
        if(data!=null){
//            if(data instanceof Blob){
                BlobTool bt = null;
                try {
                    bt = new BlobTool(data, filename);
                  
//              BlobTool bt = new BlobTool(new java.io.ByteArrayInputStream(clob.getBytes(0,(int)clob.length())));
                    long start = System.currentTimeMillis();
                    
                    String text = bt.extract();
                    
                    String dataValue;
                    StringBuffer strb = new StringBuffer();
                    if (text != null) {
                            if ((dataValue = ((String) rowdata.get(key))) != null) {
                             
                                rowdata.remove(key);
                                strb.setLength(0);
                                strb.append(dataValue).append(" ").append(text);
                                rowdata.put(key, strb.toString());
                                strb.setLength(0);
                            } else {
                            	 
                                rowdata.put(key, text);
                            }
                        }

//                    System.out.println(this.getName() + ";" + text == null ?
//                                       "null" :
//                                       text.substring(0,
//                            20 > text.length() ? text.length() : 20));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }finally{
                    try{
                        data.close();
                    }catch(Exception e){}
                }

            }
        }
//    }


	public long getStartTime() {
		return startTime;
	}


	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

 
}
