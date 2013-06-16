package com.xx.platform.core.nutch;

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
public class NutchCommand {
    public static boolean CRAWL_COMMAND_FILECRAWLER=false;
    public static boolean CRAWL_COMMAND_CRAWLER=false;
    public static final String CRAWL_COMMAND = "crawl" ;
    public static final String CRAWL_STATUS_RUNNING = "Running" ;
    public static final String CRAWL_STATUS_NOT_RUNNING = "Not Running" ;
    public static final String CRAWL_STATUS_IDLE = "Idle" ;
    public static final String CRAWL_STATUS_STOPPING = "Stopping" ;
    private static String command ;
    private static boolean search = true ;
    private static boolean crawl = false ;

    public static boolean isCrawl() {
        return crawl;
    }

    public static String getCommand() {
        return NutchCommand.command;
    }

    public boolean isSearch() {
        return search;
    }

    public static void setCrawl(boolean crawl) {
        NutchCommand.crawl = crawl;
    }

    public static void setCommand(String command) {
        NutchCommand.command = command;
    }

    public static void setSearch(boolean search) {
        NutchCommand.search = search;
    }
    public static boolean getSearch() {
            return NutchCommand.search;
    }
}
