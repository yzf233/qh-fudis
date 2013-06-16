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
	        String test = "周三沪深市场表现为宽幅振荡的走势，上涨股票的数量明显多于下跌股票的数量，两市有近60只股票报收涨停板位置，显示市场做多的力量仍然充裕。从市场盘面来看，在上午股指宽幅震荡过程中，部分蓝筹股的走强稳定了军心，成为股指最终以红盘报收的关键。比如大秦铁路，中国铝业，五粮液，交通银行等。由于上述个股，均是以基金为首的机构投资者重藏品中，其逆势走强，绝非一般资金行为，因此从市场主流品种活跃来看，机构投资者在目前点位，大肆做空迹象并不明显。　　 那么对于后市，5000点之上多空分歧加剧，市场走向何方依然将是未知数。但对于身在其中的主力资金而言，无论是从进一步扩展空间，还是从全身而退的战略角度，均需要一只带动市场人气的领涨龙头品种。如昔日中海发展，中信证券那样的人气龙头品种。而真正的龙头品种，要具备成交活跃适合大资金进出，价值低估并具成长空间，绝对价格适中易得到市场认同等优势，并且必须得到主流资金的关照。敏感时期，市场呼唤领涨龙头，这正是短期内重要的选股思路。　　 从周三盘面来看，大秦铁路巨量涨停，具备了龙头的风范，但该股的缺点在于绝对价格较高，并且短期涨幅过大，后市宽幅震荡不可避免。那么两市之中是否还存在具备龙头风范的滞涨大盘蓝筹股呢？交通银行  （601328 行情,资料,评论,搜索）携手汇丰银行，引起了我们的关注。　　 交通银行  （601328 行情,资料,评论,搜索）第二大股东为香港上海汇丰银行，持股数量与第一大股东财政部十分接近，公司的外资概念十分正宗。由于该股具备成交活跃适合大资金进出，价值低估并具成长空间，绝对价格适中易得到市场认同等优势。同时该股7月20日至今股价涨幅仅20%，是银行板块涨幅最小的品种之一。近期盘面经常出现百万级大单，这绝非一般资金所为，该股能否一鸣惊人突破上市高点成为市场新龙头，无疑值得期待。"

	          ;

	        XdOneKeyTokenizer bree = new XdOneKeyTokenizer(new StringReader(
	                  test));
	          Token token;
	          while ((token = bree.next()) != null) {
	              {
	                  System.out.println("字符：" + token.termText() + " 开始：" +
	                                     token.startOffset() + " 结束：" +
	                                     token.endOffset());
	              }
	          }
	      } catch (Exception ex) {
	          ex.printStackTrace();
	      }
		
	}

}
