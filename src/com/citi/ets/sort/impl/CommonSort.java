package com.citi.ets.sort.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import com.citi.ets.AbstractInMemorySort;
import com.citi.ets.Sort;
import com.citi.ets.Trade;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;

public class CommonSort extends AbstractInMemorySort<Trade> {

    private String header = null;

    @Override
    public Trade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile), 32768);

        List<Trade> trades = new ArrayList<Trade>(10000000);
        String line = null;
        boolean headed = false;
        for (; (line = reader.readLine()) != null;) {
            if (!headed) {
                headed = true;
                this.header = line;
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
        outputFile.createNewFile();

        RandomAccessFile fout = new RandomAccessFile(outputFile, "rw");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer byteBuffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        byteBuffer.put(this.header.getBytes());
        byteBuffer.put(Sort.NEW_LINE);
        for (Trade trade : trades) {
            trade.appendTo(byteBuffer);
        }
        fout.close();
    }
}
