/*  Copyright 2004 Ryan Ackley
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xx.platform.util.tools.ms;

// JDK imports
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * This class extracts the text from a Word 6.0/95/97/2000/XP word doc
 *
 * @author Ryan Ackley
 * @author Andy Hedges
 * @author J&eacute;r&ocirc;me Charron
 *
 */
public class WordExtractor
    extends MSExtractor {
  private HWPFDocument doc;

  public WordExtractor() {}

  /**
   * Create a new Word Extractor
   * @param is InputStream containing the word file
   */
  public WordExtractor(InputStream is) throws IOException {
    this(HWPFDocument.verifyAndBuildPOIFS(is));
  }

  /**
   * Create a new Word Extractor
   * @param fs POIFSFileSystem containing the word file
   */
  public WordExtractor(POIFSFileSystem fs) throws IOException {
    this(new HWPFDocument(fs));
  }

  /**
   * Create a new Word Extractor
   * @param doc The HWPFDocument to extract from
   */
  public WordExtractor(HWPFDocument doc) throws IOException {
    this.doc = doc;
  }

  /**
   * Command line extractor, so people will stop moaning that
   *  they can't just run this.
   */
  public static void main(String[] args) throws IOException {
	  String path = "D:\\自动文摘和关键词抽取测试样本10+10\\10篇中文";
		File f = new File(path);
		File [] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			WordExtractor extractor = new WordExtractor(new FileInputStream(fs[i].getAbsolutePath()));
		    String t = extractor.getText();
		    String filepath = fs[i].getAbsolutePath().substring(0,fs[i].getAbsolutePath().lastIndexOf("."))+".txt";
		    System.out.println(filepath);
		    log(filepath, t);
		    
		}
//		if(args.length == 0) {
//			System.err.println("Use:");
//			System.err.println("   java org.apache.poi.hwpf.extractor.WordExtractor <filename>");
//			System.exit(1);
//		}

    // Process the first argument as a file
//    FileInputStream fin = new FileInputStream("f:\\广告.doc");
//    WordExtractor extractor = new WordExtractor(fin);
//    System.out.println(extractor.getText());
  }

  public static void log(String filepath,String str){
	  try{
		
		  BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath)));
		  bw.write(str);
		  bw.flush();
		  bw.close();
	  }catch (Exception e) {
		e.printStackTrace();
		// TODO: handle exception
	}
  }
  
  /**
   * Get the text from the word file, as an array with one String
   *  per paragraph
   */
  public String[] getParagraphText() {
    String[] ret;

    // Extract using the model code
    try {
      Range r = doc.getRange();

      ret = new String[r.numParagraphs()];
      for (int i = 0; i < ret.length; i++) {
        Paragraph p = r.getParagraph(i);
        ret[i] = p.text();

        // Fix the line ending
        if (ret[i].endsWith("\r")) {
          ret[i] = ret[i] + "\n";
        }
      }
    }
    catch (Exception e) {
      // Something's up with turning the text pieces into paragraphs
      // Fall back to ripping out the text pieces
      ret = new String[1];
      ret[0] = getTextFromPieces();
    }

    return ret;
  }

  /**
   * Grab the text out of the text pieces. Might also include various
   *  bits of crud, but will work in cases where the text piece -> paragraph
   *  mapping is broken. Fast too.
   */
  public String getTextFromPieces() {
    StringBuffer textBuf = new StringBuffer();

    Iterator textPieces = doc.getTextTable().getTextPieces().iterator();
    while (textPieces.hasNext()) {
      TextPiece piece = (TextPiece) textPieces.next();

      String encoding = "Cp1252";
      if (piece.isUnicode()) {
        encoding = "UTF-16LE";
      }
      try {
        String text = new String(piece.getRawBytes(), encoding);
        textBuf.append(text);
      }
      catch (UnsupportedEncodingException e) {
        throw new InternalError("Standard Encoding " + encoding +
                                " not found, JVM broken");
      }
    }

    String text = textBuf.toString();

    // Fix line endings (Note - won't get all of them
    text = text.replaceAll("\r\r\r", "\r\n\r\n\r\n");
    text = text.replaceAll("\r\r", "\r\n\r\n");

    if (text.endsWith("\r")) {
      text += "\n";
    }

    return text;
  }

  /**
   * Grab the text, based on the paragraphs. Shouldn't include any crud,
   *  but slightly slower than getTextFromPieces().
   */
  public String getText() {
    StringBuffer ret = new StringBuffer();
    String[] text = getParagraphText();
    for (int i = 0; i < text.length; i++) {
      ret.append(text[i]);
    }
    return ret.toString();
  }

  public String extractText(POIFSFileSystem poifs) throws Exception {
    this.doc = new HWPFDocument(poifs);
    return getText();
  }

}

