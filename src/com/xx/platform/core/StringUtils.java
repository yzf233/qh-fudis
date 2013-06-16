package com.xx.platform.core;


import java.security.*;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.xx.platform.core.nutch.IndexFieldImpl;
public class StringUtils {

    private static final String PASSWORD_CRYPT_KEY = "ejdlsoep";
    private final static String DES = "DES";

    private static String getKey()
    {
    	String ec=c(SearchContext.getXdtechsite().getEncryptpwd().toUpperCase());
    	return ec;
    }
    private static String c(String e)
    {
    	if(e==null||e.trim().equals(""))
    		return e;
    	return e+PASSWORD_CRYPT_KEY;
    }
    public static boolean isEncryptField(String fieldName)
    {
    	List<IndexFieldImpl>  ls=SearchContext.getIndexFieldList();
    	for(IndexFieldImpl index:ls)
    	{
    		if(index.isIsencrypt()&&index.getCode().equals(fieldName))
    			return true;
    	}
    	return false;
    }
    /**
     * 加密
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
//DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
// 从原始密匙数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
// 创建一个密匙工厂，然后用它把DESKeySpec转换成
// 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
// Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
// 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
// 现在，获取数据并加密
// 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     * @param src 数据源
     * @param key 密钥，长度必须是8的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
// DES算法要求有一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
// 从原始密匙数据创建一个DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
// 一个SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
// Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
// 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
// 现在，获取数据并解密
// 正式执行解密操作
        return cipher.doFinal(src);
    }

    /**
     * 密码解密
     * @param data
     * @return
     * @throws Exception
     */
    public final static String decrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data.getBytes()),
            		getKey().getBytes()));
        } catch (Exception e) {
        }
        return data;

       }    /**
     * 密码加密
     * @param password
     * @return
     * @throws Exception
     */
    public final static String encrypt(String password) {
        try {
            return  byte2hex(encrypt(password.getBytes(),
                                    getKey().getBytes()));
        } catch (Exception e) {
        }
        return password;
    }
/** 

          * 二行制转字符串 

          * @param b 

          * @return 

          */ 

           public static String byte2hex(byte[] b) { 

                 String hs = ""; 

                 String stmp = ""; 

                 for (int n = 0; n < b.length; n++) { 

                     stmp = (java.lang.Integer.toHexString(b[n] & 0XFF)); 

                     if (stmp.length() == 1) 

                         hs = hs + "0" + stmp; 

                     else 

                         hs = hs + stmp; 

                 } 

                 return hs.toUpperCase(); 

            } 

            public static byte[] hex2byte(byte[] b) { 

             if((b.length%2)!=0) 

                throw new IllegalArgumentException("长度不是偶数"); 

                 byte[] b2 = new byte[b.length/2]; 

                 for (int n = 0; n < b.length; n+=2) { 

                   String item = new String(b,n,2); 

                   b2[n/2] = (byte)Integer.parseInt(item,16); 

                 } 
             return b2; 
        }
       public static void main(String[] args) {
            String es = "打";
            String ds1= StringUtils.encrypt(es);
            System.out.println("encrpt:"+ ds1);
            System.out.println("decrypt:"+ StringUtils.decrypt("06C637E6823D88E0"));
        }
}
