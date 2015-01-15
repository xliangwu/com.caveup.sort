package com.citi.ets;

public interface Row {

    public Object[] getColumns();

    public void setColumn(Object newValue, int col);
}
