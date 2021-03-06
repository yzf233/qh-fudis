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

import java.util.Properties;
import java.util.ArrayList;
import java.util.logging.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.util.regex.*;

import org.cyberneko.html.parsers.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.*;
import org.apache.html.dom.*;

import org.apache.nutch.protocol.Content;
import org.apache.nutch.util.*;
import org.apache.nutch.parse.*;
import org.apache.lucene.document.Field;


public class HtmlParser implements Parser {
  public static final Logger LOG =
    LogFormatter.getLogger("org.apache.nutch.parse.html");

  // I used 1000 bytes at first, but  found that some documents have
  // meta tag well past the first 1000 bytes.
  // (e.g. http://cn.promo.yahoo.com/customcare/music.html)
  private static final int CHUNK_SIZE = 2000;
  private static Pattern metaPattern =
    Pattern.compile("<meta\\s+([^>]*http-equiv=[\"' ]{0,}?content-type[\"' ]{0,}?[^>]*)>",
                    Pattern.CASE_INSENSITIVE);
  private static Pattern charsetPattern =
    Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)",
                    Pattern.CASE_INSENSITIVE);

  private static String parserImpl = NutchConf.get().get("parser.html.impl", "neko");

  /**
   * Given a <code>byte[]</code> representing an html file of an
   * <em>unknown</em> encoding,  read out 'charset' parameter in the meta tag
   * from the first <code>CHUNK_SIZE</code> bytes.
   * If there's no meta tag for Content-Type or no charset is specified,
   * <code>null</code> is returned.  <br />
   * FIXME: non-byte oriented character encodings (UTF-16, UTF-32)
   * can't be handled with this.
   * We need to do something similar to what's done by mozilla
   * (http://lxr.mozilla.org/seamonkey/source/parser/htmlparser/src/nsParser.cpp#1993).
   * See also http://www.w3.org/TR/REC-xml/#sec-guessing
   * <br />
   *
   * @param content <code>byte[]</code> representation of an html file
   */

  private static String sniffCharacterEncoding(byte[] content) {
    int length = content.length < CHUNK_SIZE ?
                 content.length : CHUNK_SIZE;

    // We don't care about non-ASCII parts so that it's sufficient
    // to just inflate each byte to a 16-bit value by padding.
    // For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
    // {U+0041, U+0082, U+00B7}.
    String str = new String(content, 0, 0, length);

    Matcher metaMatcher = metaPattern.matcher(str);
    String encoding = null;
    if (metaMatcher.find()) {
      Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
      if (charsetMatcher.find())
        encoding = new String(charsetMatcher.group(1));
    }

    return encoding;
  }


  private static String defaultCharEncoding =
    NutchConf.get().get("parser.character.encoding.default", "windows-1252");

  public Parse getParse(Content content) {
    HTMLMetaTags metaTags = new HTMLMetaTags();

    URL base;
    try {
      base = new URL(content.getBaseUrl());
    } catch (MalformedURLException e) {
      return new ParseStatus(e).getEmptyParse();
    }

    String text = "";
    String title = "";
    Outlink[] outlinks = new Outlink[0];
    Properties metadata = new Properties();

    // check that contentType is one we can handle
    String contentType = content.getContentType();
    if (!"".equals(contentType) && !contentType.startsWith("text/html"))
      return new ParseStatus(ParseStatus.FAILED, ParseStatus.FAILED_INVALID_FORMAT,
              "Content-Type not text/html: " + contentType).getEmptyParse();

    // parse the content
    DocumentFragment root;
    try {
      byte[] contentInOctets = content.getContent();
      InputSource input = new InputSource(new ByteArrayInputStream(contentInOctets));
      String encoding = StringUtil.parseCharacterEncoding(contentType);
      if ((encoding != null) && !("".equals(encoding))) {
        metadata.put("OriginalCharEncoding", encoding);
        if ((encoding = StringUtil.resolveEncodingAlias(encoding)) != null) {
          metadata.put("CharEncodingForConversion", encoding);
          LOG.fine(base + ": setting encoding to " + encoding);
        }
      }

      // sniff out 'charset' value from the beginning of a document
      if ((encoding == null) || ("".equals(encoding))) {
        encoding = sniffCharacterEncoding(contentInOctets);
        if (encoding!=null) {
          metadata.put("OriginalCharEncoding", encoding);
          if ((encoding = StringUtil.resolveEncodingAlias(encoding)) != null) {
            metadata.put("CharEncodingForConversion", encoding);
            LOG.fine(base + ": setting encoding to " + encoding);
          }
        }
      }

      if (encoding == null) {
        // fallback encoding.
        // FIXME : In addition to the global fallback value,
        // we should make it possible to specify fallback encodings for each ccTLD.
        // (e.g. se: windows-1252, kr: x-windows-949, cn: gb18030, tw: big5
        // doesn't work for jp because euc-jp and shift_jis have about the
        // same share)
        encoding = defaultCharEncoding;
        metadata.put("CharEncodingForConversion", defaultCharEncoding);
        LOG.fine(base + ": falling back to " + defaultCharEncoding);
      }
      input.setEncoding(encoding);
      LOG.fine("Parsing...");
      root = parse(input);
    } catch (IOException e) {
      return new ParseStatus(e).getEmptyParse();
    } catch (DOMException e) {
      return new ParseStatus(e).getEmptyParse();
    } catch (SAXException e) {
      return new ParseStatus(e).getEmptyParse();
    } catch (Exception e) {
      e.printStackTrace();
      return new ParseStatus(e).getEmptyParse();
    }

    // get meta directives
    HTMLMetaProcessor.getMetaTags(metaTags, root, base);
    LOG.fine("Meta tags for " + base + ": " + metaTags.toString());
    // check meta directives
    if (!metaTags.getNoIndex()) {               // okay to index
      StringBuffer sb = new StringBuffer();
      LOG.fine("Getting text...");
      Properties property = new Properties() ;
      StringBuffer sbs = new StringBuffer() ; //收集有价值的信息 ， 例如作者 等字数小于10个大于2的文本信息
      DOMContentUtils.getTextWithOutLink(sb , sbs, root ,property);          // extract text
      metadata.putAll(property);
//      System.out.println(sbs.toString());
      text = sb.toString();
      sb.setLength(0);
      LOG.fine("Getting title...");
      DOMContentUtils.getTitle(sb, root);         // extract title
      title = sb.toString().trim();
    }

    if (!metaTags.getNoFollow()) {              // okay to follow links
      ArrayList l = new ArrayList();              // extract outlinks
      URL baseTag = DOMContentUtils.getBase(root);
      LOG.fine("Getting links...");
      DOMContentUtils.getOutlinks(baseTag!=null?baseTag:base, l, root);
      outlinks = (Outlink[])l.toArray(new Outlink[l.size()]);
      LOG.fine("found "+outlinks.length+" outlinks in "+content.getUrl());
    }

    if (!metaTags.getNoCache()) {             // okay to cache
      // ??? FIXME ???
    }

    // copy content metadata through
    metadata.putAll(content.getMetadata());
    ParseStatus status = new ParseStatus(ParseStatus.SUCCESS);
    if (metaTags.getRefresh()) {
      status.setMinorCode(ParseStatus.SUCCESS_REDIRECT);
      status.setMessage(metaTags.getRefreshHref().toString());
    }
    ParseData parseData = new ParseData(status, title, outlinks, metadata);
    Parse parse = new ParseImpl(text, parseData);

    // run filters on parse
    return HtmlParseFilters.filter(content, parse, metaTags, root);
  }

  private DocumentFragment parse(InputSource input) throws Exception {
    if (parserImpl.equalsIgnoreCase("tagsoup"))
      return parseTagSoup(input);
    else return parseNeko(input);
  }

  private DocumentFragment parseTagSoup(InputSource input) throws Exception {
    HTMLDocumentImpl doc = new HTMLDocumentImpl();
    DocumentFragment frag = doc.createDocumentFragment();
    DOMBuilder builder = new DOMBuilder(doc, frag);
    org.ccil.cowan.tagsoup.Parser reader = new org.ccil.cowan.tagsoup.Parser();
    reader.setContentHandler(builder);
    reader.setFeature(reader.ignoreBogonsFeature, true);
    reader.setFeature(reader.bogonsEmptyFeature, false);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", builder);
    reader.parse(input);
    return frag;
  }

  private DocumentFragment parseNeko(InputSource input) throws Exception {
    DOMFragmentParser parser = new DOMFragmentParser();
    // some plugins, e.g., creativecommons, need to examine html comments
    try {
      parser.setFeature("http://apache.org/xml/features/include-comments",
              true);
      parser.setFeature("http://apache.org/xml/features/augmentations",
              true);
      parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
              false);
      parser.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",
              true);
      parser.setFeature("http://cyberneko.org/html/features/report-errors",
              true);
    } catch (SAXException e) {}
    // convert Document to DocumentFragment
    HTMLDocumentImpl doc = new HTMLDocumentImpl();
    doc.setErrorChecking(false);
    DocumentFragment res = doc.createDocumentFragment();
    DocumentFragment frag = doc.createDocumentFragment();
    parser.parse(input, frag);
    res.appendChild(frag);

    try {
      while(true) {
        frag = doc.createDocumentFragment();
        parser.parse(input, frag);
        if (!frag.hasChildNodes()) break;
        LOG.info(" - new frag, " + frag.getChildNodes().getLength() + " nodes.");
        res.appendChild(frag);
      }
    } catch (Exception x) { x.printStackTrace();};
    return res;
  }

  public static void main(String[] args) throws Exception {
    LOG.setLevel(Level.FINE);
    String name = "d:\\tt.html";
    String url = "file:"+name;
    File file = new File(name);
    byte[] bytes = new byte[(int)file.length()];
    DataInputStream in = new DataInputStream(new FileInputStream(file));
    in.readFully(bytes);
    Parse parse = new HtmlParser().getParse(new Content(url,url,
                                                        bytes,"text/html",
                                                        new Properties()));
//    String parseContent = new String(bytes) ;
//    java.util.regex.Pattern pattern = Pattern.compile(
//        "(出处|来自|来源|出自|转载|转自)(:|：|　| |\\r|\\n|\\t){0,}(<[\\S\\s]*?>([^<]*?)<[\\S\\s]*?>|([\\S\\s]*?)[<| ｜　|\\r|\\n|\\t])",
//        Pattern.CASE_INSENSITIVE);
//    java.util.regex.Matcher matcher = pattern.matcher(parseContent);
//    if (matcher.find() && matcher.groupCount() >= 4)
//      for (int i = 0; i < matcher.groupCount() ; i++) {
//        System.out.println("group "+i+" :"+matcher.group(i)) ;
//      }

    System.out.println("data: "+parse.getData());

    System.out.println("text: "+parse.getText());

  }
}
