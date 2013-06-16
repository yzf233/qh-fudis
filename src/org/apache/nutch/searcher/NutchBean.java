/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.nutch.searcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.nutch.fs.LocalFileSystem;
import org.apache.nutch.indexer.IndexSegment;
import org.apache.nutch.ipc.RPC;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseText;
import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

import com.xx.platform.core.SearchContext;
import com.xx.platform.core.nutch.IndexImpl;
import com.xx.platform.core.nutch.IndexInterface;
import com.xx.platform.core.nutch.RuntimeDataCollect;
import com.xx.platform.core.rpc.ImDistributedTool;
import com.xx.platform.core.rpc.ImInterface;
import com.xx.platform.core.rpc.ServiceInterface;
import com.xx.platform.domain.model.distributed.Synchro;
import com.xx.platform.util.tools.ArraysObjectTool;

/**
 * One stop shopping for search-related functionality.
 * 
 * @version $Id: NutchBean.java,v 1.19 2005/02/07 19:10:08 cutting Exp $
 */
public class NutchBean implements Searcher, HitDetailer, HitSummarizer,
		HitContent, DistributedSearch.Protocol, ServiceInterface,
		com.xx.platform.core.nutch.IndexInterface {

	private final static String SERVICE_NAME = "分布式查询";
	private static boolean isInit = false;
	public static final Logger LOG = LogFormatter
			.getLogger("org.apache.nutch.searcher.NutchBean");

	static {
		LogFormatter.setShowThreadIDs(true);
	}

	private String[] segmentNames;

	private IndexInterface indextool;
	private Searcher searcher;
	private HitDetailer detailer;
	private HitSummarizer summarizer;
	private HitContent content;
	// 用来标志当前BEAN是否为分布式创建的
	private boolean diserver = false;
	private static NutchBean bean = null;
	private float RAW_HITS_FACTOR = NutchConf.get().getFloat(
			"searcher.hostgrouping.rawhits.factor", 2.0f);

	/**
	 * BooleanQuery won't permit more than 32 required/prohibited clauses. We
	 * don't want to use too many of those.
	 */
	private static final int MAX_PROHIBITED_TERMS = 20;

	/** Cache in servlet context. */
	public static NutchBean get(ServletContext app) throws IOException {
		if (bean == null) {
			bean = (NutchBean) app.getAttribute("nutchBean");
		}
		if (bean == null) {
			// LOG.info("creating new bean");
			bean = new NutchBean();
			app.setAttribute("nutchBean", bean);
		} else if (bean.isDiserver()
				&& !SearchContext.getXdtechsite().getSudis()) {
			bean = new NutchBean();
			app.setAttribute("nutchBean", bean);
		} else if (!bean.isDiserver()
				&& SearchContext.getXdtechsite().getSudis()) {
			bean = new NutchBean();
			app.setAttribute("nutchBean", bean);
		}
		// System.out.println("分布式NutchBean:"+bean.isDiserver());
		return bean;
	}

	public static NutchBean getBean() throws IOException {
		if (bean == null) {
			bean = new NutchBean();
		}
		if (SearchContext.dir_change || RuntimeDataCollect.has_new_index) {

			bean = new NutchBean();
			SearchContext.dir_change = false;
			RuntimeDataCollect.has_new_index = false;
		}
		return bean;
	}

	/** Construct reading from connected directory. */
	public NutchBean() throws IOException {
		this(new File(SearchContext.search_dir)); // 自定义索引位置路径
	}

	/** Construct in a named directory. */
	public NutchBean(File dir) throws IOException {
		if (SearchContext.getXdtechsite().getSudis()) {
			// System.out.println("初始化分布式NUTCHBENA");
			String[] servers = SearchContext.getDiservers();
			// System.out.println("查询服务器数："+servers.length);
			init(new DistributedSearch.Client(servers));
		} else {
			if (!isInit) {
				// System.out.println("初始化本地NutchBean");
				init(new File(dir, "index"), new File(dir, "segments"));
			}
		}
	}

	private void init(File indexDir, File segmentsDir) throws IOException {
		isInit = true;
		IndexSearcher indexSearcher;
		/**
		 * 初始化查询字段
		 */
		// List<IndexFieldImpl> indexFieldList =
		// SearchContext.getDataHandler().findAllByIObjectCType(IndexFieldImpl.class)
		// ;
		// QueryIndexField categoryQueryIndex = new QueryIndexField("docType") ;
		// for(IndexFieldImpl indexField:indexFieldList)
		// {
		// QueryIndexField queryIndex = new
		// QueryIndexField(indexField.getCode()) ;
		// }
		if (indexDir.exists()) {
			LOG.info("检索索引库位于：" + indexDir.getCanonicalPath());
			indexSearcher = new IndexSearcher(indexDir.getCanonicalPath());
			indextool = new IndexImpl();
		} else {
			LOG.info("检索索引库位于：" + segmentsDir.getCanonicalPath());

			Vector vDirs = new Vector();
			File[] directories = segmentsDir.listFiles();
			for (int i = 0; i < directories.length; i++) {
				File indexdone = new File(directories[i],
						IndexSegment.DONE_NAME);
				if (indexdone.exists() && indexdone.isFile()) {
					vDirs.add(directories[i]);
				}
			}

			directories = new File[vDirs.size()];
			for (int i = 0; vDirs.size() > 0; i++) {
				directories[i] = (File) vDirs.remove(0);
			}

			indexSearcher = new IndexSearcher(directories);
			indextool = new IndexImpl();
		}

		FetchedSegments segments = new FetchedSegments(new LocalFileSystem(),
				segmentsDir.toString());

		this.segmentNames = segments.getSegmentNames();

		this.indextool = indextool;
		this.searcher = indexSearcher;
		this.detailer = indexSearcher;
		this.summarizer = segments;
		this.content = segments;
		isInit = false;
		this.diserver = false;
	}

	private void init(DistributedSearch.Client client) throws IOException {
		this.segmentNames = client.getSegmentNames();
		this.indextool = client;
		this.searcher = client;
		this.detailer = client;
		this.summarizer = client;
		this.content = client;
		this.diserver = true;
	}

	public String[] getSegmentNames() {
		return segmentNames;
	}

	public IndexInterface getIndextool() {
		return indextool;
	}

	public Hits search(Query query, int numHits) throws IOException {
		return search(query, numHits, null, null, false);
	}

	public Hits search(Query query, int numHits, String dedupField,
			String sortField, boolean reverse) throws IOException {
		return searcher.search(query, numHits, dedupField, sortField, reverse);
	}

	/**
	 * 数据修复使用，强制关闭Segmeng文件
	 * 
	 * @param segmentName
	 *            String
	 */
	public void closeSegment(String segmentName) {
		if (((FetchedSegments) this.summarizer).getSegment(segmentName) != null)
			((FetchedSegments) this.summarizer).getSegment(segmentName).close();
	}

	private class DupHits extends ArrayList {
		private boolean maxSizeExceeded;
	}

	/**
	 * Search for pages matching a query, eliminating excessive hits from the
	 * same site. Hits after the first <code>maxHitsPerDup</code> from the
	 * same site are removed from results. The remaining hits have {@link
	 * Hit#moreFromDupExcluded()} set.
	 * <p>
	 * If maxHitsPerDup is zero then all hits are returned.
	 * 
	 * @param query
	 *            query
	 * @param numHits
	 *            number of requested hits
	 * @param maxHitsPerDup
	 *            the maximum hits returned with matching values, or zero
	 * @return Hits the matching hits
	 * @throws IOException
	 */
	public Hits search(Query query, int numHits, int maxHitsPerDup)
			throws IOException {
		return search(query, numHits, maxHitsPerDup, "site", null, false);
	}

	/**
	 * Search for pages matching a query, eliminating excessive hits with
	 * matching values for a named field. Hits after the first
	 * <code>maxHitsPerDup</code> are removed from results. The remaining hits
	 * have {@link Hit#moreFromDupExcluded()} set.
	 * <p>
	 * If maxHitsPerDup is zero then all hits are returned.
	 * 
	 * @param query
	 *            query
	 * @param numHits
	 *            number of requested hits
	 * @param maxHitsPerDup
	 *            the maximum hits returned with matching values, or zero
	 * @param dedupField
	 *            field name to check for duplicates
	 * @return Hits the matching hits
	 * @throws IOException
	 */
	public Hits search(Query query, int numHits, int maxHitsPerDup,
			String dedupField) throws IOException {
		return search(query, numHits, maxHitsPerDup, dedupField, null, false);
	}

	/**
	 * Search for pages matching a query, eliminating excessive hits with
	 * matching values for a named field. Hits after the first
	 * <code>maxHitsPerDup</code> are removed from results. The remaining hits
	 * have {@link Hit#moreFromDupExcluded()} set.
	 * <p>
	 * If maxHitsPerDup is zero then all hits are returned.
	 * 
	 * @param query
	 *            query
	 * @param numHits
	 *            number of requested hits
	 * @param maxHitsPerDup
	 *            the maximum hits returned with matching values, or zero
	 * @param dedupField
	 *            field name to check for duplicates
	 * @param sortField
	 *            Field to sort on (or null if no sorting).
	 * @param reverse
	 *            True if we are to reverse sort by <code>sortField</code>.
	 * @return Hits the matching hits
	 * @throws IOException
	 */
	public Hits search(Query query, int numHits, int maxHitsPerDup,
			String dedupField, String sortField, boolean reverse)
			throws IOException {
		if (SearchContext.dir_change || RuntimeDataCollect.has_new_index) {

			bean = new NutchBean();
			SearchContext.dir_change = false;
			RuntimeDataCollect.has_new_index = false;
		}

		if (!bean.searcher.equals(searcher)) {
			this.segmentNames = bean.segmentNames;
			this.indextool = bean.indextool;
			this.searcher = bean.searcher;
			this.detailer = bean.detailer;
			this.summarizer = bean.summarizer;
			this.content = bean.content;
			isInit = false;
			this.diserver = false;
		}

		// System.out.println("maxHitsPerDup;"+maxHitsPerDup);
		if (maxHitsPerDup <= 0) // disable dup checking
			return search(query, numHits, dedupField, sortField, reverse);

		int numHitsRaw = (int) (numHits * RAW_HITS_FACTOR);
		if (searcher == null)
			return new Hits();
		// System.out.println("查询参数;"+query.getXdht());
		// String[] terms = query.getTerms();
		// for (int i = 0; i < terms.length; i++) {
		// System.out.println(terms[i]);
		// }
		Hits hits = searcher.search(query, numHitsRaw, dedupField, sortField,
				reverse);
		// System.out.println("结果数："+hits.getLength()+" "+hits.getTotal());
		long total = hits.getTotal();
		Map dupToHits = new HashMap();
		List resultList = new ArrayList();
		Set seen = new HashSet();
		List excludedValues = new ArrayList();
		boolean totalIsExact = true;
		for (int rawHitNum = 0; rawHitNum < hits.getTotal(); rawHitNum++) {
			// get the next raw hit
			if (rawHitNum >= hits.getLength()) {
				// optimize query by prohibiting more matches on some excluded
				// values
				Query optQuery = (Query) query.clone();
				for (int i = 0; i < excludedValues.size(); i++) {
					if (i == MAX_PROHIBITED_TERMS)
						break;
					optQuery.addProhibitedTerm(
							((String) excludedValues.get(i)), dedupField);
				}
				numHitsRaw = (int) (numHitsRaw * RAW_HITS_FACTOR);

				hits = searcher.search(optQuery, numHitsRaw, dedupField,
						sortField, reverse);

				rawHitNum = -1;
				continue;
			}

			Hit hit = hits.getHit(rawHitNum);

			if (seen.contains(hit))
				continue;
			seen.add(hit);

			// get dup hits for its value
			String value = hit.getDedupValue();

			DupHits dupHits = (DupHits) dupToHits.get(value);

			// /quhuan 06/12/06 修正dedupField=null时。。bug
			if (dupHits == null)
				dupToHits.put(value, dupHits = new DupHits());
			// if (dupHits == null){
			// dupHits = new DupHits();
			// if(value!=null)
			// dupToHits.put(value, dupHits = new DupHits());
			// }
			// /修改结束

			// does this hit exceed maxHitsPerDup?
			if (dupHits.size() == maxHitsPerDup) { // yes -- ignore the hit
				if (!dupHits.maxSizeExceeded) {

					// mark prior hits with moreFromDupExcluded
					for (int i = 0; i < dupHits.size(); i++) {
						((Hit) dupHits.get(i)).setMoreFromDupExcluded(true);
					}
					dupHits.maxSizeExceeded = true;

					excludedValues.add(value); // exclude dup
				}
				totalIsExact = false;
			} else { // no -- collect the hit
				resultList.add(hit);
				dupHits.add(hit);

				// are we done?
				// we need to find one more than asked for, so that we can tell
				// if
				// there are more hits to be shown
				if (resultList.size() > numHits)
					break;
			}
		}

		Hits results = new Hits(total, (Hit[]) resultList
				.toArray(new Hit[resultList.size()]));
		results.setTotalIsExact(totalIsExact);
		// System.out.println("返回之前："+results.getTotal()+"
		// "+results.getLength());
		return results;
	}

	public String getExplanation(Query query, Hit hit) throws IOException {
		return searcher.getExplanation(query, hit);
	}

	public HitDetails getDetails(Hit hit) throws IOException {
		return detailer.getDetails(hit);
	}

	public HitDetails[] getDetails(Hit[] hits) throws IOException {
		return detailer.getDetails(hits);
	}

	public String getSummary(HitDetails hit, Query query) throws IOException {
		return summarizer.getSummary(hit, query);
	}

	public String[] getSummary(HitDetails[] hits, Query query)
			throws IOException {
		return summarizer.getSummary(hits, query);
	}

	public byte[] getContent(HitDetails hit) throws IOException {
		return content.getContent(hit);
	}

	public ParseData getParseData(HitDetails hit) throws IOException {
		return content.getParseData(hit);
	}

	public ParseText getParseText(HitDetails hit) throws IOException {
		return content.getParseText(hit);
	}

	public String[] getAnchors(HitDetails hit) throws IOException {
		return content.getAnchors(hit);
	}

	public long getFetchDate(HitDetails hit) throws IOException {
		return content.getFetchDate(hit);
	}

	/** For debugging. */
	public static void main(String[] args) throws Exception {
		String usage = "NutchBean query";

		if (args.length == 0) {
			System.err.println(usage);
			System.exit(-1);
		}

		NutchBean bean = new NutchBean();
		Query query = Query.parse(args[0]);

		Hits hits = bean.search(query, 10);
		System.out.println("Total hits: " + hits.getTotal());
		int length = (int) Math.min(hits.getTotal(), 10);
		Hit[] show = hits.getHits(0, length);
		HitDetails[] details = bean.getDetails(show);
		String[] summaries = bean.getSummary(details, query);

		for (int i = 0; i < hits.getLength(); i++) {
			System.out.println(" " + i + " " + details[i]); // + "\n" +
															// summaries[i]);
		}
	}

	public String getName() {
		return SERVICE_NAME;
	}

	public boolean deleteDocuments(int ino, int docno) throws IOException {
		return this.indextool.deleteDocuments(ino, docno);
	}

	public int deleteDocuments(String field, String value) throws IOException {
		return deleteDocuments(field, value, true);
	}

	public int deleteDocuments(String field, String value, boolean command)
			throws IOException {
		if(command){
			List<Synchro> synchro = SearchContext.getSynchroList();
			if (ImDistributedTool.isRuning&&ImDistributedTool.isReady && synchro != null&& synchro.size() > 0)
				for (Synchro s : synchro) {// 遍历每个节点
					try {
						((ImInterface) RPC.getProxy(ImInterface.class,ImDistributedTool.getNode(s.getIpaddress()),SearchContext.synChroTiomeOut)).
						deleteOneIndexByField(ArraysObjectTool.ObjectToArrays(field),ArraysObjectTool.ObjectToArrays(value));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
		return this.indextool.deleteDocuments(field, value);
	}

	public void deleteDocuments(Query query) throws IOException {
		this.indextool.deleteDocuments(query);
	}

	public boolean isDiserver() {
		return diserver;
	}

	public void setDiserver(boolean diserver) {
		this.diserver = diserver;
	}
}
