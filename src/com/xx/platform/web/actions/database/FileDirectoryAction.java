package com.xx.platform.web.actions.database;

import java.util.List;

import jcifs.smb.SmbFile;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.database.FileDirectory;
import com.xx.platform.web.actions.BaseAction;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class FileDirectoryAction extends BaseAction {
	private List<FileDirectory> fileDirectoryList;

	private FileDirectory fileDirectory;

	private List<String> testlist;

	private Integer sum = 0;

	private int page = 1;


	// public String test() throws Exception {
	// DirectoryFethListTool tool = new DirectoryFethListTool();
	// List<Map<String, Object>> dataList = tool.getDbList();
	// testlist = new java.util.ArrayList<String>();
	// for(Map<String, Object> map : dataList){
	// //testlist.add("content:"+map.get("content")==null?"":map.get("content"));
	// }
	// return Action.SUCCESS;
	// }

	

	public String list() throws Exception {
		// System.out.println("page="+page);
		fileDirectoryList = service.findByIObjectCType(FileDirectory.class,
				page, FIV_PAGE_SIZE);
		return Action.SUCCESS;
	}

	public String netlist() throws Exception {
		fileDirectoryList = service.findAllByCriteria(DetachedCriteria
				.forClass(FileDirectory.class).add(
						Restrictions.eq("dirtype", "remote")));
		return Action.SUCCESS;
	}

	public String addDo() throws Exception {
		if (request.getParameter("type") != null
				&& request.getParameter("type").equals("0")) {
			String url = "smb://" + request.getParameter("fileDirectory.uName")
					+ ":" + request.getParameter("fileDirectory.uPwd") + "@"
					+ request.getParameter("fileDirectory.remoteIPaddress")
					+ request.getParameter("fileDirectory.remoteFiledir");
			if (request.getParameter("fileDirectory.remoteFiledir").startsWith(
					"/")) {
				message = testNetfile(url);
			} else {
				message = "·����ʽ������鿴ҳ����·����ʽʾ����";
			}
			return Action.INPUT;

		} else if (request.getParameter("type") != null
				&& request.getParameter("type").equals("1")) {
			if (fileDirectory != null) {
				fileDirectory.setState("0");
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//ȥ���ո�-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.saveIObject(fileDirectory);
				message = "��ӳɹ�";
			}
		}
		return Action.SUCCESS;
	}

	public String addnetDo() throws Exception {
		if (request.getParameter("type") != null
				&& request.getParameter("type").equals("0")) {
			String url = "smb://" + request.getParameter("fileDirectory.uName")
					+ ":" + request.getParameter("fileDirectory.uPwd") + "@"
					+ request.getParameter("fileDirectory.remoteIPaddress")
					+ request.getParameter("fileDirectory.remoteFiledir");
			if (request.getParameter("fileDirectory.remoteFiledir").startsWith(
					"/")
					&& request.getParameter("fileDirectory.remoteFiledir")
							.endsWith("/")
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("//") >= 0)
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("\\") >= 0)) {
				message = testNetfile(url);
			} else {
				message = "·����ʽ������鿴ҳ����·����ʽʾ����";
			}
			return Action.INPUT;

		} else if (request.getParameter("type") != null
				&& request.getParameter("type").equals("1")) {
			if (fileDirectory != null
					&& request.getParameter("fileDirectory.remoteFiledir")
							.startsWith("/")
					&& request.getParameter("fileDirectory.remoteFiledir")
							.endsWith("/")
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("//") >= 0)
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("\\") >= 0)) {
				fileDirectory.setState("0");
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//ȥ���ո�-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.saveIObject(fileDirectory);
			} else {
				message = "·����ʽ������鿴ҳ����·����ʽʾ����";
				return Action.INPUT;
			}
		}
		return Action.SUCCESS;
	}

	public String edit() throws Exception {
		if ((fileDirectory != null && fileDirectory.getId() != null)) {
			fileDirectory = (FileDirectory) service.getIObjectByPK(
					FileDirectory.class, fileDirectory.getId());
			// System.out.println("LAYER:"+fileDirectory.getIslayers());
		}
		return Action.SUCCESS;
	}

	public String editDo() throws Exception {
		if ((fileDirectory != null && fileDirectory.getId() != null)) {
			if (request.getParameter("type") != null
					&& request.getParameter("type").equals("1")) {
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//ȥ���ո�-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.updateIObject(fileDirectory);
				message = "Ŀ¼��Ϣ�޸ĳɹ�";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("2")) { // ɾ��
				service.deleteIObject(fileDirectory);
				message = "Ŀ¼����Ϣɾ���ɹ�";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("3")) {
				String url = "smb://"
						+ request.getParameter("fileDirectory.uName") + ":"
						+ request.getParameter("fileDirectory.uPwd") + "@"
						+ request.getParameter("fileDirectory.remoteIPaddress")
						+ request.getParameter("fileDirectory.remoteFiledir");
				if (request.getParameter("fileDirectory.remoteFiledir")
						.startsWith("/")
						&& request.getParameter("fileDirectory.remoteFiledir")
								.endsWith("/")
						&& !(request
								.getParameter("fileDirectory.remoteFiledir")
								.indexOf("//") >= 0)
						&& !(request
								.getParameter("fileDirectory.remoteFiledir")
								.indexOf("\\") >= 0)) {
					message = testNetfile(url);
				} else {
					message = "·����ʽ������鿴ҳ����·����ʽʾ����";
				}
				return Action.INPUT;
			} else {
				message = "·����ʽ������鿴ҳ����·����ʽʾ����";
				return Action.INPUT;
			}
		}
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	public String editNetDo() throws Exception {
		if ((fileDirectory != null && fileDirectory.getId() != null)) {
			if (request.getParameter("type") != null
					&& request.getParameter("type").equals("1")
					&& request.getParameter("fileDirectory.remoteFiledir")
							.startsWith("/")
					&& request.getParameter("fileDirectory.remoteFiledir")
							.endsWith("/")
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("//") >= 0)
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("\\") >= 0)) {
				// System.out.println(fileDirectory.getIslayers()+"---");
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//ȥ���ո�-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.updateIObject(fileDirectory);
				message = "Ŀ¼��Ϣ�޸ĳɹ�";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("2")) { // ɾ��
				service.deleteIObject(fileDirectory);
				message = "Ŀ¼����Ϣɾ���ɹ�";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("3")) {
				String url = "smb://"
						+ request.getParameter("fileDirectory.uName") + ":"
						+ request.getParameter("fileDirectory.uPwd") + "@"
						+ request.getParameter("fileDirectory.remoteIPaddress")
						+ request.getParameter("fileDirectory.remoteFiledir");
				if (request.getParameter("fileDirectory.remoteFiledir")
						.startsWith("/")
						&& request.getParameter("fileDirectory.remoteFiledir")
								.endsWith("/")
						&& !request.getParameter("fileDirectory.remoteFiledir")
								.startsWith("//")
						&& !request.getParameter("fileDirectory.remoteFiledir")
								.endsWith("//")) {
					message = testNetfile(url);
				} else {
					message = "·����ʽ������鿴ҳ����·����ʽʾ����";
				}
				return Action.INPUT;
			} else {
				message = "·����ʽ������鿴ҳ����·����ʽʾ����";
				return Action.INPUT;
			}
		}
		SearchContext.reloadRules();
		return Action.SUCCESS;
	}

	private String testNetfile(String url) {
		try {
			SmbFile smbFile = new SmbFile(url);
			if (smbFile.canRead()
					&& url.substring(url.length() - 1).equals("/")
					&& !(request.getParameter("fileDirectory.remoteFiledir")
							.indexOf("//") >= 0)) {
				message = "���Գɹ�";

			} else if (url.indexOf("\\") >= 0) {
				message = "·����ʽ���������'/'��Ϊ�ָ���";
			} else if (!url.substring(url.length() - 1).equals("/")) {

				message = "����ʧ��,����Զ���ļ�·����β����'/'��";
			} else {
				message = "����ʧ��,�����û����������·����ʽ�Ƿ���ȷ��";
			}

		} catch (Exception e) {
			message = "����ʧ��,�����û����������·����ʽ�Ƿ���ȷ��";
		}
		return message;

	}

	public FileDirectory getFileDirectory() {
		return fileDirectory;
	}

	public List getFileDirectoryList() {
		return fileDirectoryList;
	}

	public int getPage() {
		return page;
	}

	public Integer getSum() {
		return sum;
	}

	public List getTestlist() {
		return testlist;
	}

	public void setFileDirectory(FileDirectory fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setFileDirectoryList(List fileDirectoryList) {
		this.fileDirectoryList = fileDirectoryList;
	}

	public void setTestlist(List testlist) {
		this.testlist = testlist;
	}

}
