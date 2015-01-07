package com.citi.ets.sort.impl;

import java.io.File;
import java.io.FileInputStream;
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
        List<TradeByte> trades = new ArrayList<TradeByte>(1000000);
        FileInputStream fin = new FileInputStream(inputFile);
        FileChannel fc = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        boolean isHeaderLine = false;
        byte b = 0;

        byte[][] lineBytes = new byte[5][];
        int index = 0;
        int i = 0;

        byte[] tmpByte = new byte[100];
        int t = 0;
        byte[] bufferBytes = null;
        while (fc.read(buffer) != -1) {
            buffer.flip();
            bufferBytes = buffer.array();
            for (i = 0; i < buffer.limit(); i++) {
                b = bufferBytes[i];
                if (b != 13 && b != 10 && b != COMMA_BYTE) {
                    tmpByte[t++] = b;
                }

                if (b == COMMA_BYTE || b == 13) {
                    lineBytes[index] = new byte[t];
                    System.arraycopy(tmpByte, 0, lineBytes[index], 0, t);
                    t = 0;
                    index++;
                }

                // one line
                if (index == 5) {
                    if (!isHeaderLine) {
                        isHeaderLine = true;
                        this.header = new StringBuilder().append(lineBytes[0]).append(lineBytes[1])
                                .append(lineBytes[2]).append(lineBytes[3]).append(lineBytes[4]).toString();
                    } else {
                        trades.add(new TradeByte(lineBytes));
                    }
                    index = 0;
                    lineBytes = new byte[5][];
                }
            }

            // don't contains \r\n
            if (fc.position() == inputFile.length() && index == 4) {
                lineBytes[index] = new byte[t];
                System.arraycopy(tmpByte, 0, lineBytes[index], 0, t);
                t = 0;
                trades.add(new TradeByte(lineBytes));
            }

            buffer.clear();
        }

        fc.close();
        fin.close();

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
