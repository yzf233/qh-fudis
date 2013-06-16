package com.xx.platform.core.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

public class XdOneKeyTokenizer extends Tokenizer {
	private int iHZ = 0, iStart = 0;
	private String iToken="";  
	public XdOneKeyTokenizer(Reader reader) {
		    this.input = reader;
		  }
	/**
	 * @param args
	 */
	  private char[] charb = new char[1024];
	  public void tokenize() throws IOException {
		    StringBuffer strb = new StringBuffer();

		    int length;
		    while ( (length = input.read(charb)) > 0) {
		      strb.append(new String(charb, 0, length));
		    }
		    this.iToken = strb.toString();
	  }
	  public Token next() throws IOException {
		if(iToken.equals(""))
			tokenize();
		if(iToken==null||iToken.length()==0)
			return null;
	    int oldStart = iStart;
	    String tokenchar="";
	    if (iStart<iToken.length()) {
	    	tokenchar = iToken.substring(oldStart,oldStart+1);
	      iStart ++;
	      return new Token(tokenchar.toLowerCase(), oldStart, iStart);
	    }
	    else {
	      return null;
	    }
	  }
	public static void main(String[] args) {
	    try {
	        String test = "���������г�����Ϊ����񵴵����ƣ����ǹ�Ʊ���������Զ����µ���Ʊ�������������н�60ֻ��Ʊ������ͣ��λ�ã���ʾ�г������������Ȼ��ԣ�����г������������������ָ����𵴹����У���������ɵ���ǿ�ȶ��˾��ģ���Ϊ��ָ�����Ժ��̱��յĹؼ������������·���й���ҵ������Һ����ͨ���еȡ������������ɣ������Ի���Ϊ�׵Ļ���Ͷ�����ز�Ʒ�У���������ǿ������һ���ʽ���Ϊ����˴��г�����Ʒ�ֻ�Ծ����������Ͷ������Ŀǰ��λ���������ռ��󲢲����ԡ����� ��ô���ں��У�5000��֮�϶�շ���Ӿ磬�г�����η���Ȼ����δ֪�����������������е������ʽ���ԣ������Ǵӽ�һ����չ�ռ䣬���Ǵ�ȫ����˵�ս�ԽǶȣ�����Ҫһֻ�����г�������������ͷƷ�֡��������к���չ������֤ȯ������������ͷƷ�֡�����������ͷƷ�֣�Ҫ�߱��ɽ���Ծ�ʺϴ��ʽ��������ֵ�͹����߳ɳ��ռ䣬���Լ۸������׵õ��г���ͬ�����ƣ����ұ���õ������ʽ�Ĺ��ա�����ʱ�ڣ��г�����������ͷ�������Ƕ�������Ҫ��ѡ��˼·������ ����������������������·������ͣ���߱�����ͷ�ķ緶�����ùɵ�ȱ�����ھ��Լ۸�ϸߣ����Ҷ����Ƿ����󣬺��п���𵴲��ɱ��⡣��ô����֮���Ƿ񻹴��ھ߱���ͷ�緶�����Ǵ���������أ���ͨ����  ��601328 ����,����,����,������Я�ֻ�����У����������ǵĹ�ע������ ��ͨ����  ��601328 ����,����,����,�������ڶ���ɶ�Ϊ����Ϻ�������У��ֹ��������һ��ɶ�������ʮ�ֽӽ�����˾�����ʸ���ʮ�����ڡ����ڸùɾ߱��ɽ���Ծ�ʺϴ��ʽ��������ֵ�͹����߳ɳ��ռ䣬���Լ۸������׵õ��г���ͬ�����ơ�ͬʱ�ù�7��20������ɼ��Ƿ���20%�������а���Ƿ���С��Ʒ��֮һ���������澭�����ְ��򼶴󵥣������һ���ʽ���Ϊ���ù��ܷ�һ������ͻ�����иߵ��Ϊ�г�����ͷ������ֵ���ڴ���"

	          ;

	        XdOneKeyTokenizer bree = new XdOneKeyTokenizer(new StringReader(
	                  test));
	          Token token;
	          while ((token = bree.next()) != null) {
	              {
	                  System.out.println("�ַ���" + token.termText() + " ��ʼ��" +
	                                     token.startOffset() + " ������" +
	                                     token.endOffset());
	              }
	          }
	      } catch (Exception ex) {
	          ex.printStackTrace();
	      }
		
	}

}
