package com.citi.ets;

import java.io.File;
import java.util.Arrays;
import org.junit.Test;
import com.citi.ets.sort.impl.CommonSort;

public class PCSortTest {

    @Test
    public void test() {
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        File outputFile = new File("E:\\resources\\Citi\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        try {
            SortFactory.getSort(SortMethod.DEFAULT).sort(inputFile, outputFile, tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }

    @Test
    public void t2() {

        CommonSort sorter = new CommonSort();
        Integer[] testInt = new Integer[] { 1, 3, 8, 2, 7, 9, 6, 10, 11, 18, 15, 16 };
        sorter.merge(testInt, 0, 3, 3, 6);
        System.out.println(Arrays.toString(testInt));
        int[][] index = new int[][] { { 0, 3 }, { 3, 6 }, { 6, 10 }, { 10, 12 } };
        sorter.sortedMerge(testInt, index, 0, 3);
        System.out.println(Arrays.toString(testInt));

    }
}
