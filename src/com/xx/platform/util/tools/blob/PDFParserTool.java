package com.xx.platform.util.tools.blob;

import java.io.InputStream;

import org.pdfbox.encryption.DocumentEncryption;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;


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
public class PDFParserTool
    implements ParserBlobTool {
  private static final String PDF_HEADER = "%PDF-";
  
  private static int wordlength = 3;// private int threadCount = // max number of threads
     
  public String extract(InputStream in) {
	  long l = System.currentTimeMillis();
    String text;
	try {
		text = parse(in);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		text = "";
	}
    System.out.println(System.currentTimeMillis()-l);
    System.out.println(text);
    System.out.println("_____________________________________");
//    TestLog.log(text);
    String rt = filterText(text);
//    TestLog.log(rt+"------");
    float radio = (float)(text.length()-rt.length())/text.length();
//    TestLog.log(text.length()+" "+rt.length()+" "+" "+(text.length()-rt.length())+" "+(radio)+"  =="+wordlength);
//   
    if(radio>=0.5){
    	return rt;
    }
    return text;
  }

  public String parse(InputStream input) throws Exception {
      PDDocument pdf = null;
      try{
          StringBuffer strb = new StringBuffer();
          PDFParser parser = new PDFParser(input);
          parser.parse();

          pdf = parser.getPDDocument();

          if (pdf.isEncrypted()) {
              DocumentEncryption decryptor = new DocumentEncryption(pdf);
              //Just try using the default password and move on
              decryptor.decryptDocument("");
          }
//          System.out.println("------" +Thread.currentThread().getId());
          
          // collect text
          PDFTextStripper stripper = new PDFTextStripper();
          String text = stripper.getText(pdf);
//          StringWriter sw = new StringWriter();
//          stripper.writeText(pdf, sw)
          strb.append(text).append(" ");
          // collect title
          PDDocumentInformation info = pdf.getDocumentInformation();
          String title = info.getTitle();
          strb.append(title).append(" ");
          return strb.toString();
      }finally{
          if(pdf!=null)
              pdf.close();
      }
  }

  public static boolean ispdf(byte[] headerline) {
    boolean pdf = false;
    StringBuffer buffer = new StringBuffer(11);
    for (byte b : headerline) {
      int c = b & 0xff;
      if (isWhitespace(c) && c != -1)
        continue;
      if (!isEOL(c) && c != -1)
        buffer.append( (char) c);
    }
    String header = buffer.toString();
    if (header.length() < PDF_HEADER.length() + 1) {
      return pdf;
    }
    int headerStart = header.indexOf(PDF_HEADER);
    if (headerStart >= 0)
      pdf = true;
    return pdf;
  }

  private static boolean isEOL(int c) {
    return c == 10 || c == 13;
  }

  private static boolean isWhitespace(int c) {
    return c == 0 || c == 9 || c == 12 || c == 10
        || c == 13 || c == 32;
  }

  public static void main(String[] args) throws Exception {
    PDFParserTool tool = new PDFParserTool();
    long l = System.currentTimeMillis();
    System.out.println(tool.extract(new java.io.FileInputStream(new java.io.File("D:\\S28BW-412121011360PDF.PDF"))));
    System.out.println((System.currentTimeMillis()-l));
  }
  
  
  public String filterText(String text){
	  char[] temps = text.toCharArray();
	  StringBuffer sb = new StringBuffer();
//	  boolean pre = true;
//	  boolean now = false;
//	  boolean next = false;
	  StringBuffer temp = new StringBuffer();
	  for (int i = 0; i < temps.length; i++) {
		 int c = (int)temps[i];
//		 int d = (int)temps[i++];
//		 int e = (int)temps[i++];
		 if((c>=28&&c<=90)||(c>=97&&c<=122)||(c>=0x8140&&c<=0xfefe)||(c>=8&&c<=13)){
//			 sb.append((char)c);
			temp.append((char)c);
		 }else{
			 if(temp.length()>wordlength){
				 sb.append(temp.toString());
			 }
			 temp.setLength(0);
		 }
	}
	  return sb.toString();
  }
  
}

