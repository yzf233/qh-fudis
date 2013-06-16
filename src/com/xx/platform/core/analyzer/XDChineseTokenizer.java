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
 * @author not attributable 北京线点科技有限公司 jaddy0302
 * @version 1.0
 */
public class XDChineseTokenizer
    extends Tokenizer {
  private List<String> tokenList;
  private Map<String, Integer> tokenMap;
  private static long time;
  /**
   * 词库 Map ， 扩展支持词性标注和词频
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
   * 初始化词库
   * @param keyword String
   */
  public static void init(String keyword) {
    Map<java.lang.Character, Map> pointMap = stackMap;
    for (int i = 0; i < keyword.length(); i++) {
      char key = keyword.charAt(i);
      if (key == ' ')
        continue;
      if (pointMap.containsKey(key)) {
        //找到了 ，下个字符检索
        pointMap = pointMap.get(key);
        if ( (i + 1 >= keyword.length()))
          ( (VHashMap) pointMap).setRange(true);
        continue;
      }
      else {
        pointMap = pointMap.put(key,
                                new VHashMap(1, 1,
                                             (i + 1 >= keyword.length()) ? true : false));
        //未收录新词 或 词结束
      }
    }
  }

  private Token token = null;
  /**
   * 分词代码
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
      if ( (int) currentchar > 65281 && (int) currentchar < 65374) { //全角转半角
        currentchar = (char) ( (int) currentchar - 65248);
      }

      { //中英文和标点符号
        if ( (int) currentchar <= 255) {
          //英文字符、数字、标点符号
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
                          toLowerCase().intern()); //英文标点符号 或者空格
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
            case -1: { //标点符号 或者也可能是 副词介词等其他未识别的词
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
            case 0: { //已找到字符， 但该字符无下级字符，即 该字为词的最后一个字符
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
    String[] words = input.split("[,， ]");
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
          strb.append("，");
        }
        if (!xd.containsKey(word)) {
          output.write("\n" + word);
          init(word);
          strb.append("'").append(word).append("'新收录");
          wordNum++;
        }
        else {
          strb.append("'").append(word).append("'已收录");
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
    String[] words = input.split("[,， ]");
    StringBuffer strb = new StringBuffer();
    {
      for (String word : words) {
        if (xd.containsKey(word)) {
          if (strb.length() > 0) {
            strb.append("，");
          }
          strb.append("'").append(word).append("'已收录");
        }
        else {
          if (strb.length() > 0) {
            strb.append("，");
          }
          strb.append("'").append(word).append("'未收录");

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
      IbeaProperty.log.info("开始加载词库");
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
      IbeaProperty.log.info("加载词库完成,共加载" + n + "个词条");
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
      String test = "周三沪深市场表现为宽幅振荡的走势，上涨股票的数量明显多于下跌股票的数量，两市有近60只股票报收涨停板位置，显示市场做多的力量仍然充裕。从市场盘面来看，在上午股指宽幅震荡过程中，部分蓝筹股的走强稳定了军心，成为股指最终以红盘报收的关键。比如大秦铁路，中国铝业，五粮液，交通银行等。由于上述个股，均是以基金为首的机构投资者重藏品中，其逆势走强，绝非一般资金行为，因此从市场主流品种活跃来看，机构投资者在目前点位，大肆做空迹象并不明显。　　 那么对于后市，5000点之上多空分歧加剧，市场走向何方依然将是未知数。但对于身在其中的主力资金而言，无论是从进一步扩展空间，还是从全身而退的战略角度，均需要一只带动市场人气的领涨龙头品种。如昔日中海发展，中信证券那样的人气龙头品种。而真正的龙头品种，要具备成交活跃适合大资金进出，价值低估并具成长空间，绝对价格适中易得到市场认同等优势，并且必须得到主流资金的关照。敏感时期，市场呼唤领涨龙头，这正是短期内重要的选股思路。　　 从周三盘面来看，大秦铁路巨量涨停，具备了龙头的风范，但该股的缺点在于绝对价格较高，并且短期涨幅过大，后市宽幅震荡不可避免。那么两市之中是否还存在具备龙头风范的滞涨大盘蓝筹股呢？交通银行  （601328 行情,资料,评论,搜索）携手汇丰银行，引起了我们的关注。　　 交通银行  （601328 行情,资料,评论,搜索）第二大股东为香港上海汇丰银行，持股数量与第一大股东财政部十分接近，公司的外资概念十分正宗。由于该股具备成交活跃适合大资金进出，价值低估并具成长空间，绝对价格适中易得到市场认同等优势。同时该股7月20日至今股价涨幅仅20%，是银行板块涨幅最小的品种之一。近期盘面经常出现百万级大单，这绝非一般资金所为，该股能否一鸣惊人突破上市高点成为市场新龙头，无疑值得期待。"

        ;

        XDChineseTokenizer bree = new XDChineseTokenizer(new StringReader(
                test));
        Token token;
//        while ((token = bree.next()) != null) {
//            {
//                System.out.println("字符：" + token.termText() + " 开始：" +
//                                   token.startOffset() + " 结束：" +
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

    out("内存占用:(" +
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
