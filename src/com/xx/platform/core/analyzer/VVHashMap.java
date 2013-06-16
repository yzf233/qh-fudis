package com.xx.platform.core.analyzer;
/**
 *
 * <p>Title: HashMap</p>
 *
 * <p>Description: 重载 HashMap put 方法</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: xd</p>
 *
 * @author qh
 * @version 1.0
 */
import java.util.HashMap;

public class VVHashMap<K, V> extends HashMap<K, V> {
        private boolean range = false ;
        private String vcode = null;//代码

        public VVHashMap(int initialCapacity, float loadFactor , boolean range) {
            super(initialCapacity, loadFactor);
            this.range = range ;
            this.vcode = null;
        }

        public VVHashMap(int initialCapacity, float loadFactor , boolean range,String vcode) {
            super(initialCapacity, loadFactor);
            this.range = range ;
            this.vcode = vcode;
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
        public String getVcode() {
            return vcode;
        }

        public void setVcode(String vcode) {
            this.vcode = vcode;
        }
    }
