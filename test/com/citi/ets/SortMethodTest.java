package com.citi.ets;

import java.util.Arrays;
import org.junit.Test;

public class SortMethodTest {

    @Test
    public void test() {
        int size = 5000000;
        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = size - i;
        }
        long start = System.currentTimeMillis();
        Arrays.sort(a);
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }
}
