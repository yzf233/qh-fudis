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

package org.apache.nutch.protocol.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.nutch.util.LogFormatter;
import org.apache.nutch.util.NutchConf;

import org.apache.nutch.db.Page;
import org.apache.nutch.pagedb.FetchListEntry;
import org.apache.nutch.protocol.*;

import com.xx.platform.core.SearchContext;
import com.xx.platform.domain.model.crawl.Proregion;
import com.xx.platform.util.constants.IbeaProperty;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.NameValuePair;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Calendar;
import org.apache.nutch.protocol.httpclient.MultiProperties;
import org.apache.nutch.util.mime.MimeType;
import java.util.Properties;
import org.apache.nutch.util.mime.MimeTypes;
import org.apache.commons.httpclient.Cookie;

/** An implementation of the Http protocol. */
public class Http implements Protocol {

    public static final Logger LOG =
            LogFormatter.getLogger("org.apache.nutch.net.Http");

    public static Map<String,
                      HttpClient> CLIENT_MAP = new HashMap<String, HttpClient>();

    HttpClient client = new HttpClient();


    static {
        if (NutchConf.get().getBoolean("http.verbose", false)) {
            LOG.setLevel(Level.FINE);
        }
    }

    static final int BUFFER_SIZE = 8 * 1024;

    private static final int MAX_REDIRECTS =
            NutchConf.get().getInt("http.redirect.max", 3);

    static String PROXY_HOST = SearchContext.getXdtechsite().getProxy(); //NutchConf.get().get("http.proxy.host");

    static int PROXY_PORT = SearchContext.getXdtechsite().getProxyport(); //NutchConf.get().getInt("http.proxy.port",8080);

    static boolean PROXY = (PROXY_HOST != null && PROXY_HOST.length()>0);

    static int TIMEOUT = NutchConf.get().getInt("http.timeout", 10000);

    static int MAX_CONTENT = NutchConf.get().getInt("http.content.limit",
            64 * 1024);

    static int MAX_DELAYS = NutchConf.get().getInt("http.max.delays", 3);

    static int MAX_THREADS_PER_HOST =
            NutchConf.get().getInt("fetcher.threads.per.host", 1);

    static String AGENT_STRING = getAgentString();

    static long SERVER_DELAY =
            (long) (NutchConf.get().getFloat("fetcher.server.delay", 1.0f) *
                    1000);

    static {
        LOG.info("http.proxy.host = " + PROXY_HOST);
        LOG.info("http.proxy.port = " + PROXY_PORT);

        LOG.info("http.timeout = " + TIMEOUT);
        LOG.info("http.content.limit = " + MAX_CONTENT);
        LOG.info("http.agent = " + AGENT_STRING);

        LOG.info("fetcher.server.delay = " + SERVER_DELAY);
        LOG.info("http.max.delays = " + MAX_DELAYS);
    }

    /** Maps from InetAddress to a Long naming the time it should be unblocked.
     * The Long is zero while the address is in use, then set to now+wait when
     * a request finishes.  This way only one thread at a time accesses an
     * address. */
    private static HashMap BLOCKED_ADDR_TO_TIME = new HashMap();

    /** Maps an address to the number of threads accessing that address. */
    private static HashMap THREADS_PER_HOST_COUNT = new HashMap();

    /** Queue of blocked InetAddress.  This contains all of the non-zero entries
     * from BLOCKED_ADDR_TO_TIME, ordered by increasing time. */
    private static LinkedList BLOCKED_ADDR_QUEUE = new LinkedList();

    private RobotRulesParser robotRules = new RobotRulesParser();

    private static InetAddress blockAddr(URL url) throws ProtocolException {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(url.getHost());
        } catch (UnknownHostException e) {
            throw new HttpException(e);
        }

        int delays = 0;
        while (true) {
            cleanExpiredServerBlocks(); // free held addresses

            Long time;
            synchronized (BLOCKED_ADDR_TO_TIME) {
                time = (Long) BLOCKED_ADDR_TO_TIME.get(addr);
                if (time == null) { // address is free

                    // get # of threads already accessing this addr
                    Integer counter = (Integer) THREADS_PER_HOST_COUNT.get(addr);
                    int count = (counter == null) ? 0 : counter.intValue();

                    count++; // increment & store
                    THREADS_PER_HOST_COUNT.put(addr, new Integer(count));

                    if (count >= MAX_THREADS_PER_HOST) {
                        BLOCKED_ADDR_TO_TIME.put(addr, new Long(0)); // block it
                    }
                    return addr;
                }
            }

            if (delays == MAX_DELAYS) {
                throw new RetryLater(url,
                                     "Exceeded http.max.delays: retry later.");
            }

            long done = time.longValue();
            long now = System.currentTimeMillis();
            long sleep = 0;
            if (done == 0) { // address is still in use
                sleep = SERVER_DELAY; // wait at least delay

            } else if (now < done) { // address is on hold
                sleep = done - now; // wait until its free
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {}
            delays++;
        }
    }

    private static void cleanExpiredServerBlocks() {
        synchronized (BLOCKED_ADDR_TO_TIME) {
            while (!BLOCKED_ADDR_QUEUE.isEmpty()) {
                InetAddress addr = (InetAddress) BLOCKED_ADDR_QUEUE.getLast();
                long time = ((Long) BLOCKED_ADDR_TO_TIME.get(addr)).longValue();
                if (time <= System.currentTimeMillis()) {
                    BLOCKED_ADDR_TO_TIME.remove(addr);
                    BLOCKED_ADDR_QUEUE.removeLast();
                } else {
                    break;
                }
            }
        }
    }

    private static void unblockAddr(InetAddress addr) {
        synchronized (BLOCKED_ADDR_TO_TIME) {
            int addrCount = ((Integer) THREADS_PER_HOST_COUNT.get(addr)).
                            intValue();
            if (addrCount == 1) {
                THREADS_PER_HOST_COUNT.remove(addr);
                BLOCKED_ADDR_QUEUE.addFirst(addr);
                BLOCKED_ADDR_TO_TIME.put
                        (addr,
                         new Long(System.currentTimeMillis() + SERVER_DELAY));
            } else {
                THREADS_PER_HOST_COUNT.put(addr, new Integer(addrCount - 1));
            }
        }
    }

    public ProtocolOutput getProtocolOutput(String urlString) {
        ProtocolOutput output = null;
        try {
            return getProtocolOutput(new FetchListEntry(true,
                    new Page(urlString, 1.0f, 1), new String[0]));
        } catch (MalformedURLException mue) {
            return new ProtocolOutput(null, new ProtocolStatus(mue));
        }
    }


    private int calculateTryToRead(int totalRead) {
        int tryToRead = Http.BUFFER_SIZE;
        if (Http.MAX_CONTENT <= 0) {
            return Http.BUFFER_SIZE;
        } else if (Http.MAX_CONTENT - totalRead < Http.BUFFER_SIZE) {
            tryToRead = Http.MAX_CONTENT - totalRead;
        }
        return tryToRead;
    }

    public String getHeader(String name) {
        Properties headers = new Properties();
        return (String) headers.get(name);
    }


    public ProtocolOutput getProtocolOutput(FetchListEntry fle) {
        String urlString = fle.getUrl().toString();
        int redirects = 0;
        int code = 0;
        boolean checked = false;

        try {
            URL url = new URL(urlString);
            //登陆程序
            java.util.List<Proregion> proregionList = SearchContext.getProregionList();
            for (Proregion proregion:proregionList) {
                if (urlString.indexOf(proregion.getSname()) < 0) {
                    continue;
                }
                //查看以前是否登陆过此网站的cookies是否过期
                HttpClient clients = CLIENT_MAP.get(proregion.getSname());
                if (clients != null) {
                    if (Calendar.getInstance().getTime().after(clients.
                            getState().getCookies()[2].getExpiryDate())) {
                        //Cookies过期，重新登陆
                        client = new HttpClient();
                        client.getHostConfiguration().setHost(proregion.
                                getSname());
                        PostMethod post = new PostMethod(proregion.getTarurl());
                        NameValuePair name = new NameValuePair(proregion.
                                getUsernamefieldname(), proregion.getUsername());
                        NameValuePair pass = new NameValuePair(proregion.
                                getPasswordfieldname(),
                                proregion.getPassword());
                        post.setRequestBody(new NameValuePair[] {name, pass});
                        code = client.executeMethod(post);
                    }
                    checked = true;
                } else {
                    client = new HttpClient();
                    client.getHostConfiguration().setHost(proregion.getSname());
                    PostMethod post = new PostMethod(proregion.getTarurl());
                    NameValuePair name = new NameValuePair(proregion.
                            getUsernamefieldname(), proregion.getUsername());
                    NameValuePair pass = new NameValuePair(proregion.
                            getPasswordfieldname(),
                            proregion.getPassword());
                    post.setRequestBody(new NameValuePair[] {name, pass});
                    code = client.executeMethod(post);
                    checked = true;
                }
            }

            if (checked) {
                GetMethod get = new GetMethod(urlString);

                code = client.executeMethod(get);
                MultiProperties headers = new MultiProperties();

                Header[] heads = get.getResponseHeaders();
                for (int i = 0; i < heads.length; i++) {
                    headers.put(heads[i].getName(), heads[i].getValue());
                }

                byte[] content;
                String base = url.toString();
                String orig = url.toString();

                content = get.getResponseBodyAsString().getBytes();
                get.releaseConnection();
                String contentType = getHeader("Content-Type");
                boolean MAGIC =
                        NutchConf.get().getBoolean("mime.type.magic", true);
                MimeTypes MIME =
                        MimeTypes.get(NutchConf.get().get(
                                "mime.types.file"));

                if (contentType == null) {
                    MimeType type = null;
                    if (MAGIC) {
                        type = MIME.getMimeType(orig, content);
                    } else {
                        type = MIME.getMimeType(orig);
                    }
                    if (type != null) {
                        contentType = type.getName();
                    } else {
                        contentType = "";
                    }
                }
//                System.out.println("code=" + code);
                if (code == 200) { // got a good response
                    return new ProtocolOutput(new Content(orig, base,
                            content,
                            contentType, headers)); // return it

                } else if (code == 410) { // page is gone
                    throw new ResourceGone(url, "Http: " + code);

                } else if (code >= 300 && code < 400) { // handle redirect
                    if (redirects == MAX_REDIRECTS) {
                        throw new HttpException("Too many redirects: " +
                                                urlString);
                    }

                    LOG.fine("redirect to " + url);

                } else { // convert to exception
                    throw new HttpError(code);
                }

                return null;
            }

            while (true) {

                if (!RobotRulesParser.isAllowed(url)) {
                    throw new ResourceGone(url, "Blocked by robots.txt");
                }

                InetAddress addr = null;
                HttpResponse response;
                if (!PROXY) {
                    addr = blockAddr(url);
                }
                try {
                    response = new HttpResponse(urlString, url); // make a request
                } finally {
                    if (!PROXY) {
                        unblockAddr(addr);
                    }
                }

                code = response.getCode();

                if (code == 200) { // got a good response
                    return new ProtocolOutput(response.toContent()); // return it

                } else if (code == 410) { // page is gone
                    throw new ResourceGone(url, "Http: " + code);

                } else if (code >= 300 && code < 400) { // handle redirect
                    if (redirects == MAX_REDIRECTS) {
                        throw new HttpException("Too many redirects: " +
                                                urlString);
                    }
                    url = new URL(url, response.getHeader("Location"));
                    redirects++;
                    LOG.fine("redirect to " + url);

                } else { // convert to exception
                    throw new HttpError(code);
                }
            }
        } catch (Exception e) {
            return new ProtocolOutput(null, new ProtocolStatus(e));
        }
    }

    private static String getAgentString() {
        String agentName = NutchConf.get().get("http.agent.name");
        String agentVersion = NutchConf.get().get("http.agent.version");
        String agentDesc = NutchConf.get().get("http.agent.description");
        String agentURL = NutchConf.get().get("http.agent.url");
        String agentEmail = NutchConf.get().get("http.agent.email");

        if ((agentName == null) || (agentName.trim().length() == 0)) {
            LOG.severe("No User-Agent string set (http.agent.name)!");
        }

        StringBuffer buf = new StringBuffer();

        buf.append(agentName);
        if (agentVersion != null) {
            buf.append("/");
            buf.append(agentVersion);
        }
        if (((agentDesc != null) && (agentDesc.length() != 0))
            || ((agentEmail != null) && (agentEmail.length() != 0))
            || ((agentURL != null) && (agentURL.length() != 0))) {
            buf.append(" (");

            if ((agentDesc != null) && (agentDesc.length() != 0)) {
                buf.append(agentDesc);
                if ((agentURL != null) || (agentEmail != null)) {
                    buf.append("; ");
                }
            }

            if ((agentURL != null) && (agentURL.length() != 0)) {
                buf.append(agentURL);
                if (agentEmail != null) {
                    buf.append("; ");
                }
            }

            if ((agentEmail != null) && (agentEmail.length() != 0)) {
                buf.append(agentEmail);
            }

            buf.append(")");
        }
        return buf.toString();
    }

    /** For debugging. */
    public static void main(String[] args) throws Exception {
        boolean verbose = false;
        String url = null;

        String usage = "Usage: Http [-verbose] [-timeout N] url";

        if (args.length == 0) {
            System.err.println(usage);
            System.exit( -1);
        }

        for (int i = 0; i < args.length; i++) { // parse command line
            if (args[i].equals("-timeout")) { // found -timeout option
                TIMEOUT = Integer.parseInt(args[++i]) * 1000;
            } else if (args[i].equals("-verbose")) { // found -verbose option
                verbose = true;
            } else if (i != args.length - 1) {
                System.err.println(usage);
                System.exit( -1);
            } else { // root is required parameter
                url = args[i];
            }
        }

        Http http = new Http();

        if (verbose) {
            LOG.setLevel(Level.FINE);
        }

        Content content = http.getProtocolOutput(url).getContent();

        System.out.println("Content Type: " + content.getContentType());
        System.out.println("Content Length: " +
                           content.get("Content-Length"));
        System.out.println("Content:");
        String text = new String(content.getContent());
        System.out.println(text);

    }

}
