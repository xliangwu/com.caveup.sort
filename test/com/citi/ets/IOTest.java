package com.citi.ets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class IOTest {

    @Test
    public void commonIO() throws Exception {
        File outputFile = new File("E:\\resources\\Citi\\test.txt");
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        reader.close();
    }

    @Test
    public void test() throws Exception {
        File outputFile = new File("E:\\resources\\Citi\\test.txt");
        RandomAccessFile fi = new RandomAccessFile(outputFile, "rw");
        FileChannel fc = fi.getChannel();

        long start = System.currentTimeMillis();

        MappedByteBuffer buffer = fc.map(MapMode.READ_WRITE, 0, outputFile.length());
        boolean eol = false;
        boolean skipLF = false;
        boolean isHeader = false;
        byte b = 0;

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

                if (!isHeader) {
                    isHeader = true;
                } else {
                    byteBuffer.flip();
                    byte[] newLineBytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(newLineBytes);
                    System.out.println(new String(newLineBytes));
                }
                byteBuffer.clear();
            }

            if (!buffer.hasRemaining() && byteBuffer.flip().limit() > 0) {
                byte[] newLineBytes = new byte[byteBuffer.limit()];
                byteBuffer.get(newLineBytes);
                System.out.println(new String(newLineBytes));
            }
        }
        fc.close();
        fi.close();
        System.out.println("use #" + "***" + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test3() throws Exception {
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        RandomAccessFile fi = new RandomAccessFile(inputFile, "rw");
        FileChannel fc = fi.getChannel();

        long start = System.currentTimeMillis();

        List<Trade> trades = new ArrayList<Trade>(10000000);
        MappedByteBuffer buffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        boolean eol = false;
        boolean skipLF = false;
        boolean isHeader = false;
        byte b = 0;

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

                if (!isHeader) {
                    isHeader = true;
                } else {
                    byteBuffer.flip();
                    byte[] newLineBytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(newLineBytes);
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
        fi.close();
        System.out.println("use #" + trades.size() + "***" + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() {

        long[] data = new long[] { 123, 456 };
        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 8);
        LongBuffer longBuffer = byteBuffer.asLongBuffer();
        longBuffer.put(data);
        System.out.println(Arrays.toString(byteBuffer.array()));

        byte b[] = new byte[8];

        ByteBuffer buf = ByteBuffer.wrap(b);
        buf.putLong(13);
        System.out.println(Arrays.toString(buf.array()));

        System.out.println(Arrays.toString(String.valueOf(",").getBytes()));
        System.out.println(Arrays.toString(intToBytes2(13)));
    }

    public static byte[] intToBytes2(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

}
