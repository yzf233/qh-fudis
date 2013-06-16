package com.xx.platform.web.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.xx.platform.core.SearchContext;
import com.xx.platform.core.rpc.ImDistributedTool;

public class CheckSynchroInterceptor extends AbstractInterceptor{

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		if(SearchContext.getSynchroList()!=null && SearchContext.getSynchroList().size()>0){
			if(ImDistributedTool.isRuning&&!ImDistributedTool.isReady){
				return "synchro";
			}
		}
		return arg0.invoke();
	}

}
