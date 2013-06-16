package com.xx.platform.core;

  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.xx.platform.util.constants.IbeaProperty;

/**
 * 拼音搜索核心类
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")   
public class PYContext {   
	public static VHashMap<java.lang.Character, VHashMap> pinYinMap=new VHashMap(1,1,false);
	private final static int WORDLENGTH=10;//返回词数
	private final static boolean MORE=true;//false则拼音必须完全匹配才能返回词，true部分匹配即可。
	private final static int LENGTH=2;//输入大于这个长度的拼音，才能返回结果。
    static{
    	try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			IbeaProperty.log.info("拼音词库加载失败");
		}
    }
    /**
     * 初始化，构造拼音树
     * @throws Exception
     */
    public static void init() throws Exception
    {
    	File file=new File(System.getProperty("XDTECHLicensePath")+File.separator+"words"+File.separator+"word.txt");
    	InputStream worddata = new FileInputStream(file);
		String newline = null;
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				worddata,"utf-8"));
		int n = 0;
		while ((newline = in.readLine()) != null) {
			String str[]=newline.split("\t");
			String temp[]=new String [2];
			temp[0]=str[0];//词
			temp[1]=str[2];//权重
			
			String fullSpell=str[1].replace("'", "");
			if(fullSpell==null||fullSpell.trim().length()==0)
				continue;
			VHashMap<java.lang.Character, VHashMap> point=new VHashMap(1,1,false);
			if(pinYinMap.get(fullSpell.charAt(0))==null)
			{
				pinYinMap.put(fullSpell.charAt(0), new VHashMap(1, 1,false));
			}
			point=(VHashMap<java.lang.Character, VHashMap>)pinYinMap.get(fullSpell.charAt(0));
			for(int i=1;i<fullSpell.length();i++)
			{
				char at=fullSpell.charAt(i);
				if(point.get(fullSpell.charAt(i))==null)
				{
					point.put(fullSpell.charAt(i), new VHashMap(1, 1,false));
				}
				point=(VHashMap<java.lang.Character, VHashMap>)point.get(at);
				if(i==(fullSpell.length()-1))
				{
					point.setRange(true);
					point.addWord(temp);
				}
			}
			n++;
		}
		resetMap(pinYinMap);//优化内存
		System.out.println("初始化完毕，共加载:"+n+" 个拼音词条") ;
    }
    /**
     * 除去pinYinMap里词权重内容，以降低内存消耗
     * @param map
     */
    private static void resetMap(VHashMap<java.lang.Character, VHashMap> map)
    {
    	String pinyin="abcdefghijklmnopqrstuvwxyz";
    	for(int i=0;i<pinyin.length();i++)
    	{
    		java.lang.Character c=pinyin.charAt(i);
    		VHashMap<java.lang.Character, VHashMap> point=(VHashMap<java.lang.Character, VHashMap>)pinYinMap.get(c);
    		if(point!=null&&point.isRange())
    		{
    			List<String[]> ls=point.getWordList();
    			List<String[]> temp=new ArrayList();
    			for(String[] s:ls)
    			{
    				String t[]=new String [1];
    				t[0]=s[0];
    				temp.add(t);
    			}
    			point.clear();
    			point.setWordList(temp);
    			resetMap(point);
    		}
    	}
    }
    /**
     * 公共使用方法，输入拼音返回中文词
     * @param word
     * @return
     */
    public static List<String> find(String word)
    {
    	if(word==null||word.trim().equals("")||word.trim().length()<=LENGTH)
    		return null;
    	List<String> wordList=new ArrayList();
    	VHashMap<java.lang.Character, VHashMap> point=new VHashMap(1,1,false);
    	VHashMap<java.lang.Character, VHashMap> headPoint=(VHashMap<java.lang.Character, VHashMap>)pinYinMap.get(word.charAt(0));
		if(headPoint==null)
		{
			return null;
		}
		else
		{
			if(headPoint.isRange())
			{
				wordList.clear();
				for(String [] wd:headPoint.getWordList())
				{
					if(!wordList.contains(wd[0]))
					wordList.add(wd[0]);
				}
			}
		}
		if(word.length()==1)
		{
			point=headPoint;
		}
		else
		{
			for(int i=1;i<word.length();i++)
			{
				wordList.clear();
				point=(VHashMap<java.lang.Character, VHashMap>)headPoint.get(word.charAt(i));
				if(point==null)
				{
					return null;
				}
				if(MORE&&i!=word.length()-1)//如果设置必须完全匹配，那么直接找到拼音的叶子节点，查看是否有词
				{
					headPoint=point;
					continue;
				}
				else
				{
					if(point.isRange())
					{
						wordList.clear();
						for(String wd[]:point.getWordList())
						{
							if(!wordList.contains(wd[0]))
							wordList.add(wd[0]);
						}
					}
				}
				headPoint=point;
			}
		}
		if((wordList==null||wordList.size()<WORDLENGTH)&&MORE)
		{
			findprix(point,wordList);
		}
		while(wordList.size()>WORDLENGTH)
		{
			wordList.remove(wordList.size()-1);
		}
//		for(String wd:wordList)
//		{
//			System.out.println(wd);
//		}
    	return wordList;
    }
    /**
     * 层序遍历查找子节点词内容
     * @param point
     * @param wordList
     */
    public static void findprix(VHashMap<java.lang.Character, VHashMap> point,List<String> wordList)
    {
		java.util.Iterator<java.lang.Character> it=point.keySet().iterator();
		while(it.hasNext())
		{
			java.lang.Character c=it.next();
			VHashMap<java.lang.Character,VHashMap> tempMap=(VHashMap<java.lang.Character, VHashMap>)point.get(c);
			if(tempMap.isRange())
			{
				for(String wd[]:tempMap.getWordList())
				{
					if(!wordList.contains(wd[0]))
					wordList.add(wd[0]);
					if(wordList.size()>9)
						return ;
				}
			}
			findprix(tempMap,wordList);
		}
    }
       
    public static void main(String args[]) throws Exception{     
    	BufferedReader stdin =new BufferedReader(new InputStreamReader(System.in)); 
    	String pinyin = null ;
    	System.out.print("请输入拼音：");
		while((pinyin=stdin.readLine())!=null)
		{
			pinyin=pinyin.toLowerCase();
			List ls=find(pinyin);
			if(ls==null)
			{
				System.out.println();
				System.out.print("请输入拼音：");
				continue;
			}
			else
			{
				for(int i=0;i<ls.size();i++)
				{
					System.out.println(ls.get(i));
				}
				System.out.println();
				System.out.print("请输入拼音：");
			}
		}
    }      
       
}  
