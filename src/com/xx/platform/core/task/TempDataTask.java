package com.xx.platform.core.task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.nutch.ipc.RPC;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;
import com.xx.platform.util.tools.SerializableObject;

public class TempDataTask extends java.util.TimerTask{
	
	public void run(){
		List<Synchro> synchro = SearchContext.getSynchroList();
		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null&& synchro.size() > 0){
			task();
		}
	}
	public void task(){
		String dataPath=SearchContext.tempDataPath;
		File dataFile=new File(dataPath);
		if(dataFile.exists()){
			File[] files=dataFile.listFiles();
			if(files!=null&&files.length>0){
				List<Document> documents=new ArrayList<Document>(); 
				List<File> deleteFiles=new ArrayList<File>(); 
				SerializableObject serializableObject=new SerializableObject();
				for(File objectFile:files){
					if(objectFile.isFile()){
						try {
							Object object=serializableObject.readObject(objectFile);
							if(objectFile.getPath().endsWith(".lst")){
								List<Document> objectList=(List<Document>)object;
								documents.addAll(objectList);
							}else{
								Document document=(Document)object;
								documents.add(document);
							}
							deleteFiles.add(objectFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(documents.size()>1000){
						//发送到集群中的其他节点
						try {
							sendData(documents);
							clear(documents,deleteFiles);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				////发送到集群中的其他节点
				try {
					sendData(documents);
					clear(documents,deleteFiles);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 
	 * @param documents
	 * @throws Exception 
	 */
	public void sendData(List<Document> documents) throws Exception{
		List<Synchro> synchro = SearchContext.getSynchroList();
		if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null&& synchro.size() > 0){
			for (Synchro s : synchro) {// 遍历每个节点
				((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut)).addDocument(ArraysObjectTool.ObjectToArrays(documents));
			}
		}
	}
	/**
	 * 清空
	 * @param documents
	 * @param deleteFiles
	 */
	public void clear(List<Document> documents,List<File> deleteFiles){
		documents.clear();
		for(File file:deleteFiles){
			file.delete();
		}
		deleteFiles.clear();
	}
}

