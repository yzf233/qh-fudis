package com.xx.platform.core.nutch;

import java.io.IOException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author quhuan
 * @version 1.0
 */
public interface IndexInterface {
    public boolean deleteDocuments(int ino,int docno) throws IOException;
    public int deleteDocuments(String field,String value) throws IOException;
    public void deleteDocuments(org.apache.nutch.searcher.Query query) throws IOException;
}
