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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;



/**
 * This class extracts the text from a Word 6.0/95/97/2000/XP word doc
 *
 * @author Ryan Ackley
 * @author Andy Hedges
 * @author J&eacute;r&ocirc;me Charron
 *
 */
public class ExcelExtrator extends MSExtractor{
  private InputStream input ;
  public ExcelExtrator()
  {}

  public ExcelExtrator(InputStream input)
  {
    this.input = input ;
  }
  public String getText() throws Exception
  {
    String text = getExtrator(input) ;
    return text!=null?text:"" ;
  }
  private String getExtrator(InputStream input) throws Exception
  {
    POIFSFileSystem poifs = new POIFSFileSystem(input);
    return extractText(poifs) ;
  }

  public String extractText(POIFSFileSystem poifs) throws Exception {
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();
    String text = null;
       try {
           HSSFWorkbook wb = new HSSFWorkbook(poifs, true);
           if (wb == null) {
               return null;
           }

           HSSFSheet sheet;
           HSSFRow row;
           HSSFCell cell;
           int sNum = 0;
           int rNum = 0;
           int cNum = 0;

           sNum = wb.getNumberOfSheets();
           for (int i = 0; i < sNum; i++) {
               if ((sheet = wb.getSheetAt(i)) == null) {
                   continue;
               }
               String[] key = null; //对应field
               boolean init = false; //key 是否初始化
               rNum = sheet.getLastRowNum();
               for (int j = 0; j <= rNum; j++) {
                   if ((row = sheet.getRow(j)) == null) {
                       continue;
                   }
                   Map<String, String> rowdata = new HashMap<String, String>();
                   cNum = row.getLastCellNum();
                   if(!init)
                       key = new String[cNum];
                   String value = "";
                   StringBuffer content = new StringBuffer();
                   for (int k = 0; k < cNum; k++) {
                       if ((cell = row.getCell((short) k)) != null) {
                           if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                               value = cell.getStringCellValue();
                           } else if (cell.getCellType() ==
                                      HSSFCell.CELL_TYPE_NUMERIC) {
                               Double d = new Double(cell.getNumericCellValue());
                               value = d.toString();
                           }else value="";
                           if(init){
                               content.append(value);
                           }else{
                               key[k] = value;
                           }
                       }
                   }
               }
           }
        }catch(Exception e){text="";}
        return text ;

  }
}

