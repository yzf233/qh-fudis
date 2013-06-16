package com.xx.platform.core.script;

import java.util.*;
import java.util.regex.*;

import org.mozilla.javascript.*;
import org.apache.bsf.BSFManager;
import org.apache.bsf.BSFEngine;

import com.xx.platform.core.db.WebDbAdminTool;
import com.xx.platform.dao.IBase;
import com.xx.platform.plugin.url.InnerURL;
import com.xx.platform.util.constants.IbeaProperty;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BSFScriptManage {
    public static final void execute(String script , IBase service , InnerURL innerUrl) throws Exception
    {
//        Context cx = Context.enter();
//        cx.setLanguageVersion(Context.VERSION_1_2);
//        List<String> urlList = new ArrayList() ;
        Pattern pattern = Pattern.compile("^(http|https|ftp|file)://([\\w\\-_]+\\.)+[\\w\\-_]+([\\w\\-_./?%&=;@ ]*)") ;
        Matcher matcher ;

//        Scriptable scope = cx.initStandardObjects();
//        ScriptableObject.putProperty(scope, "crawl",Context.javaToJS(new ScriptCrawlTool() , scope) );
//
//        script = script.replaceAll("\n","") ;
//        Object object = cx.evaluateString(scope , script , "outer" , 1 , null) ;
        BSFManager mgr = new BSFManager();
        mgr.registerScriptingEngine("javascript",
                                    "org.apache.bsf.engines.javascript.JavaScriptEngine",
                                    null);
        List<String> resList = null ;
        mgr.declareBean("crawl", new ScriptCrawlTool(), ScriptCrawlTool.class);
        mgr.declareBean("returnValue", resList = new java.util.ArrayList(), ArrayList.class);
        mgr.declareBean("service", service,service.getClass());
        mgr.declareBean("url", innerUrl,innerUrl.getClass());
        mgr.declareBean("log", IbeaProperty.log, IbeaProperty.log.getClass());
        mgr.declareBean("list1", new java.util.ArrayList(), ArrayList.class);
        mgr.declareBean("list2", new java.util.ArrayList(), ArrayList.class);
        mgr.declareBean("list3", new java.util.ArrayList(), ArrayList.class);
        mgr.declareBean("list4", new java.util.ArrayList(), ArrayList.class);
        mgr.declareBean("list5", new java.util.ArrayList(), ArrayList.class);
        BSFEngine engine = mgr.loadScriptingEngine("javascript");
        Object return_value = engine.eval("script", -1, -1, script);
        return_value = return_value instanceof java.util.ArrayList ? return_value : resList ;
        if(return_value instanceof java.util.ArrayList)
        {
            for(String url :(List<String>)return_value)
            {
                if(url==null || url.trim().length()==0)
                    continue ;
                matcher = pattern.matcher(url) ;
                if(url!=null && matcher.find())
                {
                    innerUrl.setUrl(url);
                    try {
                        service.saveIObject(innerUrl);
                    } catch (Exception ex) {
                    }
                }
            }
            WebDbAdminTool.reloadReader();
        }

    }
    public static void main(String[] args)
    {
        try {
//            BSFManager mgr = new BSFManager();
//            mgr.registerScriptingEngine("javascript",
//                                        "org.apache.bsf.engines.javascript.JavaScriptEngine",
//                                        null);
//            mgr.declareBean("crawl" , new ScriptCrawlTool(),ScriptCrawlTool.class);
//            BSFEngine engine = mgr.loadScriptingEngine("javascript");
//            Object return_value = engine.eval("asdf", -1, -1, "['a' , 'b' , 'c']");
//            System.out.println("got: " + return_value.getClass());
            Pattern pattern = Pattern.compile("^(http|https|ftp|file)://([\\w\\-_]+\\.)+[\\w\\-_]+([\\w\\-_./?%&=;@ ]*)") ;
            Matcher matcher=pattern.matcher("http://www.xd-tech.com.cn") ;

            System.out.println(matcher.find());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
