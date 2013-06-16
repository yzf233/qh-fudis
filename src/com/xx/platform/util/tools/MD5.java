package com.xx.platform.util.tools;
/**
 *
 * <p>Title: MD5加密</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: 北京线点科技有限公司</p>
 * @author iceworld
 * @version 1.0
 */
public class MD5 {
  public static final String MD5STR1 = "234OSADJFLZF*(&(*(alsjdflaksdasdfafsafa";
  public static final String MD5STR = "alsjdflaksdasdfafsafa!^&@asfasdf=0-==_+__))*(*";
  /**
   * 加密
   * @param source String
   * @return String
   */
  public static String encoding(String source){
    StringBuffer reStr = null;
    try {
      java.security.MessageDigest alga = java.security.MessageDigest.getInstance("MD5");
      byte[] bs = alga.digest(source.getBytes());
      reStr = new StringBuffer();
      for(int i = 0; i < bs.length ; i ++){
        reStr.append(byteHEX(bs[i]));
      }
    }catch (Exception ex) {
    }
    return reStr==null?null:reStr.toString();
  }
  /**
   * 比较
   * @param a String
   * @param b String
   * @return boolean
   */
  public static boolean compare(String a, String b){
    boolean flag = false;
    if(a == b){
      return true;
    }
    try {
      java.security.MessageDigest alga = java.security.MessageDigest.
          getInstance("MD5");
      flag = alga.isEqual(a.getBytes(), encoding(b).getBytes());
    }catch (Exception ex) {
    }
    return flag;
  }
  /**
   * 进行md5字符中的比较
   * @param md5a String
   * @param md5b String
   * @return boolean
   */
  public static boolean compareMD5(String md5a, String md5b){
    boolean flag = false;
    if(md5a == md5b){
      return true;
    }
    try {
      java.security.MessageDigest alga = java.security.MessageDigest.
          getInstance("MD5");
      flag = alga.isEqual(md5a.getBytes(), md5b.getBytes());
    }catch (Exception ex) {
    }

    return flag;
  }


  /*byteHEX()，用来把一个byte类型的数转换成十六进制的ASCII表示，
  　因为java中的byte的toString无法实现这一点，我们又没有C语言中的
    sprintf(outbuf,"%02X",ib)
  */
      public static String byteHEX(byte ib) {
              char[] Digit = { '0','1','2','3','4','5','6','7','8','9',
              'A','B','C','D','E','F' };
              char [] ob = new char[2];
              ob[0] = Digit[(ib >>> 4) & 0X0F];
              ob[1] = Digit[ib & 0X0F];
              String s = new String(ob);
              return s;
      }

      public static void main(String args[]) {
        String m = MD5.encoding("111");
        System.out.println(m);

        String m1 = MD5.encoding("111");
        System.out.println(m1);
        System.out.println(MD5.compare(m1,"111"));
//        System.out.println(MD5.compareMD5("698D51A19D8A121CE581499D7B701668", "BCBE3365E6AC95EA2C0343A2395834DD"));
      }
}
