package com.xx.platform.core.analyzer;

import java.util.*;

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
/**
 *
 * <p>Title: HashMap</p>
 *
 * <p>Description: ���� HashMap put ����</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable �����ߵ�Ƽ����޹�˾
 * @version 1.0
 */
public class VHashMap<K, V> extends HashMap<K, V> {
    private boolean range = false ;
    public VHashMap(int initialCapacity, float loadFactor , boolean range) {
        super(initialCapacity, loadFactor);
        this.range = range ;
    }

    public V put(K key, V value) {
        super.put(key, value);
        return value;
    }

    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }
}
