package com.xx.platform.web.actions.index;

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
public class Wordfile {
    private String fileName;
    private long fileSize;
    private long lastModi;
    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastModi() {
        return lastModi;
    }

    public void setLastModi(long lastModi) {
        this.lastModi = lastModi;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
