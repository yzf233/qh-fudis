package com.xx.platform.core.analyzer;

import java.util.*;
import java.io.*;
import org.apache.lucene.analysis.Token;



/**
 * <p>Title: </p>
 *
 * <p>Description: <b>注意,返回的vcode(集合)可能存在null</b></p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company:xd-tech </p>
 *
 * @author qh
 * @version 1.0
 */
public class HCvcodeTool{
    /**
     * 词库 Map ， 扩展支持词性标注和词频
     */
    private static Map<java.lang.Character, Map> stackMap;

    private Map<java.lang.Character, Map> pointMap = null;

    static {
        init();
    }
    /**
     * 初始化词库
     * @param keyword String
     */
    public static void init(String keyword) {
        Map<java.lang.Character, Map> pointMap = stackMap;
        String[] words = keyword.split("\t");
        if(words.length!=2) throw new RuntimeException("\"行业/产品\"文件格式不对!");
        keyword = words[1];
        for (int i = 0; i < keyword.length(); i++) {
            char key = keyword.charAt(i);
            if (key == ' ')
                continue;
            if (pointMap.containsKey(key)) {
                pointMap = pointMap.get(key);
                continue;
            } else {
                pointMap = pointMap.put(key,
                                        new VVHashMap(1, 1,
                        (i + 1 == keyword.length()) ? true : false, words[0]));
            }
        }
    }
    /**
     * vcode查找方法
     */
    private String getVcode(String input) {
        if (input == null || input.length() == 0)
            return null;
        if (input.matches("[A-Z]{1}[\\d]*"))
            return input;
        char currentchar;
        pointMap = null;
        for (int i = 0; i < input.length(); i++) {
            currentchar = input.charAt(i);
            //全角转半角
            if ((int) currentchar > 65281 && (int) currentchar < 65374) {
                currentchar = (char) ((int) currentchar - 65248);
            }
            pointMap = find(currentchar,i==0?stackMap:pointMap);//i==0,first c
            if(pointMap==null){return null;}
        }
        if(pointMap!=null){
            return ((VVHashMap)pointMap).getVcode();
        }
        return null;
    }
    private Set getVcode(Token[] ts) {
        Set vcodes = new HashSet();
            for(Token t:ts){
                vcodes.add(this.getVcode(t.termText()));
            }
       return vcodes;
    }
    public Map find(char input, Map < java.lang.Character, Map > point) {
        Character c = Character.valueOf(input);
        if (c != null && point != null && point.get(c) != null) {
            point = point.get(c);
            if (point != null)
                return point;
            else
                return null;
        } else
            return null;
    }


    public static void init() {
        stackMap = new VVHashMap(1, 1, false);
        int n = 0;
        try {
            //IbeaProperty.log.info("开始加载词库");
            System.out.println("开始加载词库");
//            SearchContext.wordFileList.add("words" + File.separator +
//                                           "bothlexu8.txt");
//            InputStream worddata = new FileInputStream(new File(
//                    test.class.getClassLoader().getResource(
//                            File.separator + "words" + File.separator +
//                            "bothlexu8.txt").
//                    getFile()));
            InputStream worddata = new FileInputStream(new File("d:\\wordstext.txt"));

            String newword = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    worddata, "UTF8"));
            while ((newword = in.readLine()) != null) {
                n++;
                init(newword);
            }
            in.close();
            System.out.println("加载词库完成,共加载" + n + "个词条");
           // IbeaProperty.log.info("加载词库完成,共加载" + n + "个词条");
        } catch (UnsupportedEncodingException ex) {
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }

    }

    public static void main(String[] args){
        try {
            HCvcodeTool bree = new HCvcodeTool();
            Token[] ts = new Token[5];
            ts[0] = new Token("A0901",0,0); // get accurate
            ts[1] = new Token("煤炭",0,0);    //get firse
            ts[2] = new Token("sdfdsfs",0,0); //null
            ts[3] = new Token("水泥制",0,0);
            ts[4] = new Token("电力、蒸气",0,0);
            Set set = bree.getVcode(ts);
            for(java.util.Iterator i = set.iterator();i.hasNext();){
                System.out.print( i.next() +",");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
