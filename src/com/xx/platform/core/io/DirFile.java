package com.xx.platform.core.io;

import org.apache.nutch.io.LongWritable;
import java.io.File;
import org.apache.nutch.fs.NutchFileSystem;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.io.VHashMap.Entry;

import java.io.IOException;
import java.util.logging.Logger;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.io.MD5Hash;

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
public class DirFile {
    public static final Logger LOG =
      LogFormatter.getLogger("com.xx.platform.core.io.DirFile");
    private static File dbFile = new File(SearchContext.getXdtechsite().
                                          getSearchdir(), "filedb");
    private static boolean isReloading = false;

    private static String mapDir = dbFile.toString();
    private NutchFileSystem nfs;

    private static VHashMap windexMap = new VHashMap(4000000, 0.5f);
    private SequenceFile.Reader windexReader;
    private static long size;

    public DirFile() throws IOException {
        this.nfs = NutchFileSystem.getXDFS();;
        if(!isReloading)
            init();
    }


    public boolean addContents(String content) throws Exception {
        if (get(MD5Hash.digest(content).getDigest()) == null) {
            synchronized (windexMap) {
                windexMap.put(MD5Hash.digest(content).getDigest(),size);
//                size = windexMap.size();
                size++;
            }
            return true;
        } else
            return false;
    }

    private Object get(byte[] key) throws Exception {
        return (Object) windexMap.getB(key);
    }

    private static void writeMap() throws Exception {
        NutchFileSystem nfs = NutchFileSystem.get();
        File dataFile = new File(mapDir, MapFile.MAP_INDEX_DATA_FILE_NAME);
        WebDbIndex webIndex = new WebDbIndex();
        org.apache.nutch.io.LongWritable key = new LongWritable(0);
        SequenceFile.Writer windexWriter = new SequenceFile.Writer(nfs,
                dataFile.getPath(), LongWritable.class, WebDbIndex.class, true);
        for (Entry e : windexMap.table) {
            while (e != null) {
                webIndex.setHash((byte[]) e.getKey());
                webIndex.setKey(((Long) e.getValue()).longValue());
                windexWriter.append(key, webIndex);
                key.set(key.get() + 1);
                e = e.next;
            }
        }
        windexWriter.close();
    }

    private void init() {
        try {
            if (!dbFile.exists())
                dbFile.mkdir();

            File dataFile = new File(mapDir, MapFile.MAP_INDEX_DATA_FILE_NAME);
            if (dataFile.exists()) {
                org.apache.nutch.io.LongWritable key = new LongWritable(0);
                windexReader = new SequenceFile.Reader(nfs, dataFile.getPath());
                WebDbIndex webIndex = new WebDbIndex();
                while (windexReader.next(key, webIndex)) {
                    windexMap.put(webIndex.getHash(), webIndex.getKey());
                    key.set(key.get() + 1);
                    webIndex = new WebDbIndex();
                }
                windexReader.close();
                size = windexMap.size();
                isReloading = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.info(ex.getMessage());
        }
    }

    static {
    {
      {
        Thread hookThread = new Thread(new Runnable() {

          public void run() {
            LOG.info("--回写目录信息");
            try {
                //if(isReloading){
                    writeMap();
                     LOG.info("--回写目录信息完成");
                //}
            }
            catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
        Runtime.getRuntime().addShutdownHook(hookThread);
      }
    }
  }

}
