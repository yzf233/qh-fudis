package com.xx.platform.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xx.platform.core.SearchContext;

public class ZfilterSome {
	public static List<Long> listLong;
	private static Map<Character, Character> sList = new HashMap<Character, Character>();;

	/**
	 * 获得文件中内容，存到List中
	 * 
	 * @param RealPath
	 *            String
	 * @return List
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static List<String> ReadFile() throws FileNotFoundException,
			UnsupportedEncodingException, IOException {
		File rf = new File(SearchContext.contextPath + "filterWord.txt");
		List<String> list = new ArrayList<String>();
		if (!rf.exists()) {
			return null;
		} else {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					rf));
			BufferedReader reader = new BufferedReader(read);
			String line;
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
			reader.close();
			read.close();
		}
		return list;
	}

	/**
	 * 判断是否有0X，有的话去掉
	 * 
	 * @param list
	 *            List
	 * @return List
	 */
	public static List<String> change(List<String> list) {
		if (list != null && list.size() > 0) {
			int n = list.size();
			for (int i = 0; i < n; i++) {
				String result;
				String s = list.get(i);
				if (s != null && s.trim().length() > 0) {
					String temp = s.substring(0, 2);
					if ("0x".equalsIgnoreCase(temp)) {
						result = s.substring(2, s.length());
						list.set(i, result);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 将16进制数的字符串转换为10进制Long型
	 * 
	 * @param list
	 *            List
	 * @return List
	 */
	public static List<Long> getLong(List<String> list) {
		List<Long> listLong = new ArrayList<Long>();
		if (list != null && list.size() > 0) {
			int i = 1;
			for (String s : list) {
				try {
					Long l = Long.valueOf(s, 16);
					listLong.add(l);
					i++;
				} catch (Exception e) {
					System.out.println("第" + i + "行 \"" + s + "\" 或者 \"0x" + s
							+ "\" 不是十六进制数！");
					i++;
				}
			}
		}
		return listLong;
	}

	public static String doAll(String content) {
		if (listLong == null) {
			try {
				List<String> list = ReadFile();
				if (list != null && list.size() > 0) {
					list = change(list);
					listLong = getLong(list);
				} else {
					listLong = new ArrayList<Long>();
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (FileNotFoundException ex) {
			} catch (IOException ex) {
			}
			if (listLong != null) {
				for (long l : listLong) {
					sList.put((char) l, (char) l);
				}
			}
		}

		StringBuffer strb = new StringBuffer();
		long length = content.length();
		char c;
		for (int i = 0; content != null && i < length; i++) {
			c = content.charAt(i);
			if (content.charAt(i) > 31 && sList.get((char) c) == null) {
				strb.append(c);
			}
		}
		return strb.toString();
	}

	public static void main(String[] args) {
	}
}
