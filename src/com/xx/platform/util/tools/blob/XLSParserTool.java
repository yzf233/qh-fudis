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
package com.xx.platform.util.tools.blob;

// JDK imports
import java.io.IOException;
import java.io.InputStream;

/**
 * This class extracts the text from a Word 6.0/95/97/2000/XP word doc
 *
 * @author Ryan Ackley
 * @author Andy Hedges
 * @author J&eacute;r&ocirc;me Charron
 *
 */
public class XLSParserTool implements ParserBlobTool {
  private InputStream input ;

  public XLSParserTool() {}

  /**
   * Create a new Word Extractor
   * @param is InputStream containing the word file
   */
  public XLSParserTool(InputStream input) throws IOException {
    this.input = input ;
  }


  public String extract(InputStream input) throws Exception{

//    return new ExcelExtrator().extractText(input) ;
	  return "";
  }

}

