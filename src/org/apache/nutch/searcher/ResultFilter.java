package org.apache.nutch.searcher;

import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.db.BerkeleyDB;

public class ResultFilter extends Filter {
	private static BitSet bitSet=new BitSet();

	public BitSet bits(IndexReader reader) throws IOException {
		return bitSet;
	}
	public static void addResultBitSet(String id)
	{
		if(id!=null&&id.matches("[\\d]?"))
			bitSet.set(Integer.valueOf(id),true);
	}
}
