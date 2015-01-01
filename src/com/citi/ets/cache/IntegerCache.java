package com.citi.ets.cache;

import java.util.HashMap;
import java.util.Map;

public final class IntegerCache {

    private static final Map<String, Integer> intCacheMap = new HashMap<String, Integer>();
    private static final IntegerCache SINGLE_INSTANCE = new IntegerCache();

    private IntegerCache() {
    }

    public static IntegerCache getInstance() {
        return SINGLE_INSTANCE;
    }

    public int getInteger(String input) {
        Integer res = intCacheMap.get(input);
        if (null == res) {
            try {
                res = Integer.parseInt(input);
                intCacheMap.put(input, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res.intValue();
    }

    public void clear() {
        intCacheMap.clear();
    }
}
