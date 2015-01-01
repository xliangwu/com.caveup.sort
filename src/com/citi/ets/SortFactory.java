package com.citi.ets;

import com.citi.ets.sort.impl.PCSort;

public class SortFactory {

    public static Sort getSort(SortMethod method) {
        Sort sort = null;
        if (method == SortMethod.DEFAULT) {
            sort = new PCSort();
        }
        return sort;
    }
}
