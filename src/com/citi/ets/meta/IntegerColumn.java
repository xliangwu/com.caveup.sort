package com.citi.ets.meta;

import java.util.ArrayList;
import java.util.List;

public class IntegerColumn extends AbstractColumn<Integer> {

    private final List<Integer> rows = new ArrayList<Integer>(10000);

    @Override
    public Integer getData(int index) {
        return rows.get(index);
    }

    @Override
    public void addRow(String t) {
        rows.add(Integer.parseInt(t));
    }

    @Override
    public void mergeRow(String t, int index) {
        Integer a = getData(index);
        Integer b = Integer.parseInt(t);
        if (null != fun) {
            Integer res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, Integer t) {
        rows.set(index, t);

    }

}
