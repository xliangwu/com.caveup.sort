package com.citi.ets.ds;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import com.citi.ets.meta.Column;

public final class DataTable {

    private Column<?>[] columns;
    private Map<String, Integer> groupByCacheMap = new HashMap<String, Integer>();
    private int rowIndex = 0;
    private Integer[] referIndex;

    public void sort() {
        referIndex = new Integer[rowIndex];
        for (int i = 0; i < rowIndex; i++) {
            referIndex[i] = i;
        }

        Arrays.sort(referIndex, new Comparator<Integer>() {

            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            public int compare(Integer o1, Integer o2) {
                int res = 0;
                for (int i = 0; i < columns.length; i++) {
                    Comparable<Object> a = (Comparable) columns[i].getData(o1);
                    Comparable<Object> b = (Comparable) columns[i].getData(o2);
                    if (columns[i].getOrder()) {
                        res = a.compareTo(b);
                    } else {
                        res = b.compareTo(a);
                    }

                    if (res != 0) {
                        return res;
                    }
                }
                return 0;
            }
        });
    }

    public String getRow(int index) {
        StringBuilder res = new StringBuilder(50);
        for (Column<?> col : columns) {
            res.append(col.getFormatData(referIndex[index]));
            res.append(",");
        }

        res.deleteCharAt(res.length() - 1);
        return res.toString();
    }

    public void addRow(String input) {
        String[] fields = input.split(",");

        StringBuilder keyBuilder = new StringBuilder();
        Column<?> col = null;
        String f = null;
        for (int i = 0; i < fields.length; i++) {
            col = columns[i];
            f = fields[i];

            if (col.isGrouped()) {
                keyBuilder.append(f).append("_");
            }
        }

        Integer index = null;
        String key = keyBuilder.toString();
        index = groupByCacheMap.get(key);
        if (null != index) {
            for (int i = 0; i < fields.length; i++) {
                col = columns[i];
                f = fields[i];
                col.mergeRow(f, index);
            }
        } else {
            groupByCacheMap.put(key, rowIndex);
            rowIndex++;
            for (int i = 0; i < fields.length; i++) {
                col = columns[i];
                f = fields[i];
                col.addRow(f);
            }
        }

    }

    public void clearCache() {
        groupByCacheMap = null;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setColumns(Column<?>[] columns) {
        this.columns = columns;
    }
}
