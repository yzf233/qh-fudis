package com.xx.platform.util.tools.zhconverter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.SearchContext;

public class ZHConverter {
	private static Logger logger=Logger.getLogger(ZHConverter.class);
	private Properties charMap = new Properties();
	private Set<String> conflictingSets = new HashSet<String>();

	public static final int TRADITIONAL = 0;
	public static final int SIMPLIFIED = 1;
	private static final int NUM_OF_CONVERTERS = 2;
	private static final ZHConverter[] converters = new ZHConverter[NUM_OF_CONVERTERS];
	private static final String[] propertyFiles = new String[2];

	static {
		// propertyFiles[TRADITIONAL] = "zh2Hant.properties";
		propertyFiles[SIMPLIFIED] = SearchContext.contextPath+"classes"+File.separator+"words"+File.separator+"zh2Hans.properties";
		logger.info("加载繁简体对照词库，词库文件："+propertyFiles[SIMPLIFIED]);
		getInstance(SIMPLIFIED);
	}
	public ZHConverter(){}
	/**
	 * 
	 * @param converterType
	 *            0 for traditional and 1 for simplified
	 * @return
	 */
	public static ZHConverter getInstance(int converterType) {
		if (converterType >= 0 && converterType < NUM_OF_CONVERTERS) {
			if (converters[converterType] == null) {
				synchronized (ZHConverter.class) {
					converters[converterType] = new ZHConverter(
							propertyFiles[converterType]);
				}
			}
			return converters[converterType];
		} else {
			return null;
		}
	}

	public static String convert(String text, int converterType) {
		ZHConverter instance = getInstance(converterType);
		return instance.convert(text);
	}

	private ZHConverter(String propertyFile) {
		InputStream is = null;
		try {
			is=new FileInputStream(propertyFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
//		is = getClass().getResourceAsStream(propertyFile);
		if (is != null) {
			try {
				charMap.load(is);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
				}
			}
		}
		initializeHelper();
	}

	private void initializeHelper() {
		Map<String, Integer> stringPossibilities = new HashMap<String, Integer>();

		Iterator iter = charMap.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			int keyCount = key.length();
			if (keyCount >= 1) {
				int integer;
				for (int i = 0; i < keyCount; i++) {
					String keySubstring = key.substring(0, i + 1);
					if (stringPossibilities.containsKey(keySubstring)) {
						integer = stringPossibilities.get(keySubstring);
						stringPossibilities.put(keySubstring, integer + 1);
					} else {
						stringPossibilities.put(keySubstring, 1);
					}
				}
			}
		}

		for (Map.Entry<String, Integer> entry : stringPossibilities.entrySet()) {
			if (entry.getValue() > 1) {
				conflictingSets.add(entry.getKey());
			}
		}
	}

	public String convert(String in) {
		boolean isChange = NutchConf.get()
				.getBoolean("char.ZHConverter", false);
		if(!isChange){
			return in;
		}
		if (in == null) {
			return in;
		}
		StringBuilder outString = new StringBuilder();
		StringBuilder stackString = new StringBuilder();
		long charCount = in.length();
		String key = null;
		CharSequence sequence = null;
		char c;
		for (int i = 0; i < charCount; i++) {
			c = in.charAt(i);
			key = String.valueOf(c);
			stackString.append(key);
			key = stackString.toString();
			if (conflictingSets.contains(key)) {

			} else if (charMap.containsKey(key)) {
				outString.append(charMap.get(key));
				stackString.setLength(0);
			} else {
				sequence = stackString.subSequence(0, stackString.length() - 1);
				stackString.delete(0, stackString.length() - 1);
				flushStack(outString, new StringBuilder(sequence));
			}
		}
		flushStack(outString, stackString);
		return outString.toString();
	}

	private void flushStack(StringBuilder outString, StringBuilder stackString) {
		while (stackString.length() > 0) {
			if (charMap.containsKey(stackString.toString())) {
				outString.append(charMap.get(stackString.toString()));
				stackString.setLength(0);
			} else {
				outString.append(String.valueOf(stackString.charAt(0)));
				stackString.delete(0, 1);
			}
		}
	}

	/**
	 * 测试方法
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("F:\\txt3\\新建文本文档.txt"))));
		StringBuffer sbContent = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null) {
			sbContent.append(line);
		}
		br.close();
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		String simplifiedStr = converter.convert(sbContent.toString());
		long l1 = System.currentTimeMillis();
		simplifiedStr = converter.convert(sbContent.toString());
		System.out.println(System.currentTimeMillis() - l1);
		// System.out.println(simplifiedStr);
		String simplifiedStr1 = converter.convert("有背光的機械式鍵盤");
		System.out.println(simplifiedStr1);
		// System.out.println(ZHConverter.convert("有背光的机械式键盘",
		// ZHConverter.TRADITIONAL));
	}
}
