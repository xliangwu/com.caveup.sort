package com.citi.ets.meta;

import java.util.ArrayList;
import java.util.List;

public class StringColumn extends AbstractColumn<String> {

    private final List<String> rows = new ArrayList<String>(10000);

    @Override
    public String getData(int index) {
        return rows.get(index);
    }

    @Override
    public void addRow(String t) {
        rows.add(t);
    }

    @Override
    public void mergeRow(String t, int index) {
        String a = getData(index);
        String b = t;
        if (null != fun) {
            String res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, String t) {
        rows.set(index, t);

    }

}
