package com.xx.platform.util.tools.blob;

import java.io.IOException;
import java.io.InputStream;

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
public class TxtParserTool  implements ParserBlobTool{
    public String extract(InputStream in){
      StringBuffer strb = new StringBuffer() ;
      try {
        byte[] data = new byte[1024];
        int length = 0 ;

        while((length = in.read(data))>0)
        {
          strb.append(new String(data , 0 , length)) ;
        }
      }catch (IOException ex) {}
      return  strb.toString();
    }
}
