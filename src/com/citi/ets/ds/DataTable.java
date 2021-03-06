package com.citi.ets.ds;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import com.citi.ets.meta.Column;

public final class DataTable {

    private Column<?>[] columns;
    private Map<String, Integer> groupByCacheMap = new HashMap<String, Integer>(10000);
    private int rowIndex = 0;
    private Integer[] referIndex;
    private boolean[] grouped;

    public void sort() {
        referIndex = new Integer[rowIndex];
        for (int i = 0; i < rowIndex; i++) {
            referIndex[i] = i;
        }

        // Arrays.sort(referIndex, new Comparator<Integer>() {
        //
        // @SuppressWarnings({ "rawtypes", "unchecked" })
        // @Override
        // public int compare(Integer o1, Integer o2) {
        // int res = 0;
        // for (int i = 0; i < columns.length; i++) {
        // Comparable<Object> a = (Comparable) columns[i].getData(o1);
        // Comparable<Object> b = (Comparable) columns[i].getData(o2);
        // if (columns[i].getOrder()) {
        // res = a.compareTo(b);
        // } else {
        // res = b.compareTo(a);
        // }
        //
        // if (res != 0) {
        // return res;
        // }
        // }
        // return 0;
        // }
        // });
    }

    public void appendByte(final ByteBuffer buffer, int index) {
        for (int i = 0; i < columns.length; i++) {
            columns[i].appendToBuffer(buffer, referIndex[index]);
            if (i < columns.length - 1)
                buffer.put((byte) ',');
        }
    }

    public void addRow(String input) {
        StringBuilder keyBuilder = new StringBuilder(40);
        String[] fields = parse(keyBuilder, input, columns.length, ',');

        Column<?> col = null;
        String f = null;
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

    public void addRow(String[] fields) {
        StringBuilder keyBuilder = new StringBuilder(40);
        for (int i = 0; i < fields.length; i++) {
            if (grouped[i]) {
                keyBuilder.append(fields[i]);
            }
        }

        Column<?> col = null;
        String f = null;
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

    private String[] parse(StringBuilder key, String input, int size, char sep) {
        String[] res = new String[size];
        int inputLen = input.length();
        int lastIndex = 0;
        int index = 0;
        for (int i = 0; i < inputLen; i++) {
            if (input.charAt(i) == sep) {
                res[index] = input.substring(lastIndex, i);
                if (grouped[index]) {
                    key.append(res[index]);
                }
                index++;
                lastIndex = i + 1;
            }
        }

        if (input.charAt(inputLen - 1) != sep) {
            res[index] = input.substring(lastIndex);
            if (grouped[index]) {
                key.append(res[index]);
            }
            index++;
        }
        return res;
    }

    public void clearCache() {
        groupByCacheMap = null;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setColumns(Column<?>[] columns) {
        this.columns = columns;
        grouped = new boolean[columns.length];
        for (int i = 0; i < columns.length; i++) {
            grouped[i] = columns[i].isGrouped();
        }
    }

    public Column<?>[] getColumns() {
        return columns;
    }
}
