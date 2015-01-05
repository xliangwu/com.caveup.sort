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
import com.citi.ets.TradeByte;

public class NioByteSort extends AbstractInMemorySort<TradeByte> {

    public static final byte COMMA_BYTE = 44;

    private String header = null;

    @Override
    public TradeByte[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        List<TradeByte> trades = new ArrayList<TradeByte>(10000000);
        RandomAccessFile fout = new RandomAccessFile(inputFile, "r");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, inputFile.length());
        boolean isHeaderLine = false;
        byte b = 0;

        byte[][] lineBytes = new byte[5][];
        int index = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(200);
        for (int i = 0; i < buffer.limit(); i++) {
            b = buffer.get();
            if (b != 13 && b != 10 && b != COMMA_BYTE) {
                byteBuffer.put(b);
            }

            if (b == COMMA_BYTE || b == 13) {
                byteBuffer.flip();
                lineBytes[index] = new byte[byteBuffer.limit()];
                byteBuffer.get(lineBytes[index]);
                byteBuffer.clear();
                index++;
            }

            if (!buffer.hasRemaining() && byteBuffer.flip().limit() > 0) {
                lineBytes[index] = new byte[byteBuffer.limit()];
                byteBuffer.get(lineBytes[index]);
                byteBuffer.clear();
                index++;
            }
            // one line
            if (index == 5) {
                if (!isHeaderLine) {
                    isHeaderLine = true;
                    this.header = new StringBuilder().append(lineBytes[0]).append(lineBytes[1]).append(lineBytes[2])
                            .append(lineBytes[3]).append(lineBytes[4]).toString();
                } else {
                    trades.add(new TradeByte(lineBytes));
                }
                index = 0;
            }
        }

        fc.close();
        fout.close();

        return trades.toArray(new TradeByte[] {});
    }

    @Override
    public void outputAfterSort(TradeByte[] trades, File inputFile, File outputFile, File tempDir) throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        RandomAccessFile fout = new RandomAccessFile(outputFile, "rw");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer byteBuffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        byteBuffer.put(this.header.getBytes());
        byteBuffer.put(Sort.NEW_LINE);
        for (TradeByte trade : trades) {
            trade.appendTo(byteBuffer);
        }
        fout.close();
    }
}
