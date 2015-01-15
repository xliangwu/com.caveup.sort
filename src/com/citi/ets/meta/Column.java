package com.citi.ets.meta;

import com.citi.ets.funs.Function;

public interface Column<T extends Comparable<T>> {

    public T getData(int index);

    public String getFormatData(int index);

    public void setData(int index, T t);

    public void addRow(String t);

    public void setFunction(Function<T> fun);

    public void mergeRow(String t, int index);

    public void setGrouped(boolean boo);

    public boolean isGrouped();

    public String getKey(int index);

    public void setOrder(boolean asc);

    public boolean getOrder();
}
