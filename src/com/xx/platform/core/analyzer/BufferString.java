package com.xx.platform.core.analyzer;

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
public class BufferString {
    char[] buffer;
    int length;
    int hasUsed = 0;
    public BufferString(int length) {
        buffer = new char[this.length = length];
    }

    public BufferString append(char character) {
        buffer[hasUsed++] = character;
        return this;
    }

    public void setLength(int length) {
        java.util.Arrays.fill(buffer, hasUsed = length, this.length,
                                                (char) 0);
    }

    public long length() {
        return hasUsed;
    }

    public String toString() {
        return new String(buffer, 0, hasUsed);
    }
}
