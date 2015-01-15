package com.citi.ets;

import java.io.File;
import org.junit.Test;
import com.citi.ets.ds.DataTable;
import com.citi.ets.funs.SumFunction;
import com.citi.ets.sort.impl.GroupSort;

public class GroupSortTest {

    @Test
    public void test() {
        GroupSort sorter = new GroupSort();
        String header = "Facility ID(Integer/GROUP BY/SORT ASC),Product Type(String/GROUP BY/SORT DESC),Trade Count(Integer/SUM/),Maturity(Date/MAX/),Maturity2(Date/MIN/),Exposure(Long/SUM/)";
        DataTable table = sorter.createTable(header);
        System.out.println(table.toString());
    }

    @Test
    public void ta() {
        SumFunction<Long> s = new SumFunction<Long>();
        System.out.println(s.execute(123, 133));
        System.out.println(s.execute(123.44, 133));
        System.out.println(s.execute(123L, 133L));
    }

    @Test
    public void t1() {
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        File outputFile = new File("E:\\resources\\Citi\\output_correct.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        try {
            GroupSort sorter = new GroupSort();
            sorter.sort(inputFile, outputFile, tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }
}
