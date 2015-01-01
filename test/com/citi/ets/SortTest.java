package com.citi.ets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class SortTest {

    private static List<Trade> trades = new ArrayList<Trade>();

    @Test
    public void t3() {
        long start = System.currentTimeMillis();
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        RandomAccessFile randomAccessFiile = null;
        FileChannel fcin = null;
        try {
            randomAccessFiile = new RandomAccessFile(inputFile, "r");
            fcin = randomAccessFiile.getChannel();
            ByteBuffer rBuffer = ByteBuffer.allocate(8192);

            readFileByLine(8192, fcin, rBuffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                randomAccessFiile.close();
                if (fcin != null)
                    fcin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("use #" + (System.currentTimeMillis() - start) + "ms");
    }

    public static void readFileByLine(int bufSize, FileChannel fcin, ByteBuffer rBuffer) {
        String enterStr = "\n";
        try {
            byte[] bs = new byte[bufSize];

            int size = 0;
            boolean headed = true;
            StringBuffer strBuf = new StringBuffer("");
            // while((size = fcin.read(buffer)) != -1){
            while (fcin.read(rBuffer) != -1) {
                int rSize = rBuffer.position();
                rBuffer.rewind();
                rBuffer.get(bs);
                rBuffer.clear();
                String tempString = new String(bs, 0, rSize);
                // System.out.print(tempString);
                // System.out.print("<200>");

                int fromIndex = 0;
                int endIndex = 0;
                while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {
                    String line = tempString.substring(fromIndex, endIndex);
                    line = new String(strBuf.toString() + line);
                    if (!headed) {
                        trades.add(new Trade(line));
                    }
                    headed = false;
                    strBuf.delete(0, strBuf.length());
                    fromIndex = endIndex + 1;
                }
                if (rSize > tempString.length()) {
                    strBuf.append(tempString.substring(fromIndex, tempString.length()));
                } else {
                    strBuf.append(tempString.substring(fromIndex, rSize));
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        Sorter sorter = new Sorter();
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        File outputFile = new File("E:\\resources\\Citi\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        try {
            sorter.call(inputFile, outputFile, tempDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
    }

    @Test
    public void t1() {
        String s = "16800, QC  EP9fn,991,01/03/2017,2378402.1292994376";

        System.out.println(Arrays.toString(parse(s, 5, ',')));

    }

    private String[] parse(String input, int size, char sep) {
        String[] res = new String[size];
        int inputLen = input.length();
        int lastIndex = 0;
        int index = 0;
        for (int i = 0; i < inputLen; i++) {
            if (input.charAt(i) == sep) {
                res[index++] = input.substring(lastIndex, i);
                lastIndex = i + 1;
            }
        }

        if (input.charAt(inputLen - 1) != sep) {
            res[index++] = input.substring(lastIndex);
        }
        return res;
    }
}
