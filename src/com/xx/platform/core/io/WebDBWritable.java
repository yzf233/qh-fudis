package com.xx.platform.core.io;

import org.apache.nutch.io.Writable;

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
public interface WebDBWritable extends Writable{
    public void setIndex(long index) ;
    public long getIndex() ;
    public void setDocNo(int docNo) ;
    public int getDocNo() ;
}
