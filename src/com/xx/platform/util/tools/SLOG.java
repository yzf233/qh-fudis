package com.xx.platform.util.tools;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.system.Sqllog;

public class SLOG {
	public SLOG(){};
    public static void addMessage(String str)
    {
    	if(str.replace(" ","").equals(""))
    		return;
		Sqllog slog=new Sqllog();
		slog.setDtime(new Date());
		slog.setMessage(str);
		SearchContext.getDao().saveIObject(slog);
		
    }
}
