package com.citi.ets.meta;

import com.citi.ets.funs.Function;

public abstract class AbstractColumn<T extends Comparable<T>> implements Column<T> {

    protected boolean grouped = false;
    protected boolean orderByAsc = true;
    protected Function<T> fun;

    @Override
    public void setGrouped(boolean boo) {
        this.grouped = boo;
    }

    @Override
    public boolean getOrder() {
        return orderByAsc;
    }

    @Override
    public String getFormatData(int index) {
        return String.valueOf(getData(index));
    }

    @Override
    public boolean isGrouped() {
        return grouped;
    }

    @Override
    public void setFunction(Function<T> fun) {
        this.fun = fun;
    }

    @Override
    public void setOrder(boolean asc) {
        this.orderByAsc = asc;
    }

    @Override
    public String getKey(int index) {
        if (!isGrouped())
            return null;
        return String.valueOf(getData(index));
    }
}
