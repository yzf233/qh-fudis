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

package org.apache.nutch.indexer;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

import org.apache.nutch.fs.*;
import org.apache.nutch.util.*;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;

import com.xx.platform.core.SearchContext;

import org.apache.nutch.fetcher.Fetcher;

/*************************************************************************
 * IndexMerger creates an index for the output corresponding to a
 * single fetcher run.
 *
 * @author Doug Cutting
 * @author Mike Cafarella
 *************************************************************************/
public class IndexMerger {
  public static final Logger LOG =
    LogFormatter.getLogger("org.apache.nutch.indexer.IndexMerger");

  public static final String DONE_NAME = "merge.done";

  private int MERGE_FACTOR = NutchConf.get().getInt("indexer.mergeFactor",
      IndexWriter.DEFAULT_MERGE_FACTOR);
  private int MIN_MERGE_DOCS = NutchConf.get().getInt("indexer.minMergeDocs",16);
  private int MAX_MERGE_DOCS = NutchConf.get().getInt("indexer.maxMergeDocs",
      IndexWriter.DEFAULT_MAX_MERGE_DOCS);
  private int TERM_INDEX_INTERVAL =
    NutchConf.get().getInt("indexer.termIndexInterval",
                           IndexWriter.DEFAULT_TERM_INDEX_INTERVAL);
  private NutchFileSystem nfs;
  private File outputIndex;
  private File localWorkingDir;
  private File[] segments;
  public static boolean isMergering = false ;
  /**
   * Merge all of the segments given
   */
  public IndexMerger(NutchFileSystem nfs, File[] segments, File outputIndex, File localWorkingDir) throws IOException {
      this.nfs = nfs;
      this.segments = segments;
      this.outputIndex = outputIndex;
      this.localWorkingDir = localWorkingDir;
  }

  /**
   * Load all input segment indices, then add to the single output index
   */
  public void merge() throws IOException {
    //
    // Open local copies of NFS indices
    //
    Directory[] dirs = new Directory[segments.length];
    File[] localSegments = new File[segments.length];
    for (int i = 0; i < segments.length; i++) {
        File tmpFile = new File(localWorkingDir, "indexmerge-" + new SimpleDateFormat("yyyMMddHHmmss").format(new Date(System.currentTimeMillis())));
        localSegments[i] = nfs.startLocalInput(new File(segments[i], "index"), tmpFile);
        dirs[i] = FSDirectory.getDirectory(localSegments[i], false);
    }

    //
    // Get local output target
    //
    File tmpLocalOutput = new File(localWorkingDir, "merge-output");
    File localOutput = nfs.startLocalOutput(outputIndex, tmpLocalOutput);

    //
    // Merge indices
    //
    IndexWriter writer = null;
    try{
        writer = new IndexWriter(localOutput, null,
                                             localOutput.exists() &&
                                             localOutput.isDirectory() ? false : true);
    }catch(IOException ex){
       LOG.info("索引目录被锁定，清除锁开始...");
       FSDirectory.getDirectory(localOutput).clearLock(IndexWriter.WRITE_LOCK_NAME);
       LOG.info("清除锁结束!");
       writer = new IndexWriter(localOutput, null,
                                             localOutput.exists() &&
                                             localOutput.isDirectory() ? false : true);

    }
    writer.setMergeFactor(SearchContext.getXdtechsite().getMergefactor());
    writer.setRAMBufferSizeMB(SearchContext.getXdtechsite().getMinmergedocs());
    writer.setMaxMergeDocs(MAX_MERGE_DOCS);
    writer.setTermIndexInterval(TERM_INDEX_INTERVAL);
    writer.setInfoStream(LogFormatter.getLogStream(LOG, Level.FINE));
    writer.setUseCompoundFile(false);
    writer.setSimilarity(new NutchSimilarity());
    writer.addIndexes(dirs);
    LOG.info("optimize indexing...");
    writer.optimize();
    LOG.info("done optimize");
    writer.close();

    //
    // Put target back
    //
    nfs.completeLocalOutput(outputIndex, tmpLocalOutput);

    //
    // Delete all local inputs, if necessary
    //
    for (int i = 0; i < localSegments.length; i++) {
        nfs.completeLocalInput(localSegments[i]);
    }
    localWorkingDir.delete();
  }

  /**
   * Create an index for the input files in the named directory.
   */
  public static void main(String[] args) throws Exception {
    String usage = "IndexMerger (-local | -ndfs <nameserver:port>) [-workingdir <workingdir>] outputIndex segments...";
    if (args.length < 2) {
      System.err.println("Usage: " + usage);
      return;
    }

    //
    // Parse args, read all segment directories to be processed
    //
    isMergering = true ;
    NutchFileSystem nfs = NutchFileSystem.parseArgs(args, 0);
    try {
        File workingDir = new File(new File("").getCanonicalPath());
        Vector segments = new Vector();

        int i = 0;
        if ("-workingdir".equals(args[i])) {
            i++;
            workingDir = new File(new File(args[i++]).getCanonicalPath());
        }
        File outputIndex = new File(args[i++]);

        for (; i < args.length; i++) {
            if (args[i] != null) {
                segments.add(new File(args[i]));
            }
        }
        workingDir = new File(workingDir, "indexmerger-workingdir");
        //System.out.println("workingDir："+workingDir.getAbsolutePath());
        //
        // Merge the indices
        //
        File[] segmentFiles = (File[]) segments.toArray(new File[segments.size()]);
        LOG.info("merging segment indexes to: " + outputIndex);

        if (workingDir.exists()) {
            FileUtil.fullyDelete(workingDir);
        }
        workingDir.mkdirs();
        IndexMerger merger = new IndexMerger(nfs, segmentFiles, outputIndex, workingDir);
        merger.merge();
        LOG.info("done merging");
        FileUtil.fullyDelete(workingDir);
        isMergering = false ;
        Fetcher.setDictory();
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
    finally {
        nfs.close();
    }
  }
}
