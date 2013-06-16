package com.xx.platform.web.actions.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.Action;
import com.xx.platform.core.SearchContext;
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
public class LogAction extends BaseAction {
	private List<Logfile> logList;
	private Logfile logfile;
	private String message;

	public String listLog() {
		File[] fileList = SearchContext.logFileDir.listFiles();
		logList = new ArrayList();
		for (File file : fileList) {
			if (file != null && !file.getName().endsWith("lck")) {
				logfile = new Logfile();
				logfile.setFileName(file.getName());
				logfile.setFileSize(file.length());
				logfile.setLastModi(file.lastModified());
				logList.add(logfile);
			}
		}
		return Action.SUCCESS;
	}

	public String viewLog() {
		InputStream logFile = null;
		response.setContentType("text/plain;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		if (logfile != null && logfile.getFileName() != null) {
			response.addHeader("Content-Disposition", "attachment; filename="
					+ logfile.getFileName());
			try {
				logFile = new FileInputStream(new File(
						SearchContext.logFileDir, logfile.getFileName()));
				byte[] logText = new byte[1024];
				while (logFile.read(logText) > 0) {
					response.getOutputStream().write(logText);
					logText = new byte[1024];
				}
				response.flushBuffer();
			} catch (Exception ex) {
			} finally {
				try {
					logFile.close();
				} catch (IOException ex1) {
				}
			}
		}
		return null;
	}

	public String deleteLog() {
		if (logfile != null && logfile.getFileName() != null) {
			try {
				File logFile = new File(SearchContext.logFileDir, logfile
						.getFileName());
				if (logFile.delete()) {
//					message = "日志文件删除成功";
				} else {
					message = "删除失败，可能该文件正在使用或已被删除";
				}
			} catch (Exception ex) {
				message = "删除失败，可能该文件正在使用或已被删除";
			}
		} else {
			message = "日志文件删除失败，参数错误";
		}
		listLog();
		return Action.SUCCESS;
	}

	public List getLogList() {
		return logList;
	}

	public Logfile getLogfile() {
		return logfile;
	}

	public void setLogList(List logList) {
		this.logList = logList;
	}

	public void setLogfile(Logfile logfile) {
		this.logfile = logfile;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
