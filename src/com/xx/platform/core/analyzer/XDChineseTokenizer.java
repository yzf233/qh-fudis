package com.xx.platform.core.analyzer;

import java.util.*;
import java.io.*;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Token;

import com.xx.platform.core.SearchContext;
import com.xx.platform.util.constants.IbeaProperty;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable �����ߵ�Ƽ����޹�˾ jaddy0302
 * @version 1.0
 */
public class XDChineseTokenizer
    extends Tokenizer {
  private List<String> tokenList;
  private Map<String, Integer> tokenMap;
  private static long time;
  /**
   * �ʿ� Map �� ��չ֧�ִ��Ա�ע�ʹ�Ƶ
   */
  private static Map<java.lang.Character, Map> stackMap;

  private Map<java.lang.Character, Map> pointMap = null;

  static {
    init();
  }

  public XDChineseTokenizer(Reader reader) {
    this.input = reader;
  }

  /**
   * ��ʼ���ʿ�
   * @param keyword String
   */
  public static void init(String keyword) {
    Map<java.lang.Character, Map> pointMap = stackMap;
    for (int i = 0; i < keyword.length(); i++) {
      char key = keyword.charAt(i);
      if (key == ' ')
        continue;
      if (pointMap.containsKey(key)) {
        //�ҵ��� ���¸��ַ�����
        pointMap = pointMap.get(key);
        if ( (i + 1 >= keyword.length()))
          ( (VHashMap) pointMap).setRange(true);
        continue;
      }
      else {
        pointMap = pointMap.put(key,
                                new VHashMap(1, 1,
                                             (i + 1 >= keyword.length()) ? true : false));
        //δ��¼�´� �� �ʽ���
      }
    }
  }

  private Token token = null;
  /**
   * �ִʴ���
   * @param input String
   * @return List
   */
  private List token(String input) {
    if (input == null || input.length() == 0)
      return null;
    StringBuffer strb = new StringBuffer();
    int tokenLength = 0;
    char currentchar;
    tokenList = new ArrayList();
    int maxRangeIndex = 0;
    boolean isLetter = false;
    for (int i = 0; i < input.length(); i++) {
      currentchar = input.charAt(i);
      if ( (int) currentchar > 65281 && (int) currentchar < 65374) { //ȫ��ת���
        currentchar = (char) ( (int) currentchar - 65248);
      }

      { //��Ӣ�ĺͱ�����
        if ( (int) currentchar <= 255) {
          //Ӣ���ַ������֡�������
          if (!isLetter) {
            tokenList.add(strb.toString().intern());
            strb.setLength(0);
            maxRangeIndex = 0;
          }

          if (java.lang.Character.isLetterOrDigit(currentchar)) {
            strb.append(currentchar);
          }else {
            tokenList.add(strb.toString().toLowerCase().intern());
            tokenList.add(String.valueOf(currentchar).
                          toLowerCase().intern()); //Ӣ�ı����� ���߿ո�
            strb.setLength(0);
            maxRangeIndex = 0;
          }
          isLetter = true;
        }
        else {
          if (isLetter && strb.toString() != null &&
              strb.toString().length() > 0) {
            tokenList.add(strb.toString().intern());
            strb.setLength(0);
            maxRangeIndex = 0;
          }

          pointMap = find(currentchar,
                          strb.length() == 0 ? stackMap :
                          pointMap);

          int subcondition = subcondition = (pointMap == null) ? -1 :
              pointMap.size();
          int condition = (subcondition >= 1) ? 1 : subcondition;
          switch (condition) {
            case -1: { //������ ����Ҳ������ ���ʽ�ʵ�����δʶ��Ĵ�
              if (strb.length() > 0) {
                if (maxRangeIndex > 0 && maxRangeIndex < strb.toString().length()) {
                  tokenList.add(strb.substring(0, maxRangeIndex).
                                toString().intern());
                  i = (i - 1) - (strb.length() - maxRangeIndex);
                  maxRangeIndex = 0;
                  strb.setLength(0);
                  break;
                }
                else {
                  if (maxRangeIndex == 0) {
                    for (int index = 0; index < strb.length(); index++)
                      tokenList.add(String.valueOf(strb.charAt(index)));
                  }
                  else {
                    tokenList.add(strb.toString().intern());
                    maxRangeIndex = 0;
                  }

//                                tokenList.add(strb.toString().intern());
                }
              }
              strb.setLength(0);
              pointMap = find(currentchar, stackMap);
              {
                boolean test = pointMap == null ?
                    tokenList.add(String.
                                  valueOf(currentchar)) :
                    strb.append(currentchar) != null;
              }
              break;
            }
            case 0: { //���ҵ��ַ��� �����ַ����¼��ַ����� ����Ϊ�ʵ����һ���ַ�
              strb.append(currentchar);
              tokenList.add(strb.toString().intern());
              strb.setLength(0);
              maxRangeIndex = 0;
              break;
            }
            case 1: {
              strb.append(currentchar);
              if (strb.length() >= 1 && ( (VHashMap) pointMap).isRange()) {
                maxRangeIndex = strb.length();
              }
              break;
            }
          }
          isLetter = false;
        }
      }
    }
    if (strb.length() > 0) {
      tokenList.add(strb.toString().toLowerCase().intern());
      strb.setLength(0);
      maxRangeIndex = 0;

    }
    return tokenList;
  }

  public Map find(char input, Map < java.lang.Character, Map > point) {
    if (point == null)
      point = stackMap;
    //System.out.println("input"+input);
    Character c = Character.valueOf(input);
    if (c != null && point != null && point.get(c) != null) {
//            System.out.println("pointMap"+pointMap.size());
//            System.out.println("pointMap.get"+pointMap.get(c).size());
      point = point.get(c);
      if (point != null)
        return point;
      else
        return null;
    }
    else
      return null;
  }

  public boolean containsKey(String key) {
    boolean contains = true;
    Map<java.lang.Character, Map> pointMap = stackMap;
    for (int i = 0; i < key.length(); i++) {
      pointMap = find(key.charAt(i), pointMap);
      if (pointMap == null)
        contains = false;
    }
    return contains;
  }

  public static String saveNewWord(String input) {
    String[] words = input.split("[,�� ]");
    XDChineseTokenizer xd = new XDChineseTokenizer(null);
    int wordNum = 0;
    OutputStream outStream = null;
    Writer output = null;
    StringBuffer strb = new StringBuffer();
    try {
    	System.out.println() ;
      outStream = new FileOutputStream(new File((System.getProperty("XDTECHLicensePath")+File.separator+"words"+File.separator+
              "words/bothlexu8.txt")), true);
      output = new OutputStreamWriter(outStream, "UTF-8");
      for (String word : words) {
        if (strb.length() > 0) {
          strb.append("��");
        }
        if (!xd.containsKey(word)) {
          output.write("\n" + word);
          init(word);
          strb.append("'").append(word).append("'����¼");
          wordNum++;
        }
        else {
          strb.append("'").append(word).append("'����¼");
        }
      }
      for (int i = 26; i < strb.length(); i = i + 26) {
        strb.insert(i, "<br/>");
      }

    }
    catch (Exception ex) {
      IbeaProperty.log.info(ex.getMessage());
    }
    finally {
      try {
        outStream.flush();
        output.flush();
        outStream.close();
        output.close();
      }
      catch (IOException ex1) {
      }
    }
    return strb.toString();
  }

  public static String wordSearch(String input) {
    XDChineseTokenizer xd = new XDChineseTokenizer(null);
    String[] words = input.split("[,�� ]");
    StringBuffer strb = new StringBuffer();
    {
      for (String word : words) {
        if (xd.containsKey(word)) {
          if (strb.length() > 0) {
            strb.append("��");
          }
          strb.append("'").append(word).append("'����¼");
        }
        else {
          if (strb.length() > 0) {
            strb.append("��");
          }
          strb.append("'").append(word).append("'δ��¼");

        }
      }
      for (int i = 26; i < strb.length(); i = i + 26) {
        strb.insert(i, "<br/>");
      }

    }
    return strb.toString();
  }

  public static void init() {
    stackMap = new VHashMap(1, 1, false);
    int n = 0;
    try {
      IbeaProperty.log.info("��ʼ���شʿ�");
      SearchContext.wordFileList.add("words" + File.separator +
                                     "bothlexu8.txt");
      
      InputStream worddata = new FileInputStream(new File((System.getProperty("XDTECHLicensePath")+File.separator+"words"+File.separator+"bothlexu8.txt")));
      String newword = null;
      BufferedReader in = new BufferedReader(new InputStreamReader(
          worddata, "UTF8"));
      while ( (newword = in.readLine()) != null) {
        if (newword.length() == 1) {
          continue;
        }
        n++;
        init(newword);
      }
      in.close();
      IbeaProperty.log.info("���شʿ����,������" + n + "������");
    }
    catch (UnsupportedEncodingException ex) {
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }

  }

  public static void main(String[] args) {
    try {
      String test = "���������г�����Ϊ����񵴵����ƣ����ǹ�Ʊ���������Զ����µ���Ʊ�������������н�60ֻ��Ʊ������ͣ��λ�ã���ʾ�г������������Ȼ��ԣ�����г������������������ָ����𵴹����У���������ɵ���ǿ�ȶ��˾��ģ���Ϊ��ָ�����Ժ��̱��յĹؼ������������·���й���ҵ������Һ����ͨ���еȡ������������ɣ������Ի���Ϊ�׵Ļ���Ͷ�����ز�Ʒ�У���������ǿ������һ���ʽ���Ϊ����˴��г�����Ʒ�ֻ�Ծ����������Ͷ������Ŀǰ��λ���������ռ��󲢲����ԡ����� ��ô���ں��У�5000��֮�϶�շ���Ӿ磬�г�����η���Ȼ����δ֪�����������������е������ʽ���ԣ������Ǵӽ�һ����չ�ռ䣬���Ǵ�ȫ����˵�ս�ԽǶȣ�����Ҫһֻ�����г�������������ͷƷ�֡��������к���չ������֤ȯ������������ͷƷ�֡�����������ͷƷ�֣�Ҫ�߱��ɽ���Ծ�ʺϴ��ʽ��������ֵ�͹����߳ɳ��ռ䣬���Լ۸������׵õ��г���ͬ�����ƣ����ұ���õ������ʽ�Ĺ��ա�����ʱ�ڣ��г�����������ͷ�������Ƕ�������Ҫ��ѡ��˼·������ ����������������������·������ͣ���߱�����ͷ�ķ緶�����ùɵ�ȱ�����ھ��Լ۸�ϸߣ����Ҷ����Ƿ����󣬺��п���𵴲��ɱ��⡣��ô����֮���Ƿ񻹴��ھ߱���ͷ�緶�����Ǵ���������أ���ͨ����  ��601328 ����,����,����,������Я�ֻ�����У����������ǵĹ�ע������ ��ͨ����  ��601328 ����,����,����,�������ڶ���ɶ�Ϊ����Ϻ�������У��ֹ��������һ��ɶ�������ʮ�ֽӽ�����˾�����ʸ���ʮ�����ڡ����ڸùɾ߱��ɽ���Ծ�ʺϴ��ʽ��������ֵ�͹����߳ɳ��ռ䣬���Լ۸������׵õ��г���ͬ�����ơ�ͬʱ�ù�7��20������ɼ��Ƿ���20%�������а���Ƿ���С��Ʒ��֮һ���������澭�����ְ��򼶴󵥣������һ���ʽ���Ϊ���ù��ܷ�һ������ͻ�����иߵ��Ϊ�г�����ͷ������ֵ���ڴ���"

        ;

        XDChineseTokenizer bree = new XDChineseTokenizer(new StringReader(
                test));
        Token token;
//        while ((token = bree.next()) != null) {
//            {
//                System.out.println("�ַ���" + token.termText() + " ��ʼ��" +
//                                   token.startOffset() + " ������" +
//                                   token.endOffset());
//            }
//        }
        long start = 0 ;
        bree.seg();
        long end = System.nanoTime();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    }

  /**
   * Displays some timing and approximate memory info.
   */
  public static void mem() {
    Runtime r = Runtime.getRuntime();
    long totalMemory = r.totalMemory() - r.freeMemory();
    boolean b = totalMemory > 1 << 20;
    long f = ( (totalMemory > 1 << 20) ? 20 : 10);

    out("�ڴ�ռ��:(" +
        (totalMemory + (1 << ( (totalMemory > 1 << 20) ? 20 : 10) - 1) >>
         ( (totalMemory > 1 << 20) ? 20 : 10)) +
        (totalMemory > 1 << 20 ? " mb" : " kb") + ")\n");
  }

  private static void out(String text) {
    System.out.print(text);
  }

  private char[] charb = new char[1024];
  public void tokenize() throws IOException {
    StringBuffer strb = new StringBuffer();

    int length;
    while ( (length = input.read(charb)) > 0) {
      strb.append(new String(charb, 0, length));
//            charb = new char[1024];
    }
    this.tokenList = token(strb.toString());
  }

  private int iHZ = 0, iStart = 0;
  private String iToken;
  public Token next() throws IOException {
    if (tokenList == null) {
      tokenize();
    }
    int oldStart = iStart;
    if (tokenList != null && tokenList.size() > 0) {
      iToken = tokenList.remove(0);
      if ((iToken == "" ||iToken==null)&&tokenList.size()>0)
        return next();
      iStart += iToken.length();
      return new Token(iToken.toLowerCase(), oldStart, iStart);
    }
    else {
      return null;
    }
  }
  public String seg() throws IOException {
    tokenList = null ;
     if (tokenList == null) {
       tokenize();
     }
     Integer numValue = 0 ;
     tokenMap = new HashMap<String,Integer>();
     if(tokenList!=null)
       for (String value : tokenList) {
         if (value.length() <= 1)
           continue;
         numValue = tokenMap.get(value);
         if (numValue != null) {
           tokenMap.put(value, numValue + 1);
         }
         else {
           tokenMap.put(value, 1);
         }
       }
     Iterator<String> iterator = tokenMap.keySet().iterator() ;
     Integer value ;
     String key ;
     while(iterator.hasNext())
     {
       value = tokenMap.get(key=iterator.next()) ;
       System.out.println(key+":"+value) ;
     }
     return "" ;
  }
}
