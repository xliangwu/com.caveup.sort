package com.citi.ets;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class AbstractInMemorySort implements Sort {

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {

        long start = System.currentTimeMillis();
        Trade[] trades = loadTrades(inputFile, outputFile, tempDir);
        int i = trades.length;
        System.out.println("Load completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        doSort(trades, start);
        System.out.println("Sort completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        outputAfterSort(trades, inputFile, outputFile, tempDir);
        System.out.println("Output completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");
    }

    private void doSort(Trade[] trades, long start) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int core = Runtime.getRuntime().availableProcessors();
        int threads = core <= 0 ? 1 : core * 2;
        int step = trades.length / threads;
        List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();
        for (int j = 0; j <= threads; j++) {
            int startIndex = j * step;
            int endIndex = (j + 1) * step;
            startIndex = startIndex > trades.length ? trades.length : startIndex;
            endIndex = endIndex > trades.length ? trades.length : endIndex;
            if (startIndex < endIndex) {
                futureList.add(threadPool.submit(new PartitionSort(trades, startIndex, endIndex)));
            }
        }
        for (Future<Integer> future : futureList) {
            future.get();
        }

        System.out.println("PartitionSort compeleted, use #" + (System.currentTimeMillis() - start) + "ms");
        Arrays.sort(trades);
    }

    public abstract Trade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception;

    public abstract void outputAfterSort(Trade[] trades, File inputFile, File outputFile, File tempDir)
            throws Exception;

    private static class PartitionSort implements Callable<Integer> {

        private Trade[] trades;
        private int start;
        private int end;

        public PartitionSort(Trade[] trades, int start, int end) {
            this.trades = trades;
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer call() throws Exception {
            Arrays.sort(trades, start, end);
            return 0;
        }

    }
}
