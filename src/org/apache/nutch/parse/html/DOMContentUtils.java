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

package org.apache.nutch.parse.html;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.nutch.parse.Outlink;

import org.w3c.dom.*;
import java.util.regex.Pattern;
import java.util.Properties;

/**
 * A collection of methods for extracting content from DOM trees.
 *
 * This class holds a few utility methods for pulling content out of
 * DOM nodes, such as getOutlinks, getText, etc.
 *
 */
public class DOMContentUtils {

  public static class LinkParams {
    public String elName;
    public String attrName;
      public int childLen;

      public LinkParams(String elName, String attrName, int childLen) {
          this.elName = elName;
          this.attrName = attrName;
          this.childLen = childLen;
      }

      public String toString() {
          return "LP[el=" + elName + ",attr=" + attrName + ",len=" + childLen + "]";
      }
  }

  public static HashMap linkParams = new HashMap();

  static {
      linkParams.put("a", new LinkParams("a", "href", 1));
      linkParams.put("area", new LinkParams("area", "href", 0));
      linkParams.put("form", new LinkParams("form", "action", 1));
      linkParams.put("frame", new LinkParams("frame", "src", 0));
      linkParams.put("iframe", new LinkParams("iframe", "src", 0));
      linkParams.put("script", new LinkParams("script", "src", 0));
      linkParams.put("link", new LinkParams("link", "href", 0));
      linkParams.put("img", new LinkParams("img", "src", 0));
  }

  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node},
   * and will append all the content text found beneath the DOM node to
   * the <code>StringBuffer</code>.
   *
   * <p>
   *
   * If <code>abortOnNestedAnchors</code> is true, DOM traversal will
   * be aborted and the <code>StringBuffer</code> will not contain
   * any text encountered after a nested anchor is found.
   *
   * <p>
   *
   * @return true if nested anchors were found
   */
  public static final boolean getText(StringBuffer sb, Node node,
                                      boolean abortOnNestedAnchors) {
    if (getTextHelper(sb, node, abortOnNestedAnchors, 0)) {
      return true;
    }
    return false;
  }
  /**
     * This method takes a {@link StringBuffer} and a DOM {@link Node},
     * and will append all the content text found beneath the DOM node to
     * the <code>StringBuffer</code>.
     *
     * <p>
     *
     * If <code>abortOnNestedAnchors</code> is true, DOM traversal will
     * be aborted and the <code>StringBuffer</code> will not contain
     * any text encountered after a nested anchor is found.
     *
     * <p>
     *
     * @return true if nested anchors were found
     */
    public static final boolean getTextWithOutLink(StringBuffer sb,StringBuffer sbs, Node node,
                                        boolean abortOnNestedAnchors,Properties property) {
      if (getTextHelperWithOutLink(sb, sbs,node, abortOnNestedAnchors, 0,property)) {
        return true;
      }
      return false;
  }

  /**
   * This is a convinience method, equivalent to {@link
   * #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
   *
   */
  public static final void getText(StringBuffer sb, Node node) {
    getText(sb, node, false);
  }
  /**
     * This is a convinience method, equivalent to {@link
     * #getText(StringBuffer,Node,boolean) getText(sb, node, false)}.
     *
     */
    public static final void getTextWithOutLink(StringBuffer sb,StringBuffer sbs, Node node,Properties property ) {
      getTextWithOutLink(sb,sbs, node, false,property);
  }
  // returns true if abortOnNestedAnchors is true and we find nested
  // anchors
  private static final boolean getTextHelperWithOutLink(StringBuffer sb, StringBuffer sbs ,Node node,
                                             boolean abortOnNestedAnchors,
                                             int anchorDepth,Properties property) {
    if ("script".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if ("style".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if ("a".equalsIgnoreCase(node.getNodeName())) {
      return false;
    }
    if ("title".equalsIgnoreCase(node.getNodeName())) {
          return false;
    }
//    if ("font".equalsIgnoreCase(node.getNodeName())) {
//      return false;
//    }

    if (abortOnNestedAnchors && "a".equalsIgnoreCase(node.getNodeName())) {
      anchorDepth++;
      if (anchorDepth > 1)
        return true;
    }
    if (node.getNodeType() == Node.COMMENT_NODE) {
      return false;
    }
    if (node.getNodeType() == Node.TEXT_NODE) {

      // cleanup and trim the value
      String text = node.getNodeValue();
      {
        {
          java.util.regex.Pattern pattern = Pattern.compile(
              "([\\d]{4}[-|年|/][\\d]{1,2}[-|月|/][\\d]{1,2}[日|号| |　]{1,}[\\d]{1,}[:时点：][\\d]{1,}|[\\d]{4}[-|年|/][\\d]{1,2}[-|月|/][\\d]{1,2})",
              Pattern.CASE_INSENSITIVE);
          java.util.regex.Matcher matcher = pattern.matcher(text);
          if (property != null && property.get("idate") == null && matcher.find() &&
              matcher.groupCount() >= 1) {
            String idate = matcher.group(1).replaceAll("[-|年|月|/]", "-").
                replaceAll("[日|号| ]{1,}", " ").replaceAll("[:时点：]", ":");
            if (idate.matches("[\\d]{4}-[\\d]{1,2}-[\\d]{1,2}"))
              idate = idate + " 00:00";
            property.put("idate", idate);
          }
        }
        {
          java.util.regex.Pattern pattern = Pattern.compile(
              "([\\d]{6})");
          java.util.regex.Matcher matcher = pattern.matcher(text);
          if(matcher.find()&&matcher.groupCount()>=1&&property!=null && matcher.group(1).compareTo("700000")<0)
          {
            String gupiao = (String)property.get("gupiao") ;

            if(gupiao==null)
              property.put("gupiao",matcher.group(1)) ;
            else
            {
              gupiao += matcher.group(1);
            }
          }
        }
      }
      text = text.replaceAll("\\s+", "");
      text = text.replaceAll("[ 【\\|>-】]|<[\\S\\s]*?>", "");
      text = text.trim();

      if(text.length()<60)
      {
//        if((text.length()>=2 && text.length()<=10))
//          sbs.append(text).append(' ') ;
        return false;
      }

      java.util.regex.Pattern pattern = Pattern.compile("<[\\S\\s]*?>",Pattern.CASE_INSENSITIVE) ;
      java.util.regex.Matcher matcher = pattern.matcher(text) ;
      if(matcher.find())
      {
        /**
         * 文本链接密度计算 标签密度=1000*标签数/字数
         */
        int linkMIDU = matcher.groupCount()*1000 / text.length() ;
        if(linkMIDU>50) //链接密度 大于 100
          return false ;
      }

      if (text.length() > 0) {
        if (sb.length() > 0) sb.append(' ');
      	sb.append(text);
      }
    }
    boolean abort = false;
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getTextHelperWithOutLink(sb , sbs, children.item(i),
                          abortOnNestedAnchors, anchorDepth,property)) {
          abort = true;
          break;
        }
      }
    }
    return abort;
  }
  // returns true if abortOnNestedAnchors is true and we find nested
    // anchors
    private static final boolean getTextHelper(StringBuffer sb, Node node,
                                               boolean abortOnNestedAnchors,
                                               int anchorDepth) {
      if ("script".equalsIgnoreCase(node.getNodeName())) {
        return false;
      }
      if ("style".equalsIgnoreCase(node.getNodeName())) {
        return false;
      }
      if (abortOnNestedAnchors && "a".equalsIgnoreCase(node.getNodeName())) {
        anchorDepth++;
        if (anchorDepth > 1)
          return true;
      }
      if (node.getNodeType() == Node.COMMENT_NODE) {
        return false;
      }
      if (node.getNodeType() == Node.TEXT_NODE) {
        // cleanup and trim the value
        String text = node.getNodeValue();
        text = text.replaceAll("\\s+", " ");
        text = text.trim();
        if (text.length() > 0) {
          if (sb.length() > 0) sb.append(' ');
          sb.append(text);
        }
      }
      boolean abort = false;
      NodeList children = node.getChildNodes();
      if (children != null) {
        int len = children.getLength();
        for (int i = 0; i < len; i++) {
          if (getTextHelper(sb, children.item(i),
                            abortOnNestedAnchors, anchorDepth)) {
            abort = true;
            break;
          }
        }
      }
      return abort;
  }
  /**
   * This method takes a {@link StringBuffer} and a DOM {@link Node},
   * and will append the content text found beneath the first
   * <code>title</code> node to the <code>StringBuffer</code>.
   *
   * @return true if a title node was found, false otherwise
   */
  public static final boolean getTitle(StringBuffer sb, Node node) {
    if ("body".equalsIgnoreCase(node.getNodeName())) // stop after HEAD
      return false;

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      if ("title".equalsIgnoreCase(node.getNodeName())) {
        getText(sb, node);
        return true;
      }
    }
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        if (getTitle(sb, children.item(i))) {
          return true;
        }
      }
    }
    return false;
  }

  /** If Node contains a BASE tag then it's HREF is returned. */
  public static final URL getBase(Node node) {

    // is this node a BASE tag?
    if (node.getNodeType() == Node.ELEMENT_NODE) {

      if ("body".equalsIgnoreCase(node.getNodeName())) // stop after HEAD
        return null;


      if ("base".equalsIgnoreCase(node.getNodeName())) {
        NamedNodeMap attrs = node.getAttributes();
        for (int i= 0; i < attrs.getLength(); i++ ) {
          Node attr = attrs.item(i);
          if ("href".equalsIgnoreCase(attr.getNodeName())) {
            try {
              return new URL(attr.getNodeValue());
            } catch (MalformedURLException e) {}
          }
        }
      }
    }

    // does it contain a base tag?
    NodeList children = node.getChildNodes();
    if (children != null) {
      int len = children.getLength();
      for (int i = 0; i < len; i++) {
        URL base = getBase(children.item(i));
        if (base != null)
          return base;
      }
    }

    // no.
    return null;
  }


  private static boolean hasOnlyWhiteSpace(Node node) {
    String val= node.getNodeValue();
    for (int i= 0; i < val.length(); i++) {
      if (!Character.isWhitespace(val.charAt(i)))
        return false;
    }
    return true;
  }

  // this only covers a few cases of empty links that are symptomatic
  // of nekohtml's DOM-fixup process...
  private static boolean shouldThrowAwayLink(Node node, NodeList children,
                                              int childLen, LinkParams params) {
    if (childLen == 0) {
      // this has no inner structure
      if (params.childLen == 0) return false;
      else return true;
    } else if ((childLen == 1)
               && (children.item(0).getNodeType() == Node.ELEMENT_NODE)
               && (params.elName.equalsIgnoreCase(children.item(0).getNodeName()))) {
      // single nested link
      return true;

    } else if (childLen == 2) {

      Node c0= children.item(0);
      Node c1= children.item(1);

      if ((c0.getNodeType() == Node.ELEMENT_NODE)
          && (params.elName.equalsIgnoreCase(c0.getNodeName()))
          && (c1.getNodeType() == Node.TEXT_NODE)
          && hasOnlyWhiteSpace(c1) ) {
        // single link followed by whitespace node
        return true;
      }

      if ((c1.getNodeType() == Node.ELEMENT_NODE)
          && (params.elName.equalsIgnoreCase(c1.getNodeName()))
          && (c0.getNodeType() == Node.TEXT_NODE)
          && hasOnlyWhiteSpace(c0) ) {
        // whitespace node followed by single link
        return true;
      }

    } else if (childLen == 3) {
      Node c0= children.item(0);
      Node c1= children.item(1);
      Node c2= children.item(2);

      if ((c1.getNodeType() == Node.ELEMENT_NODE)
          && (params.elName.equalsIgnoreCase(c1.getNodeName()))
          && (c0.getNodeType() == Node.TEXT_NODE)
          && (c2.getNodeType() == Node.TEXT_NODE)
          && hasOnlyWhiteSpace(c0)
          && hasOnlyWhiteSpace(c2) ) {
        // single link surrounded by whitespace nodes
        return true;
      }
    }

    return false;
  }

  /**
   * This method finds all anchors below the supplied DOM
   * <code>node</code>, and creates appropriate {@link Outlink}
   * records for each (relative to the supplied <code>base</code>
   * URL), and adds them to the <code>outlinks</code> {@link
   * ArrayList}.
   *
   * <p>
   *
   * Links without inner structure (tags, text, etc) are discarded, as
   * are links which contain only single nested links and empty text
   * nodes (this is a common DOM-fixup artifact, at least with
   * nekohtml).
   */
  public static final void getOutlinks(URL base, ArrayList outlinks,
                                       Node node) {

    NodeList children = node.getChildNodes();
    int childLen= 0;
    if (children != null)
      childLen= children.getLength();

    if (node.getNodeType() == Node.ELEMENT_NODE) {
      LinkParams params = (LinkParams)linkParams.get(node.getNodeName().toLowerCase());
      if (params != null) {
        if (!shouldThrowAwayLink(node, children, childLen, params)) {

          StringBuffer linkText = new StringBuffer();
          getText(linkText, node, true);

          NamedNodeMap attrs = node.getAttributes();
          String target = null;
          boolean noFollow = false;
          boolean post = false;
          for (int i= 0; i < attrs.getLength(); i++ ) {
            Node attr = attrs.item(i);
            String attrName = attr.getNodeName();
            if (params.attrName.equalsIgnoreCase(attrName)) {
              target = attr.getNodeValue();
            } else if ("rel".equalsIgnoreCase(attrName) &&
                       "nofollow".equalsIgnoreCase(attr.getNodeValue())) {
              noFollow = true;
            } else if ("method".equalsIgnoreCase(attrName) &&
                       "post".equalsIgnoreCase(attr.getNodeValue())) {
              post = true;
            }
          }
          if (target != null && !noFollow && !post)
            try {
              URL url = new URL(base, target);
              outlinks.add(new Outlink(url.toString(),
                                       linkText.toString().trim()));
            } catch (MalformedURLException e) {
              // don't care
            }
        }
        // this should not have any children, skip them
        if (params.childLen == 0) return;
      }
    }
    for ( int i = 0; i < childLen; i++ ) {
      getOutlinks(base, outlinks, children.item(i));
    }
  }

}

