/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xx.platform.core.io;

import java.io.*;
import org.apache.nutch.fs.*;
import org.apache.nutch.util.*;
import org.apache.nutch.fetcher.IndexFetcher;
import org.apache.nutch.io.*;

/** A dense file-based mapping from integers to values. */
public class ArrayFile
    extends MapFile {

  protected ArrayFile() {} // no public ctor

  public ArrayFile(NutchFileSystem nfs, String dirName) throws IOException {
    super(nfs, dirName);
  }
  public void init()
  {
    super.init();
  }
  public Object get(byte[] key) throws Exception {
    return super.get(key);
  }

  public void writeMap() throws Exception {
    super.writeMap();
  }

  public void rmKey(byte[] key) throws Exception {
    super.rmKey(key);
  }

  public void putValue(byte[] key, long docNum) throws Exception {
    super.putValue(key, docNum);
  }

  public int size() throws Exception {
    return super.getMap().size();
  }

  public void clear() throws Exception {
    super.getMap().clear();
  }

  /** Write a new array file. */
  public static class Writer
      extends MapFile.Writer {
    private LongWritable count = new LongWritable(MapFile.Writer.docNum);

    /** Create the named file for values of the named class. */
    public Writer(NutchFileSystem nfs, String file, Class valClass) throws
        IOException {
      super(nfs, file, LongWritable.class, valClass);
    }

    /** Append a value to the file. */
    public synchronized void append(Writable value) throws IOException {
      super.append(count, value); // add to map
      count.set(count.get() + 1); // increment count
    }

    public void flush() {

    }

    public void update(long key, Writable value) throws IOException {
      super.update(key, value);
    }
  }

  /** Provide access to an existing array file. */
  public static class Reader
      extends MapFile.Reader {
    private LongWritable key = new LongWritable();

    /** Construct an array reader for the named file.*/
    public Reader(NutchFileSystem nfs, String file) throws IOException {
      super(nfs, file);
    }

    /** Positions the reader before its <code>n</code>th value. */
    public synchronized void seek(long n) throws IOException {
      key.set(n);
      seek(key);
    }

    /** Read and return the next value in the file. */
    public synchronized Writable next(Writable value) throws IOException {
      return next(key, value) ? value : null;
    }

    /** Returns the key associated with the most recent call to {@link
     * #seek(long)}, {@link #next(Writable)}, or {@link
     * #get(long,Writable)}. */
    public synchronized long key() throws IOException {
      return key.get();
    }

    /** Return the <code>n</code>th value in the file. */
    public synchronized Writable get(long n, Writable value) throws IOException {
      key.set(n);
      return get(key, value);
    }
  }

}
