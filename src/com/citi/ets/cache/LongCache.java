package com.citi.ets.cache;

import java.util.HashMap;
import java.util.Map;

public final class LongCache {

    private static final Map<String, Long> intCacheMap = new HashMap<String, Long>();
    private static final LongCache SINGLE_INSTANCE = new LongCache();

    private LongCache() {
    }

    public static LongCache getInstance() {
        return SINGLE_INSTANCE;
    }

    public Long getLong(String input) {
        Long res = intCacheMap.get(input);
        if (null == res) {
            try {
                res = Long.parseLong(input);
                intCacheMap.put(input, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public void clear() {
        intCacheMap.clear();
    }
}
