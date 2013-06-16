package com.xx.platform.core.task;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.NutchCommand;
import com.xx.platform.util.constants.IbeaProperty;

import org.apache.nutch.searcher.Query;
import java.io.*;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.fs.NutchFileSystem;
/**
 * <p>Title: </p>
 *
 * <p>Description: ɾ������ </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public class IndexTask{
    private Query query = SearchContext.getTaskQurey();

    public void doTask() {
            IbeaProperty.log.info("����������������!");
            if(query!=null)
                try {
                    org.apache.nutch.searcher.NutchBean.getBean().
                            deleteDocuments(query);
                } catch (IOException ex) {
                    IbeaProperty.log.info("���������������쳣!["+ex.getMessage()+"]");
                }
            IbeaProperty.log.info("���������������!");

    }
    public void merger(){
            IbeaProperty.log.info("�ϲ�������������!");
            if (SearchContext.getXdtechsite().getSudis())
                SearchContext.merger();
            else{
                Fetcher fetcher = null;
                try {
                    fetcher = new Fetcher(NutchFileSystem.get());
                } catch (IOException ex) {
                    IbeaProperty.log.info("�ϲ������������쳣!["+ex.getMessage()+"]");
                }
            }


            IbeaProperty.log.info("�ϲ������������!");

    }

    public static void main(String[] argv){
            IndexTask d = new IndexTask();
            d.doTask();
    }
}
