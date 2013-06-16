package com.xx.platform.web.actions.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.nutch.DirectoryFethListTool;
import com.xx.platform.core.nutch.IndexFieldImpl;
import com.xx.platform.domain.model.database.Dbconfig;
import com.xx.platform.domain.model.database.Dbtable;
import com.xx.platform.domain.model.database.FileDirectory;
import com.xx.platform.domain.model.database.Tableproperty;
import com.xx.platform.domain.model.distributed.Diserver;
import com.xx.platform.domain.model.system.CrontabTaskAuto;
import com.xx.platform.domain.model.system.Xdtechsite;
import com.xx.platform.domain.model.user.User;
import com.xx.platform.web.actions.BaseAction;

public class DbBackupAction extends BaseAction implements java.io.Serializable  {

	// public static final String path = Thread.currentThread()
	// .getContextClassLoader().getResource("").getPath().replace(
	// "/WEB-INF/classes", "/DataBaseBackUP");

	private File upload;
	private String uploadFileName;

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String DbDackUp() {
		List backupList = new ArrayList();
		// 将需要备份的表加入备份List
//		backupList.addAll(service.findAllByIObjectCType(Dbtable.class));
		
		List<Dbtable> ls=service.findAllByIObjectCType(Dbtable.class);
			for(Dbtable d:ls)
			{
				d.setTableproperty(null);
				backupList.add(d);
			}
		backupList.addAll(service.findAllByIObjectCType(Dbconfig.class));
		backupList.addAll(service.findAllByIObjectCType(Tableproperty.class));
		backupList.addAll(service.findAllByIObjectCType(User.class));
		backupList.addAll(service.findAllByIObjectCType(CrontabTaskAuto.class));
 		backupList.addAll(service.findAllByIObjectCType(Xdtechsite.class));
 		backupList.addAll(service.findAllByIObjectCType(Diserver.class));
 		backupList.addAll(service.findAllByIObjectCType(FileDirectory.class));
 		backupList.addAll(service.findAllByIObjectCType(IndexFieldImpl.class));
 		


		java.io.ObjectOutputStream ops = null;
		// 设置输出格式和输出文件名
		response.setContentType("application/os-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ new java.text.SimpleDateFormat("yyyy-MM-dd ")
						.format(new Date()) + "xdbackup.dat" + "\"");
		// 初始化输出文件-直接在浏览器下载
		try {
			ops = new java.io.ObjectOutputStream(response.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		// 开始输出文件
		try {
			ops.writeObject(backupList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 关闭输出流
			if (ops != null) {
				try {
					ops.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		message = "备份成功！";
		return null;
	}

	public String dbRes() {
		message = null;
		try {
			//
			if (upload != null) {
				// // 将要还原的数据库清空
				service.execByHQL("delete from CrontabTaskAuto");
				service.execByHQL("delete from Xdtechsite");
				service.execByHQL("delete from Diserver");
				service.execByHQL("delete from Tableproperty");
				service.execByHQL("delete from FileDirectory");
				service.execByHQL("delete from Dbtable");
				service.execByHQL("delete from Dbconfig");
				service.execByHQL("delete from User");
				service.execByHQL("delete from IndexFieldImpl");
				//
				// // 初始化輸入流
				// byte s[]=request.getParameter("f").getBytes();
				// javax.servlet.ServletInputStream =request.getInputStream();
				// ip=request.getInputStream();
				java.io.ObjectInputStream opin = new java.io.ObjectInputStream(
						new FileInputStream(upload));
				//			
				// 將輸入流读取为List
				List resList = (ArrayList) opin.readObject();
				// 遍历List并导入数据库
				for (Object opc : resList) {
					java.lang.reflect.Method idMethod = opc.getClass()
							.getMethod("getId", new Class[] {});

					String oldId = (String) idMethod.invoke(opc,
							new Object[] {});
					if (service.findAllByCriteria(
							DetachedCriteria.forClass(opc.getClass()).add(
									Restrictions.eq("id", oldId))).size() <= 0) {
						service.saveIObject(opc);
						String newId = (String) idMethod.invoke(opc,
								new Object[] {});
						service.execByHQL("update " + opc.getClass().getName()
								+ " set id='" + oldId + "' where id='" + newId
								+ "'");
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}finally{
			upload.delete();
		}
		message = "数据库还原成功，请重新启动服务器<br/>以应用新的设置";
		return Action.SUCCESS;
	}

	public static void main(String args[]) throws Exception {
		System.out.println(new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()));
	}
}
