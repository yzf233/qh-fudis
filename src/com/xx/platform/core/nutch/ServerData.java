package com.xx.platform.core.nutch;

import java.io.IOException;

/**
 * ������������Ϣ �� �������� ���ֲ�ʽ�������������� �����е�server ������ Segment������
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
public interface ServerData {
    public int getIndexRecNum() throws IOException ;
    public int getServerIndexRecNum() throws IOException ;
    public int getServerNum() throws IOException ;
    public int getSegmentsNum() throws IOException ;
    public int getServerSegmentsNum() throws IOException ;
}
