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
				message = "路径格式错误，请查看页面中路径格式示例！";
			}
			return Action.INPUT;

		} else if (request.getParameter("type") != null
				&& request.getParameter("type").equals("1")) {
			if (fileDirectory != null) {
				fileDirectory.setState("0");
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//去除空格-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.saveIObject(fileDirectory);
				message = "添加成功";
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
				message = "路径格式错误，请查看页面中路径格式示例！";
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
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//去除空格-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.saveIObject(fileDirectory);
			} else {
				message = "路径格式错误，请查看页面中路径格式示例！";
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
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//去除空格-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.updateIObject(fileDirectory);
				message = "目录信息修改成功";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("2")) { // 删除
				service.deleteIObject(fileDirectory);
				message = "目录库信息删除成功";
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
					message = "路径格式错误，请查看页面中路径格式示例！";
				}
				return Action.INPUT;
			} else {
				message = "路径格式错误，请查看页面中路径格式示例！";
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
				fileDirectory.setFileTypes(fileDirectory.getFileTypes()!=null?fileDirectory.getFileTypes().replace(" ",""):"");//去除空格-hujun
				fileDirectory.setLocalip(SearchContext.getXdtechsite().getLocalip());
				service.updateIObject(fileDirectory);
				message = "目录信息修改成功";
			} else if (request.getParameter("type") != null
					&& request.getParameter("type").equals("2")) { // 删除
				service.deleteIObject(fileDirectory);
				message = "目录库信息删除成功";
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
					message = "路径格式错误，请查看页面中路径格式示例！";
				}
				return Action.INPUT;
			} else {
				message = "路径格式错误，请查看页面中路径格式示例！";
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
				message = "测试成功";

			} else if (url.indexOf("\\") >= 0) {
				message = "路径格式错误，请采用'/'作为分隔符";
			} else if (!url.substring(url.length() - 1).equals("/")) {

				message = "测试失败,请在远程文件路径结尾加入'/'！";
			} else {
				message = "测试失败,请检查用户名与密码或路径格式是否正确！";
			}

		} catch (Exception e) {
			message = "测试失败,请检查用户名与密码或路径格式是否正确！";
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
