package com.xx.platform.web.actions.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.nutch.ipc.RPC;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;

/**
 * ��Ŀ�ļ�������
 * @author �ߵ�Ƽ�
 *
 */
public class ProjectFileManager{
	public final static String testString="_xdtest";//�����ļ��к�׺
	public final static String separator=File.separator;
	public static final String rootPath=SearchContext.contextPath+"search"+separator;
	public static final String publishPath=SearchContext.realPath+"search"+separator;
	public static final String modulePath=SearchContext.realPath+"projectmodule"+separator;
	public static final String errorFlag="50dee8f567984f0aa757f6e05e0096c5";
	/**
	 * ������Դ
	 * @param code
	 * @throws IOException
	 */
	public void publish(String code) throws IOException{
		publish(code,true);
	}
	public void publish(String code,boolean command) throws IOException{
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.publish(ObjectToArrays(code));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		if("default".equalsIgnoreCase(code)){
			String indexFilePath=SearchContext.realPath+"index.jsp";
			File targetindex=new File(indexFilePath);
			File srcIndex=new File(rootPath.concat(code).concat(separator).concat("page").concat(separator).concat("index.jsp"));
			writeFile(srcIndex,targetindex);
			
			String resultFilePath=SearchContext.realPath+"search/result.jsp";
			File targetresult=new File(resultFilePath);
			File srcresult=new File(rootPath.concat(code).concat(separator).concat("page").concat(separator).concat("result.jsp"));
			writeFile(srcresult,targetresult);
			
			String cachedFilePath=SearchContext.realPath+"search/cached.jsp";
			File targetcached=new File(cachedFilePath);
			File srccached=new File(rootPath.concat(code).concat(separator).concat("page").concat(separator).concat("cached.jsp"));
			writeFile(srccached,targetcached);
		}else{
			StringBuilder sbPath=new StringBuilder();
			List<File> listFilesOld=this.getAllFile(publishPath.concat(code));
			for(File file:listFilesOld){
				file.delete();
			}
			List<File> listFiles=this.getAllFile(sbPath.append(rootPath).append(code).toString());
			for(File file:listFiles){
				String oldPath=file.getPath();
				String newPath=oldPath.replace(rootPath,publishPath);
				File newFile=new File(newPath);
				newFile.getParentFile().mkdirs();
				byte[] fileBytes=ArraysObjectTool.fileToBytes(file);
				ArraysObjectTool.getFileFromBytes(fileBytes,newPath);
			}
		}
	}
	/**
	 * ��Ŀ��������    ��Ҫͬ������
	 * @throws IOException 
	 */
	public void publicTest(String code) throws IOException{
		publicTest(code,true);
	}
	public void publicTest(String code,boolean cammond) throws IOException{
		String testCode=code.concat(this.testString);
		StringBuilder sbPath=new StringBuilder();
		List<File> listFilesOld=this.getAllFile(publishPath.concat(testCode));
		for(File file:listFilesOld){
			file.delete();
		}
		List<File> listFiles=this.getAllFile(sbPath.append(rootPath).append(code).toString());
		for(File file:listFiles){
			String oldPath=file.getPath();
			String newPath=oldPath.replace(rootPath+code,publishPath+testCode);
			File newFile=new File(newPath);
			newFile.getParentFile().mkdirs();
			byte[] fileBytes=ArraysObjectTool.fileToBytes(file);
			ArraysObjectTool.getFileFromBytes(fileBytes,newPath);
		}
		if(cammond){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.publicTest(code);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
	/**
	 * ��һ���ļ�������д����һ���ļ���
	 * @param srcFile
	 * @param targetFile
	 * @throws IOException 
	 */
	public void writeFile(File srcFile,File targetFile) throws IOException{
		byte[] target=ArraysObjectTool.fileToBytes(srcFile);
		ArraysObjectTool.getFileFromBytes(target, targetFile);
	}
	/**
	 * ɾ���Ѿ���������Դ
	 * @param code
	 * @throws IOException
	 */
	public void reback(String code) throws IOException{
		reback(code,true);
	}
	public void reback(String code,boolean command) throws IOException{
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.reback(ObjectToArrays(code));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		StringBuilder sbPath=new StringBuilder();
		deleteFile(new File(sbPath.append(publishPath).append(code).toString()));
	}
	/**
	 * �����ļ�  һ����Ŀ���ɵ�ʱ����Ҫ�������ļ���,�Լ��ļ�
	 * 
	 * @param projectCode
	 * @throws IOException 
	 */
	public void createFile(String projectCode,boolean command) throws IOException{
		if (command) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.createFile(ObjectToArrays(projectCode));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		String data=rootPath.concat(projectCode).concat(separator).concat("data");
		String page=rootPath.concat(projectCode).concat(separator).concat("page");
		String script=rootPath.concat(projectCode).concat(separator).concat("script");
		File dataFile=new File(data);
		File pageFile=new File(page);
		File scriptFile=new File(script);
		scriptFile.mkdirs();
		dataFile.mkdirs();
		pageFile.mkdirs();
		File indexFile=new File(modulePath.concat("index.jsp"));
		byte[] fileBytes=ArraysObjectTool.fileToBytes(indexFile);
		ArraysObjectTool.getFileFromBytes(fileBytes,pageFile.getPath()+"/index.jsp");
		File resultFile=new File(modulePath.concat("result.jsp"));
		byte[] fileBytesResult=ArraysObjectTool.fileToBytes(resultFile);
		ArraysObjectTool.getFileFromBytes(fileBytesResult,pageFile.getPath()+"/result.jsp");
		File loginFile=new File(modulePath.concat("login.jsp"));
		byte[] loginBytesResult=ArraysObjectTool.fileToBytes(loginFile);
		ArraysObjectTool.getFileFromBytes(loginBytesResult,pageFile.getPath()+"/login.jsp");
		File cached=new File(modulePath.concat("cached.jsp"));
		byte[] cachedBytesResult=ArraysObjectTool.fileToBytes(cached);
		ArraysObjectTool.getFileFromBytes(cachedBytesResult,pageFile.getPath()+"/cached.jsp");
		File content=new File(modulePath.concat("content.jsp"));
		byte[] contentBytesResult=ArraysObjectTool.fileToBytes(content);
		ArraysObjectTool.getFileFromBytes(contentBytesResult,pageFile.getPath()+"/content.jsp");
	}
	/**
	 * ɾ���ļ�
	 * 
	 * @param path
	 */
	public  void deleteFileByPath(String path,boolean command){
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.deleteFileByPath(ObjectToArrays(path));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		path=rootPath.concat(path);
		File file=new File(path);
		if(file.exists()){
			file.delete();
		}
	}
	
	/**
	 * ɾ��һ����Ŀ�µ�������Դ
	 * 
	 * @param projectCode����Ŀ����
	 */
	public void deleteFile(String projectCode,boolean command){
		if (command) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.deleteFile(ObjectToArrays(projectCode));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		String filePath=rootPath.concat(projectCode);
		File projectFile=new File(filePath);
		deleteFile(projectFile);
	}
	/**
	 * ɾ��һ����Ŀ�µ����з�����Դ
	 * @param code
	 */
	public void deletePulishResource(String code){
		deletePulishResource(code,true);	
	}
	public void deletePulishResource(String code,boolean cammond){
		String filePath=publishPath.concat(code);
		File projectFile=new File(filePath);
		deleteFile(projectFile);
	}
	/**
	 * ɾ��һ����Ŀ�µ����в�����Դ
	 * @param code
	 */
	public void deleteTestResource(String code){
		deleteTestResource(code,true);
	}
	public void deleteTestResource(String code,boolean cammond){
		String filePath=publishPath.concat(code.concat(testString));
		File projectFile=new File(filePath);
		deleteFile(projectFile);
		if(cammond){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					((ImInterface) RPC.getProxy(ImInterface.class,
							ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
							.deleteTestResource(code);
				}
		}
	}
	/**
	 * ɾ��һ���ĵ�
	 * 
	 * @param path���ĵ�·��
	 */
	public void deleteDoc(String projectCode,String fileName,String docName,boolean command){
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.deleteDoc(ObjectToArrays(projectCode),ObjectToArrays(fileName),ObjectToArrays(docName));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		String filePath = ProjectFileManager.rootPath.concat(projectCode).concat(ProjectFileManager.separator).concat(fileName);
		String docPath = filePath.concat(ProjectFileManager.separator).concat(docName);
		File file=new File(docPath);
		deleteFile(file);
	}
	/**
	 * ���õ�ɾ������
	 * �˷�������         ����          ͬ��
	 * @param rootFile���ļ�
	 */
	private static void deleteFile(File rootFile){
		if(rootFile.exists()){
			if(rootFile.isFile()){
				rootFile.delete();
			}else{
				File[] files=rootFile.listFiles();
				if(files==null||files.length==0){
					rootFile.delete();
				}else{
					for(File file:files){
						deleteFile(file);
					}
					rootFile.delete();
				}
			}
		}
	}
	/**
	 * ��ȡһ����Ŀ�µ�
	 * ���з��ļ����ļ�
	 * @param path
	 * @return
	 */
	private static List<File> getAllFile(String path){
		List<File> files=new ArrayList<File>();
		File file=new File(path);
		if(file.isFile()){
			files.add(file);
		}else{
			File[] fileList=file.listFiles();
			if(fileList!=null&&fileList.length>0){
				for(File theFile:fileList){
					if(theFile.isFile()){
						files.add(theFile);
					}else{
						files.addAll(getAllFile(theFile.getPath()));
					}
				}
			}
		}
		return files;
	}
	/**
	 * ��ȡһ���ļ����µ������ļ�
	 * @param path
	 * @return
	 */
	public static List<SourceFile> getChildFiles(String path){
		File rootFile=new File(path);
		List<SourceFile> fileList=new ArrayList<SourceFile>();
		if(rootFile.isDirectory()){
			File[] files=rootFile.listFiles();
			if(files!=null){
				for(File file:files){
					fileList.add(new SourceFile(file));
				}
			}
		}
		return fileList;
	}
	/**
	 * ������һ���ļ�
	 * 
	 * @param path
	 * @param newName
	 */
	public boolean rename(String projectCode,String fileName,String docName,String newName,boolean command){
		if (command) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.rename(ObjectToArrays(projectCode),ObjectToArrays(fileName),ObjectToArrays(docName),ObjectToArrays(newName));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
		String filePath = ProjectFileManager.rootPath.concat(projectCode).concat(ProjectFileManager.separator).concat(fileName);
		String docPath = filePath.concat(ProjectFileManager.separator).concat(docName);
		String newPath = filePath.concat(ProjectFileManager.separator).concat(newName);
		File file=new File(docPath);
		File newFile=new File(newPath);
		if(newFile.exists()){
			return false;
		}
		file.renameTo(new File(newPath));
		return true;
	}
	/**
	 * ����һ�����ļ�
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @param newName
	 * @param command
	 * @return
	 */
	public boolean createNewFile(String projectCode,String fileName,String docName,boolean command){
		String filePath = ProjectFileManager.rootPath.concat(projectCode).concat(ProjectFileManager.separator).concat(fileName);
		String docPath = filePath.concat(ProjectFileManager.separator).concat(docName);
		File file=new File(docPath);
		BufferedWriter writer=null;
		if(file.exists()){
			return false;
		}
		try {
			if (command) {
				List<Synchro> synchro = SearchContext.getSynchroList();
				if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
						&& synchro.size() > 0)
					for (Synchro s : synchro) {// ����ÿ���ڵ�
						try {
							((ImInterface) RPC.getProxy(ImInterface.class,
									ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
									.createNewFile(ObjectToArrays(projectCode),ObjectToArrays(fileName),ObjectToArrays(docName));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
			}
			file.createNewFile();
			java.io.FileOutputStream writerStream = new java.io.FileOutputStream(file);    
			writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(writerStream, "UTF-8"));
			if(file.getName().endsWith(".js")){
				writer.write("//js�ű��ļ�");
			}else if(file.getName().endsWith(".css")){
				writer.write("/*--��ʽ�ļ�--*/");
			}else if(file.getName().endsWith(".jsp")){
				writer.write("<!--JSPҳ���ļ�����ѯ��ҳ��Ϊindex.jsp����ѯ���ҳ��Ϊresult.jsp-->");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(writer!=null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	/**
	 * �����ļ��������
	 * @param projectCode
	 * @param fileName
	 * @param docName
	 * @return
	 * @throws IOException 
	 * @throws IOException
	 */
	public static String readFileHTML(String projectCode,String fileName,String docName) throws IOException{
		StringBuilder sbContent=new StringBuilder("");
		File file=new File(rootPath.concat(projectCode).concat(separator).concat(fileName).concat(separator).concat(docName));
		//Charset charset=Charset.defaultCharset();
		if(file.exists()){
			//charset=GetFileEnCoding.getEncoding(file);
			SourceFile sFile=new SourceFile(file); 
			String suffix = sFile.getSuffix();
			if(suffix!=null&&suffix.trim().length()>0){
				if("js".equalsIgnoreCase(suffix)||"vbs".equalsIgnoreCase(suffix)||"css".equalsIgnoreCase(suffix)||"jsp".equalsIgnoreCase(suffix)||"txt".equalsIgnoreCase(suffix)||"sql".equalsIgnoreCase(suffix)||"html".equalsIgnoreCase(suffix)||"htm".equalsIgnoreCase(suffix)){
					InputStream is=null;
					InputStreamReader isr=null;
					BufferedReader br=null;
					try {
						is = new FileInputStream(file);
						isr=new InputStreamReader(is,"utf-8");
						//isr=new InputStreamReader(is);
						br=new BufferedReader(isr);
						String line="";
						while((line=br.readLine())!=null){
							sbContent.append(line).append(System.getProperty("line.separator"));
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(is!=null){
							is.close();
						}
						if(isr!=null){
							isr.close();
						}
						if(br!=null){
							br.close();
						}
					}
				}else{
					sbContent.append(errorFlag);
				}
			}
		}
		return sbContent.toString();
	}
	/**
	 * �޸��ļ�������
	 * 
	 * @throws IOException 
	 */
	public void editSource(String projectCode,String fileName,String docName,String content,boolean command) throws IOException{
		if (command) {
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.editSource(ObjectToArrays(projectCode),ObjectToArrays(fileName),ObjectToArrays(docName),ObjectToArrays(content));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
		String path=ProjectFileManager.rootPath.concat(projectCode).concat(ProjectFileManager.separator).concat(fileName).concat(ProjectFileManager.separator).concat(docName);
		File file=new File(path);
		if(file.exists()&&file.isFile()){
			FileOutputStream out=null;
			OutputStreamWriter writer=null;
			BufferedWriter print=null;
			try {
				out=new FileOutputStream(file);
				writer=new OutputStreamWriter(out,"utf-8");
				print=new BufferedWriter(writer);
				print.write(content);
				print.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(out!=null){
					out.close();
				}
				if(writer!=null){
					writer.close();
				}
				if(print!=null){
					print.close();
				}
			}
			
		}
	}
	/**
	 * �ϴ��ļ�
	 * 
	 * @param filePath
	 * @param docPath
	 * @param uploadFile
	 * @throws IOException 
	 */
	public void upload(String projectCode,String fileName,String docName,File uploadFile,boolean commond){
		if(commond){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null
					&& synchro.size() > 0)
				for (Synchro s : synchro) {// ����ÿ���ڵ�
					try {
						byte[] fileBytes=fileToBytes(uploadFile);
						((ImInterface) RPC.getProxy(ImInterface.class,
								ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut))
								.upload(ObjectToArrays(projectCode),ObjectToArrays(fileName),ObjectToArrays(docName),fileBytes);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		String filePath = ProjectFileManager.rootPath.concat(projectCode).concat(ProjectFileManager.separator).concat(fileName);
		String docPath = filePath.concat(ProjectFileManager.separator).concat(docName);
		FileOutputStream fos=null;
		FileInputStream fis=null;
		try {
			fos = new FileOutputStream(docPath);
			
			fis = new FileInputStream(uploadFile);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = fis.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(uploadFile!=null&&uploadFile.exists()&&uploadFile.isFile()){
				uploadFile.delete();
			}
		}
	}
	/**
	 * �ļ����2����
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static byte[] fileToBytes(File f) throws IOException {
        if (f == null) {
            return null;
        }
        FileInputStream stream=null;
        ByteArrayOutputStream out=null;
        try {
        	stream= new FileInputStream(f);
        	out= new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1){
            	out.write(b, 0, n);
            }
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        	if(stream!=null){
        		stream.close();
        	}
        	if(out!=null){
        		out.close();
        	}
        }
        return null;
    }
	private byte[] ObjectToArrays(Object dataObject) throws IOException {
		java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
		java.io.ObjectOutputStream objOut = new java.io.ObjectOutputStream(
				baout);
		objOut.writeObject(dataObject);
		byte[] data = baout.toByteArray();
		objOut.close();
		baout.close();
		return data;
	}

	public static void main(String[] args){
		String a="aaaa.txt";
		System.out.println(a.substring(a.lastIndexOf(".")+1,a.length()));
	}
}
