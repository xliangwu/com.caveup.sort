package com.citi.ets;

import com.citi.ets.sort.impl.NioByteSort;
import com.citi.ets.sort.impl.CommonSort;


public class SortFactory {

    public static Sort getSort(SortMethod method) {
        Sort sort = null;
        if (method == SortMethod.DEFAULT) {
            sort = new CommonSort();
        }
        
        if (method == SortMethod.NIO_BYTE_SORT) {
            sort = new NioByteSort();
        }
        
        
        return sort;
    }
}
