package com.xx.platform.core;

import java.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
 *
 * <p>Title: HashMap</p>
 *
 * <p>Description: 重载 HashMap put 方法</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable 北京线点科技有限公司
 * @version 1.0
 */
public class VHashMap<K, V> extends HashMap<K, V> {
    private boolean range = false ;
    /**
     * string[0]词，string[1]权重
     */
    private List<String[]> wordList=new ArrayList();
    public VHashMap(int initialCapacity, float loadFactor , boolean range) {
        super(initialCapacity, loadFactor);
        this.range = range ;
    }

    public V put(K key, V value) {
        super.put(key, value);
        return value;
    }
    /**
     * 如果已经有了词那么需要用当前词到wordlist里面进行冒泡排序并且插入。
     * @param word
     */
    public void addWord(String [] word)
    {
    	if(wordList.size()>0)
    	{
    		int length=wordList.size();
    		for(int i=0;i<length;i++)
    		{
    			String temp[]=wordList.get(i);
    			try
    			{
	    			int pri=Integer.valueOf(temp[1]);
	    			int wordpri=Integer.valueOf(word[1]);
	    			if(wordpri>pri)
	    			{
	    				wordList.add(i, word);
	        			return;
	    			}
    			}
    			catch(Exception e)
    			{
    				
    			}
    		}
    		wordList.add(word);//最小的排在最后
    	}
    	else
    	{
    		wordList.add(word);
    	}
    }
    public List<String[]> getWordList()
    {
    	return wordList;
    }
    public void setWordList(List<String[]> wdlist)
    {
    	wordList=wdlist;
    }
    public void clearWordList()
    {
    	wordList.clear();
    }
    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }
}
