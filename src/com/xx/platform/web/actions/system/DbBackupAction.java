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
		// ����Ҫ���ݵı���뱸��List
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
		// ���������ʽ������ļ���
		response.setContentType("application/os-stream");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ new java.text.SimpleDateFormat("yyyy-MM-dd ")
						.format(new Date()) + "xdbackup.dat" + "\"");
		// ��ʼ������ļ�-ֱ�������������
		try {
			ops = new java.io.ObjectOutputStream(response.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		// ��ʼ����ļ�
		try {
			ops.writeObject(backupList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// �ر������
			if (ops != null) {
				try {
					ops.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		message = "���ݳɹ���";
		return null;
	}

	public String dbRes() {
		message = null;
		try {
			//
			if (upload != null) {
				// // ��Ҫ��ԭ�����ݿ����
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
				// // ��ʼ��ݔ����
				// byte s[]=request.getParameter("f").getBytes();
				// javax.servlet.ServletInputStream =request.getInputStream();
				// ip=request.getInputStream();
				java.io.ObjectInputStream opin = new java.io.ObjectInputStream(
						new FileInputStream(upload));
				//			
				// ��ݔ������ȡΪList
				List resList = (ArrayList) opin.readObject();
				// ����List���������ݿ�
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
		message = "���ݿ⻹ԭ�ɹ�������������������<br/>��Ӧ���µ�����";
		return Action.SUCCESS;
	}

	public static void main(String args[]) throws Exception {
		System.out.println(new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()));
	}
}
