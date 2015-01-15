package com.citi.ets.meta;

import java.util.ArrayList;
import java.util.List;

public class DoubleColumn extends AbstractColumn<Double> {

    private final List<Double> rows = new ArrayList<Double>(10000);

    @Override
    public Double getData(int index) {
        return rows.get(index);
    }

    @Override
    public void addRow(String t) {
        rows.add(Double.parseDouble(t));
    }

    @Override
    public void mergeRow(String t, int index) {
        double a = getData(index);
        double b = Double.parseDouble(t);
        if (null != fun) {
            Double res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, Double t) {
        rows.set(index, t);
    }

}
