package com.citi.ets.cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class DateCache {

    private static final SimpleDateFormat DF = new SimpleDateFormat("MM/dd/yyyy");

    private static final Map<String, Date> dateCacheMap = new HashMap<String, Date>();
    private static final DateCache SINGLE_INSTANCE = new DateCache();
    private static final ThreadLocal<SimpleDateFormat> localDateFormat = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM/dd/yyyy");
        }
    };

    private DateCache() {
    }

    public static DateCache getInstance() {
        return SINGLE_INSTANCE;
    }

    public String formatDate(Date date) {
        return localDateFormat.get().format(date);
    }

    public String formatDate(Long date) {
        return localDateFormat.get().format(date);
    }

    public Date getDate(String input) {
        Date res = dateCacheMap.get(input);
        if (null == res) {
            try {
                res = DF.parse(input);
                dateCacheMap.put(input, res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public void clear() {
        dateCacheMap.clear();
    }
}
