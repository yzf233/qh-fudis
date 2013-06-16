package com.xx.platform.util.tools;

import java.io.*;
import java.util.*;

import org.jaxen.*;
import org.jaxen.jdom.*;
import org.jdom.*;
import org.jdom.input.*;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexField;
import com.xx.platform.core.nutch.IndexFieldImpl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class XML {
  public static void main(String[] args) {
    XML parse = new XML();
    try {
      List list = parse.parse((new File(
          "C:\\Users\\Administrator\\Desktop\\Temp\\9529E245BF563F3B482573FC002BFC4D.xml")));
      System.out.println(list.size()>0?list.get(0):"");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static List parse(File file) {
    List recList = new ArrayList();
    try{
      org.jdom.Document xmlDoc = new org.jdom.Document();
      SAXBuilder builder = new SAXBuilder();
      builder.setValidation(false);
      xmlDoc = builder.build(new FileInputStream(file));

      Map recMap = new HashMap();
      recMap.put("docType", "DOMINOXML");
      recMap.put("docSource",
                  file.getAbsolutePath() != null ? file.getAbsolutePath() : "");
      recMap.put("title", file.getName());
      recMap.put("content", "");

      extractContent(xmlDoc, getIndexField("DOCUMENTGUID"),
                     "/package/attribute/id/values/value", recMap);
      extractContent(xmlDoc, getIndexField("ARCHIVENO"),
                     "/package/attribute/archiveNo/values/value", recMap);
      extractContent(xmlDoc, getIndexField("FILENO"),
                     "//package/attribute/fileno/values/value",
                     recMap);
      extractContent(xmlDoc, getIndexField("DOCUMENTTITLE"),
                     "/package/attribute/title/values/value", recMap);
      extractContent(xmlDoc, getIndexField("DOC_RIGHTS"),
                     "/package/attribute/docreader/values/value", recMap);
      extractContent(xmlDoc, getIndexField("AUTHOR1"),
                     "/package/attribute/author1/values/value", recMap);
      extractContent(xmlDoc, getIndexField("WRITEDATE"),
                     "/package/attribute/write_date/values/value", recMap);
      extractContent(xmlDoc, getIndexField("ARCHIVEDEPTCODE"),
                     "/package/attribute/archiveDeptCode/values/value", recMap);
      extractContent(xmlDoc, getIndexField("DOC_ATTACH"),
                     "//package/blocks/i/row", recMap);
      recList.add(recMap);
    }catch(Exception ex){ex.printStackTrace();}
    return recList;
  }

  public static void extractContent(
      Document xmlDoc, String name, String xpath, Map metadata) {
    try {
      JDOMXPath xp = new JDOMXPath(xpath);
      List selectNodes = xp.selectNodes(xmlDoc);
      Iterator nodes = selectNodes.iterator();
      while (nodes.hasNext()) {
        Object node = nodes.next();
        if (node instanceof Element) {
          Element elem = (Element) node;
          if (elem.getName().equals("property"))
            continue;
          metadata.put(name, elem.getValue()!=null?elem.getValue():"");
        }
        else if (node instanceof Attribute) {
          Attribute att = (Attribute) node;
          metadata.put(name, att.getValue()!=null?att.getValue():"");
        }
        else if (node instanceof Text) {
          Text text = (Text) node;
          metadata.put(name, text.getText()!=null?text.getText():"");
        }
        else if (node instanceof Comment) {
          Comment com = (Comment) node;
          metadata.put(name, com.getText()!=null?com.getText():"");
        }

      }
    }
    catch (JaxenException e) {

    }

  }

  private static String getIndexField(String code) throws Exception {
    String indexField = null;
    for (IndexField index : SearchContext.getIndexFieldSet()) {
      if (index.getCode().equalsIgnoreCase(code)) {
        indexField = ( (IndexFieldImpl) index).getId();
        break;
      }
    }
    return indexField!=null?indexField:"";
  }
}
