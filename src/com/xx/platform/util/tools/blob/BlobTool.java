package com.xx.platform.util.tools.blob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.logging.Logger;

import org.apache.nutch.util.LogFormatter;

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
public class BlobTool {
  private ParserBlobTool tool;
  private InputStream in;
  public static final Logger LOG =
      LogFormatter.getLogger("com.xx.platform.util.tools.blob.BlobTool");

  public BlobTool(InputStream input, String fileName) {
    this.in = input ;
    if (fileName != null && !fileName.equals("")) {
      if (fileName.matches("[\\s\\S]*.[pdf|PDF|Pdf|PDf|PdF|pdF]"))
        tool = new PDFParserTool();
      else if (fileName.matches("[\\s\\S]*.[doc|DOC|Doc|DOc|DoC|doC]")) {
        tool = new MSParserTool();
      }
      else if (fileName.matches("[\\s\\S]*.[xls|XLS|Xls|XLs|XlS|xlS]")) {
        tool = new XLSParserTool();
      }
      else if (fileName.matches("[\\s\\S]*.[txt|TXT|Txt|TXt|TxT|txT]")) {
        tool = new TxtParserTool();
      }
      else if (fileName.matches("[\\s\\S]*.[rtf|RTF|Rtf|RTf|RtF|rtF]")) {
        tool = new TxtParserTool();
      }

    }else {
      PushbackInputStream pin = new PushbackInputStream(input, 50);
      byte[] header = new byte[10];
      try {
        int len = pin.read(header, 0, 10);
        if (len != -1) {
          if (PDFParserTool.ispdf(header))
            tool = new PDFParserTool();
          else
            tool = new MSParserTool();

          pin.unread(header, 0, len);
          in = pin;

        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public BlobTool(InputStream input, byte[] header) {
    in = input;
    if (PDFParserTool.ispdf(header))
      tool = new PDFParserTool();
    else
      tool = new MSParserTool();
  }

  public String extract() {
	  
    String text = "" ;
    if (tool == null)
      return "";
    try {
      text = tool.extract(in);
      
//      char[] a = text.toCharArray();
//      java.io.FileOutputStream fos = null;
//      try{
//    	  fos = new java.io.FileOutputStream("d:\\aa.txt");
//    	  java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.OutputStreamWriter(fos));
////    	  bw.write(a);
//    	  bw.write("\n\n\n\n\n\n\n");
//    	  for(char tmpe:a){
//    		  bw.write(String.valueOf((int)tmpe)+"  ");
//    	  }
//      }catch(Exception ex){
//    	  
//      }finally{
//    	  if(fos != null) fos.close();
//      }
    }catch (Exception ex) {
      ex.getMessage() ;
    }
    return text;
  }

  public static void main(String[] args) throws FileNotFoundException {
    BlobTool bt = new BlobTool(new java.io.FileInputStream(
        "F:\\新建文件夹\\问题\\流程测试－－胡俊.xls"), "");
    System.out.println(bt.extract());
  }
}
