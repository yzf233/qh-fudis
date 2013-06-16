package com.xx.platform.util.tools.ms;

import javax.swing.text.DefaultStyledDocument;
import java.io.InputStream;
import javax.swing.text.rtf.RTFEditorKit;

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
public class RTFExtractor {
  public String parse(InputStream input) throws Exception
  {
    DefaultStyledDocument styledDoc = new DefaultStyledDocument();
    new RTFEditorKit().read(input, styledDoc, 0);
    String text = new String(styledDoc.getText(0, styledDoc.getLength()).getBytes("ISO8859_1"));    //提取文本
    if(input!=null)
      input.close();
    return text ;
  }
}
