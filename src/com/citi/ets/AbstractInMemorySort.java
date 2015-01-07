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

public abstract class AbstractInMemorySort<T> implements Sort {

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {

        long start = System.currentTimeMillis();
        T[] trades = loadTrades(inputFile, outputFile, tempDir);
        int i = trades.length;
        System.out.println("Load completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        doSort(trades, start);
        System.out.println("Sort completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");

        outputAfterSort(trades, inputFile, outputFile, tempDir);
        System.out.println("Output completed #" + i + " use #" + (System.currentTimeMillis() - start) + "ms");
    }

    private void doSort(T[] trades, long start) throws InterruptedException, ExecutionException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        int core = Runtime.getRuntime().availableProcessors();
        int threads = core <= 0 ? 1 : core;
        int step = trades.length / threads;
        List<Future<Integer>> futureList = new ArrayList<Future<Integer>>();

        int[][] threadIndex = new int[threads + 1][2];
        for (int j = 0; j <= threads; j++) {
            int startIndex = j * step;
            int endIndex = (j + 1) * step;
            startIndex = startIndex > trades.length ? trades.length : startIndex;
            endIndex = endIndex > trades.length ? trades.length : endIndex;
            threadIndex[j] = new int[] { startIndex, endIndex };
            if (startIndex < endIndex) {
                futureList.add(threadPool.submit(new PartitionSort<T>(trades, startIndex, endIndex)));
            }
        }
        for (Future<Integer> future : futureList) {
            future.get();
        }
        System.out.println("PartitionSort compeleted, use #" + (System.currentTimeMillis() - start) + "ms");
        sortedMerge(trades, threadIndex, 0, threads);
    }

    public int[] sortedMerge(Object[] array, int[][] index, int low, int high) {
        if (low == high) {
            return index[low];
        }

        int middle = low + (high - low) / 2;
        int[] p1 = sortedMerge(array, index, low, middle);
        int[] p2 = sortedMerge(array, index, middle + 1, high);
        merge(array, p1[0], p1[1], p2[0], p2[1]);
        return new int[] { p1[0], p2[1] };
    }

    public void merge(Object[] array, int s1, int e1, int s2, int e2) {
        int len = (e1 - s1) + (e2 - s2);

        Object[] tmps = new Object[len];
        int i = s1;
        int j = s2;
        int r = 0;
        while (i < e1 && j < e2) {
            @SuppressWarnings("unchecked")
            Comparable<Object> t1 = (Comparable<Object>) array[i];
            Object t2 = array[j];
            if (t1.compareTo(t2) <= 0) {
                tmps[r] = t1;
                i++;
            } else {
                tmps[r] = t2;
                j++;
            }
            r++;
        }

        while (i < e1) {
            tmps[r++] = array[i++];
        }

        while (j < e2) {
            tmps[r++] = array[j++];
        }

        // reset
        r = 0;
        for (i = s1; i < e1; i++) {
            array[i] = tmps[r++];
        }

        for (j = s2; j < e2; j++) {
            array[j] = tmps[r++];
        }

        tmps = null;
    }

    public abstract T[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception;

    public abstract void outputAfterSort(T[] trades, File inputFile, File outputFile, File tempDir) throws Exception;

    public static class PartitionSort<T> implements Callable<Integer> {

        private T[] trades;
        private int start;
        private int end;

        public PartitionSort(T[] trades, int start, int end) {
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
