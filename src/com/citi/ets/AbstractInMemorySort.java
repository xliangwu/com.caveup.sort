package com.citi.ets;

import java.io.File;
import java.util.Arrays;

public abstract class AbstractInMemorySort implements Sort {

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {

        long start = System.currentTimeMillis();
        Trade[] trades = loadTrades(inputFile, outputFile, tempDir);
        int i = trades.length;
        System.out.println("Load completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        Arrays.sort(trades);
        System.out.println("Sort completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        outputAfterSort(trades, inputFile, outputFile, tempDir);
        System.out.println("Output completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");
    }

    public abstract Trade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception;

    public abstract void outputAfterSort(Trade[] trades, File inputFile, File outputFile, File tempDir)
            throws Exception;

}
