package com.xx.platform.web.actions.index;

import com.xx.platform.core.service.IndexDelete;
import com.xx.platform.core.service.PushDataService;
import com.xx.platform.core.service.PushObject;
import com.xx.platform.web.actions.BaseAction;

public class xmlAction extends BaseAction {
	private static volatile java.util.List<PushObject> pushudatas = new java.util.ArrayList<PushObject>();
	private static volatile java.util.List<PushObject> delay_pushudatas = new java.util.ArrayList<PushObject>();
	private IndexDelete del = new IndexDelete();
	private PushDataService push = new PushDataService();

	public String deleteOneIndexByID() throws Exception {
		int ino = -1;
		int docId = -1;
		try {
			ino = Integer.valueOf(request.getParameter("ino"));
			docId = Integer.valueOf(request.getParameter("docId"));
		} catch (Exception e) {
			request.setAttribute("retval", "false");
			return "success";
		}
		if (ino > -1 && docId > -1) {
			boolean res = del.deleteOneIndexByID(ino, docId);
			request.setAttribute("retval", res == true ? "true" : "false");
		} else {
			request.setAttribute("retval", "false");
		}
		return "success";
	}

	public String deleteOneIndexByField() throws Exception {
		String field = (String) request.getParameter("field");
		String value = (String) request.getParameter("value");
		long res = del.deleteOneIndexByField(field, value);
		request.setAttribute("retval", String.valueOf(res));
		return "success";
	}

	public String push() throws Exception {
		String docType = (String) request.getParameter("docType");
		String val = (String) request.getParameter("val");
		String fld = (String) request.getParameter("fld");
		String[] field = fld.split(",");
		String[] vals = val.split(";");
		String[][] value = new String[vals.length][2];
		for (int i = 0; i < vals.length; i++) {
			value[i] = vals[i].split(",");
		}
		int num = push.push(docType, field, value);
		request.setAttribute("retval", String.valueOf(num));
		return "success";// 没有节点能够连接
	}

	public void merger() throws Exception {
		push.merger();
	}

}
