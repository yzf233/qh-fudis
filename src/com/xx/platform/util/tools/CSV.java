package com.xx.platform.util.tools;

import java.io.*;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import java.util.*;

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
public class CSV {
    public static void main(String[] args) {
        try {
            File file = new File("C:\\doc\\国际期货市场行情.txt");
            FileInputStream input = new FileInputStream(file);
            byte[] fileC = new byte[(int) file.length()];
            input.read(fileC);
            List<Map> valueList = parse(fileC);
            for(Map value:valueList)
            {
                for(int i=0 ; i<value.size() ; i++)
                {
                    if(i>0)
                        System.out.print(",");
                    System.out.print(value.get(i));
                }
                System.out.println();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<Map> parse(byte[] content) throws Exception {
        StringReader csvFile = new StringReader(new String(content).replaceAll(
                "Copyright [\\S\\s]*?All Rights Reserved", ""));
        org.apache.commons.csv.CSVParser parse = new CSVParser(csvFile,
                CSVStrategy.SPACE_STRATEGY);
        String[][] allValues = parse.getAllValues();
        Map<Integer, Integer> colNumMap = new TreeMap();
        List<Map> valueList = new ArrayList();
        Integer maxValue = 0, maxValueColNum = 0, maxColNum = 0;
        for (String[] values : allValues) {
            int sl = 0;
            for (String value : values) {
                if (value != null && !value.equals("")) {
                    sl += 1;
                }
            }
            if (sl == 0)
                continue;
            Integer value = (colNumMap.get(sl) != null ?
                             colNumMap.put(sl, (colNumMap.get(sl) + 1)) :
                             colNumMap.put(sl, 1));
            if (value != null && value > maxValue) {
                maxValue = colNumMap.get(sl);
                maxValueColNum = sl;
            }
            if (sl > maxColNum) {
                maxColNum = sl;
            }
        }
        //System.out.println(colNumMap+" maxValue="+maxValue +" maxValueColNum="+maxValueColNum +" maxColNum="+maxColNum+" maxColNumValue="+maxColNumValue);
        for (String[] values : allValues) {
            Map retValue = new HashMap(); //临时返回值
            int sl = 0, tempSl = 0, spaceNum = 0;
            for (String value : values) {
                if (value != null && !value.equals("")) {
                    tempSl += 1;
                }
            }
            for (String value : values) {
                if (value != null && !value.equals("")) {
                    sl += 1;
                    int avaMaxValue = values.length / maxColNum,
                            avaMaxCol = values.length / maxValueColNum;
                    if (tempSl != maxColNum &&
                        ((spaceNum > 2 * avaMaxValue &&
                          spaceNum < 4 * avaMaxValue) ||
                         (spaceNum > 2 * avaMaxCol && spaceNum < 4 * avaMaxCol))) {
                        retValue.put(retValue.size(), "");
                    }
                    retValue.put(retValue.size(), value);
                    spaceNum = 0;
                } else {
                    spaceNum++;
                }
            }
            if (retValue.size() != 0)
                valueList.add(retValue);
        }
        return valueList ;
    }
}
