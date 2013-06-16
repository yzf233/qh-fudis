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
 * ƴ������������
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")   
public class PYContext {   
	public static VHashMap<java.lang.Character, VHashMap> pinYinMap=new VHashMap(1,1,false);
	private final static int WORDLENGTH=10;//���ش���
	private final static boolean MORE=true;//false��ƴ��������ȫƥ����ܷ��شʣ�true����ƥ�伴�ɡ�
	private final static int LENGTH=2;//�������������ȵ�ƴ�������ܷ��ؽ����
    static{
    	try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			IbeaProperty.log.info("ƴ���ʿ����ʧ��");
		}
    }
    /**
     * ��ʼ��������ƴ����
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
			temp[0]=str[0];//��
			temp[1]=str[2];//Ȩ��
			
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
		resetMap(pinYinMap);//�Ż��ڴ�
		System.out.println("��ʼ����ϣ�������:"+n+" ��ƴ������") ;
    }
    /**
     * ��ȥpinYinMap���Ȩ�����ݣ��Խ����ڴ�����
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
     * ����ʹ�÷���������ƴ���������Ĵ�
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
				if(MORE&&i!=word.length()-1)//������ñ�����ȫƥ�䣬��ôֱ���ҵ�ƴ����Ҷ�ӽڵ㣬�鿴�Ƿ��д�
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
     * ������������ӽڵ������
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
    	System.out.print("������ƴ����");
		while((pinyin=stdin.readLine())!=null)
		{
			pinyin=pinyin.toLowerCase();
			List ls=find(pinyin);
			if(ls==null)
			{
				System.out.println();
				System.out.print("������ƴ����");
				continue;
			}
			else
			{
				for(int i=0;i<ls.size();i++)
				{
					System.out.println(ls.get(i));
				}
				System.out.println();
				System.out.print("������ƴ����");
			}
		}
    }      
       
}  
