package com.citi.ets.sort.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import com.citi.ets.AbstractInMemorySort;
import com.citi.ets.Trade;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;

public class NioSort extends AbstractInMemorySort {

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
        outputFile.createNewFile();

        FileOutputStream fout = new FileOutputStream(outputFile);
        FileChannel fc = fout.getChannel();
        ByteBuffer buffer = ByteBuffer.allocateDirect((int) inputFile.length());
        buffer.put("Facility ID(Integer/SORT ASC),Product Type(String/SORT ASC), HOST ID(Integer/SORT ASC), MaturityDate(Date/SORT ASC), Exposure(Double/SORT ASC)\r\n"
                .getBytes());
        for (Trade trade : trades) {
            trade.appendTo(buffer);
        }
        buffer.flip();
        fc.write(buffer);
        buffer.clear();
        fc.close();
        fout.close();
    }
}
