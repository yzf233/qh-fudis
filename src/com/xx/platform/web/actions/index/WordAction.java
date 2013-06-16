package com.xx.platform.web.actions.index;

import java.io.*;
import java.util.*;

import com.opensymphony.xwork2.*;
import com.xx.platform.core.*;
import com.xx.platform.core.analyzer.*;
import com.xx.platform.web.actions.*;

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
public class WordAction extends BaseAction{
    private List wordFileList;
    private Wordfile wordfile ;
    private int wordFileNum ;
    private String message ;
    private int wordNum ;
    private String newWord ;
    public String list()
    {
        wordFileNum = SearchContext.wordFileList.size() ;
        wordNum = SearchContext.wordNum ;
        wordFileList = new ArrayList() ;
        for(String wordFileName :SearchContext.wordFileList)
        {
            wordfile = new Wordfile();
            wordfile.setFileName(wordFileName);
            wordFileList.add(wordfile) ;
        }
        return Action.SUCCESS ;
    }
    public String viewWordFile()
    {
        if(wordfile!=null && wordfile.getFileName()!=null)
        {
            InputStream input = null ;
            byte[] word = new byte[1024] ;
            try {
                response.setContentType("text/plain");
                response.addHeader("Content-Disposition",
                       "attachment; filename=" + wordfile.getFileName());
                response.setCharacterEncoding("UTF-8");
                input = WordAction.class.getClassLoader().getResourceAsStream(
                        wordfile.getFileName()) ;
                while(input.read(word) > 0) {
                    response.getOutputStream().write(word);
                    word = new byte[1024] ;
                }
                response.flushBuffer();
            } catch (IOException ex) {
                try {
                    input.close();
                } catch (IOException ex1) {
                }
            }
        }
        return null ;
    }
    public String addNewWord()
    {
        if(newWord!=null&&newWord.trim().length()>0)
        {
//            message = XDChineseTokenizer.saveNewWord(newWord) ;
        }
        return Action.SUCCESS ;
    }
    public String searchWord()
    {
        if(newWord!=null&&newWord.trim().length()>0)
        {
//            message = XDChineseTokenizer.wordSearch(newWord) ;
        }
        return Action.SUCCESS ;
    }

    public List getWordFileList() {
        return wordFileList;
    }

    public Wordfile getWordfile() {
        return wordfile;
    }

    public int getWordFileNum() {
        return wordFileNum;
    }

    public int getWordNum() {
        return wordNum;
    }

    public String getNewWord() {
        return newWord;
    }

    public String getMessage() {
        return message;
    }

    public void setWordFileList(List wordFileList) {
        this.wordFileList = wordFileList;
    }

    public void setWordfile(Wordfile wordfile) {
        this.wordfile = wordfile;
    }

    public void setWordFileNum(int wordFileNum) {
        this.wordFileNum = wordFileNum;
    }

    public void setWordNum(int wordNum) {
        this.wordNum = wordNum;
    }

    public void setNewWord(String newWord) {
        this.newWord = newWord;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
