package com.citi.ets;

import com.citi.ets.sort.impl.NioByteSort;


public class SortFactory {

    public static Sort getSort(SortMethod method) {
        Sort sort = null;
        if (method == SortMethod.DEFAULT) {
            sort = new NioByteSort();
        }
        return sort;
    }
}
