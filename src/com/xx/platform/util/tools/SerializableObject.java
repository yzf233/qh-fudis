package com.xx.platform.util.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SerializableObject{
	private String rootPath;
	private String path;//文件存放路径
	/**
	 * 构造方法
	 * @param rootPath：根路径
	 * @param nodeId：节点ID
	 */
	public SerializableObject(String rootPath,String nextPath) {
		if(nextPath==null||nextPath.trim().length()==0){
			this.rootPath = rootPath;
			path=rootPath;
		}else{
			this.rootPath = rootPath;
			StringBuilder sbPath=new StringBuilder();
			sbPath.append(rootPath).append("/").append(nextPath);
			path=sbPath.toString();
		}
	}
	public SerializableObject() {
	}
	/**
	 * 写一个对象
	 * 
	 * @param 
	 * @param suffix
	 * @throws IOException
	 */
	private void writeFile(Object bean, File outFile)
			throws IOException {
		File parent=outFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
//		outFile.createNewFile();
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new BufferedOutputStream(
					new FileOutputStream(outFile, true)));
			out.writeObject(bean);
		}catch (Exception e) {
			System.err.println(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
/**
 * 读对象
 * @param file
 * @return
 * @throws IOException
 */
	public Object readObject(File file) throws IOException {
		Object o = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new BufferedInputStream(
					new FileInputStream(file)));
			o = in.readObject();
		} catch (Exception e) {
			System.out.println(e);
		}finally{
			if(in!=null){
				in.close();
			}
		}
		return o;
	}
/**
 * 吧对象写到文件里，对象超过100个的时候会合并
 * @param bean
 * @throws IOException
 */
	public void writeObject(Serializable bean) throws IOException {
		File container = new File(path);
		File[] files = container.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name != null && name.endsWith(".obj")) {
					return true;
				}
				return false;
			}
		});
		if (files != null && files.length > 100) {
			// 超过100个合并，并添加
			List list=new ArrayList();
			for(File file:files){
				Object o=readObject(file);
				list.add(o);
			}
			String fileName=UUID.randomUUID().toString().replace("-","");
			StringBuilder outFilePath=new StringBuilder(path).append("/").append(fileName).append(".lst"); 
			File outFile = new File(outFilePath.toString());
			writeFile(list,outFile);
			for(File file:files){
				file.delete();
			}
		}else {
			String fileName=UUID.randomUUID().toString().replace("-","");
			StringBuilder sbPath=new StringBuilder();
			sbPath.append(path).append("/").append(fileName).append(".obj");
			File outFile = new File(sbPath.toString());
			writeFile(bean, outFile);
		}
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static void main(String[] args){
		SerializableObject SerializableObject=new SerializableObject("G:\\testobject","");
		for(int i=0;i<1000;i++){
			try {
				SerializableObject.writeObject(new BeanTest());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
