package com.xx.platform.core.nutch;

import java.util.List;

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
public interface AbstractScreen {
    /**
     * 获得屏蔽词列表
     * @return List
     * @throws Exception
     */
    public List<String> getScreenWord() throws Exception;
    /**
     * 加入新的屏蔽词
     * @param screenWord String
     * @throws Exception
     */
    public void putNewScreenWord(String screenWord) throws Exception ;
    /**
     * 删除屏蔽词
     * @param screenWord String
     * @throws Exception
     */
    public void removeScreenWord(String screenWord) throws Exception ;
}
