package com.citi.ets.sort.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.citi.ets.AbstractInMemorySort;
import com.citi.ets.Trade;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;

public class PCSort extends AbstractInMemorySort {

    @Override
    public Trade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile), 32768);

        List<Trade> trades = new ArrayList<Trade>(10000000);
        String line = null;
        boolean headed = false;
        for (; (line = reader.readLine()) != null;) {
            if (!headed) {
                headed = true;
                continue;
            }
            trades.add(new Trade(line));
        }
        IntegerCache.getInstance().clear();
        DateCache.getInstance().clear();
        reader.close();
        return trades.toArray(new Trade[] {});
    }

    @Override
    public void outputAfterSort(Trade[] trades, File inputFile, File outputFile, File tempDir) throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile), 32768);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        List<Future<List<String>>> futureList = new ArrayList<Future<List<String>>>();

        int core = Runtime.getRuntime().availableProcessors();
        int threads = core <= 0 ? 1 : core;
        int step = trades.length / threads;

        for (int i = 0; i <= threads; i++) {
            int start = i * step;
            int end = (i + 1) * step;
            start = start > trades.length ? trades.length : start;
            end = end > trades.length ? trades.length : end;
            if (start < end) {
                futureList.add(threadPool.submit(new Produce(trades, start, end)));
            }
        }
        writer.write("Facility ID(Integer/SORT ASC),Product Type(String/SORT ASC), HOST ID(Integer/SORT ASC), MaturityDate(Date/SORT ASC), Exposure(Double/SORT ASC)");
        writer.newLine();
        for (Future<List<String>> future : futureList) {
            List<String> res = future.get();
            for (String line : res) {
                writer.write(line);
                writer.newLine();
            }
        }

        writer.flush();
        writer.close();
    }
}
