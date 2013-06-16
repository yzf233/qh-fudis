package org.apache.lucene.analysis;

import java.io.IOException;

import com.xx.platform.core.StringUtils;

public final class DESFilter extends TokenFilter {

	public DESFilter(TokenStream input) {
		super(input);
	}
	public final Token next(final Token reusableToken) throws IOException {
		    assert reusableToken != null;
		    Token nextToken = input.next(reusableToken);
		    if (nextToken != null) {
		      char[] buffer = nextToken.termBuffer();
		      String str=new String(buffer,0,nextToken.termLength());
		      String des= StringUtils.encrypt(str);
		      nextToken.setTermBuffer(des.toCharArray(), 0, des.toCharArray().length);
		      return nextToken;
		    } else
		      return null;
	}
}
