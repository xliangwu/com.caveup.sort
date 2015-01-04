package com.citi.ets.sort.impl;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
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

public class NioSort extends AbstractInMemorySort {

    private String header = null;

    @Override
    public Trade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        List<Trade> trades = new ArrayList<Trade>(10000000);
        RandomAccessFile fout = new RandomAccessFile(inputFile, "rw");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer buffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        boolean isHeaderLine = false;
        boolean eol = false;
        byte b = 0;
        boolean skipLF = false;

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(200);
        for (int i = 0; i < buffer.limit(); i++) {
            b = buffer.get();
            if (b != 13 && b != 10) {
                byteBuffer.put(b);
            }

            if (b == 13 || (!skipLF && b == 10)) {
                eol = true;
                if (b == 13) {
                    skipLF = false;
                }
            }

            if (b == 13) {
                skipLF = true;
            }

            if (eol) {
                eol = false;
                byteBuffer.flip();
                byte[] newLineBytes = new byte[byteBuffer.limit()];
                byteBuffer.get(newLineBytes);
                if (!isHeaderLine) {
                    isHeaderLine = true;
                    this.header = new String(newLineBytes);
                } else {
                    trades.add(new Trade(newLineBytes));
                }
                byteBuffer.clear();
            }

            if (!buffer.hasRemaining() && byteBuffer.flip().limit() > 0) {
                byte[] newLineBytes = new byte[byteBuffer.limit()];
                byteBuffer.get(newLineBytes);
                trades.add(new Trade(newLineBytes));
            }
        }

        fc.close();
        fout.close();
        IntegerCache.getInstance().clear();
        DateCache.getInstance().clear();

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
