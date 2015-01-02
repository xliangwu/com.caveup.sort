package com.citi.ets;

import java.io.File;
import org.junit.Test;

public class PCSort {

    @Test
    public void test() {
        Sorter sorter = new Sorter();
        File inputFile = new File("C:\\Resource\\ETS Hackthon\\input.csv");
        File outputFile = new File("C:\\Resource\\ETS Hackthon\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        try {
            sorter.call(inputFile, outputFile, tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }

    @Test
    public void t2(){
        
        System.out.println(((byte)','));
        System.out.println(((byte)'\r'));
    }
}
