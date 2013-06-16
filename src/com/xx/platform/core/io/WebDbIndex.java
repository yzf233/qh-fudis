package com.xx.platform.core.io;

import java.io.*;

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
public class WebDbIndex implements org.apache.nutch.io.Writable{
  private byte[] hash = new byte[16];
  private long key = 0 ;
  public void write(DataOutput out) throws IOException {
    out.write(hash , 0 ,hash.length);
    out.writeLong(key);
  }

  public void readFields(DataInput in) throws IOException {
    in.readFully(hash);
    key = in.readLong() ;
  }

  public byte[] getHash() {
    return hash;
  }

  public long getKey() {
    return key;
  }

  public void setHash(byte[] hash) {
    this.hash = hash;
  }

  public void setKey(long key) {
    this.key = key;
  }

}
