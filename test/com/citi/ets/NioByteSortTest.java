package com.citi.ets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class NioByteSortTest {

    @Test
    public void test() {
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        File outputFile = new File("E:\\resources\\Citi\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        try {
            SortFactory.getSort(SortMethod.NIO_BYTE_SORT).sort(inputFile, outputFile, tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }

    @Test
    public void test2() throws IOException {
        long start = System.currentTimeMillis();
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        List<byte[]> trades = new ArrayList<byte[]>();
        FileInputStream fin = new FileInputStream(inputFile);
        FileChannel fc = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        boolean isHeaderLine = false;
        byte b = 0;

        byte[] lineBytes = null;
        int i = 0;

        byte[] tmpByte = new byte[300];
        int t = 0;
        byte[] bufferBytes = null;
        while (fc.read(buffer) != -1) {
            buffer.flip();
            bufferBytes = buffer.array();
            for (i = 0; i < buffer.limit(); i++) {
                b = bufferBytes[i];
                if (b != 13 && b != 10) {
                    tmpByte[t++] = b;
                }

                if (b == 13) {
                    lineBytes = new byte[t];
                    System.arraycopy(tmpByte, 0, lineBytes, 0, t);
                    t = 0;

                    if (!isHeaderLine) {
                        isHeaderLine = true;
                        //
                    } else {
                        trades.add(lineBytes);
                    }
                }
            }

            // don't contains \r\n
            if (fc.position() == inputFile.length() && t > 0) {
                lineBytes = new byte[t];
                System.arraycopy(tmpByte, 0, lineBytes, 0, t);
                t = 0;
                trades.add(lineBytes);
            }

            buffer.clear();
        }

        fc.close();
        fin.close();
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }

}
