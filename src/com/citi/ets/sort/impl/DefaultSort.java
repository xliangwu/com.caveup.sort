package com.citi.ets.sort.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.citi.ets.Sort;
import com.citi.ets.Trade;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;

public class DefaultSort implements Sort {

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {

        long start = System.currentTimeMillis();
        if (outputFile.exists()) {
            outputFile.delete();
        }
        BufferedReader reader = new BufferedReader(new FileReader(inputFile), 32768);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile), 32768);

        List<Trade> trades = new ArrayList<Trade>(10000000);
        int i = 0;
        String line = null;
        boolean headed = false;
        for (; (line = reader.readLine()) != null;) {
            if (!headed) {
                headed = true;
                writer.write(line);
                writer.newLine();
                continue;
            }
            i++;
            trades.add(new Trade(line));
        }
        IntegerCache.getInstance().clear();
        DateCache.getInstance().clear();
        System.out.println("Load completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        Collections.sort(trades);
        System.out.println("Sort completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        for (Trade trade : trades) {
            writer.write(trade.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        reader.close();
        System.out.println("Output completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");
    }
}
