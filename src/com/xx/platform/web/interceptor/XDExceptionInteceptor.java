package com.xx.platform.web.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.xx.platform.web.actions.BaseAction;

public class XDExceptionInteceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation ai) {
		Object obj = ai.getAction();

		try {
			return ai.invoke();
		} catch (Exception e) {
			
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				pw.flush();
				pw.close();
				
				RecordException.log(sw.toString());
				 
				if (obj instanceof BaseAction) {
					((BaseAction) obj).setMessage(sw.toString());
				}
			} catch (Exception e1) {
			}

			return "error";
		}
	}

}
