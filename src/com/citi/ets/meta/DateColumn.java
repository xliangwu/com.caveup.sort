package com.citi.ets.meta;

import java.util.ArrayList;
import java.util.List;
import com.citi.ets.cache.DateCache;

public class DateColumn extends AbstractColumn<Long> {

    private final List<Long> rows = new ArrayList<Long>(10000);

    @Override
    public Long getData(int index) {
        return rows.get(index);
    }

    @Override
    public String getFormatData(int index) {
        return DateCache.getInstance().formatDate(getData(index));
    }

    @Override
    public void addRow(String t) {
        rows.add(DateCache.getInstance().getDate(t).getTime());
    }

    @Override
    public void mergeRow(String t, int index) {
        Long a = getData(index);
        Long b = DateCache.getInstance().getDate(t).getTime();
        if (null != fun) {
            Long res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, Long t) {
        rows.set(index, t);

    }

}
