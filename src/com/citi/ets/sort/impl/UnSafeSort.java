package com.citi.ets.sort.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import sun.misc.Unsafe;
import com.citi.ets.AbstractInMemorySort;
import com.citi.ets.Sort;
import com.citi.ets.sort.impl.UnSafeSort.DirectMemoryTrade;

public class UnSafeSort extends AbstractInMemorySort<DirectMemoryTrade> {

    private static final Unsafe unsafe;

    private static long address;
    private byte[] header = null;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        File inputFile = new File("E:\\resources\\Citi\\input2.csv");
        File outputFile = new File("E:\\resources\\Citi\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        UnSafeSort sort = new UnSafeSort();
        sort.sort(inputFile, outputFile, tempDir);
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
        unsafe.freeMemory(address);
    }

    @Override
    public DirectMemoryTrade[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        long start = System.currentTimeMillis();
        int tradeSize = 5;
        DirectMemoryTrade[] trades = new DirectMemoryTrade[tradeSize];

        long requiredHeap = tradeSize * DirectMemoryTrade.getObjectSize();
        address = unsafe.allocateMemory(requiredHeap);
        FileInputStream fin = new FileInputStream(inputFile);
        FileChannel fc = fin.getChannel();

        int tradeIndex = 0;
        ByteBuffer buffer = ByteBuffer.allocate((int) fc.size());
        fc.read(buffer);
        byte[] filesBytes = buffer.array();
        System.out.println("Load done :" + (System.currentTimeMillis() - start) + "ms");

        byte b = 0;
        int len = filesBytes.length;
        int i = 0, lastIndex = 0, findex = 0;
        int next = 1;

        for (i = 0; i < len; i++) {
            b = filesBytes[i];
            if (b == 13) {
                header = new byte[i];
                System.arraycopy(filesBytes, 0, header, 0, i);
                break;
            }
        }
        i++;
        DirectMemoryTrade trade = get(tradeIndex);
        trades[tradeIndex] = trade;
        tradeIndex++;
        for (; i < len; i++) {
            b = filesBytes[i];
            if (b != 13 && b != 10 && b != 44) {
                if (next == 1) {
                    lastIndex = i;
                    next = 0;
                }
                continue;
            }

            if (b == 44 || b == 13) {
                if (findex == 0)
                    trade.setFacilityId(toLong(filesBytes, lastIndex, i));
                if (findex == 1)
                    trade.setProductType(stringToLong(filesBytes, lastIndex, i));
                if (findex == 2)
                    trade.setHostId(toLong(filesBytes, lastIndex, i));
                if (findex == 3)
                    trade.setMaturityDate(dateToLong(filesBytes, lastIndex, i));
                if (findex == 5) {
                    trade.setExposure(doubleToLong(filesBytes, lastIndex, i));
                }

                next = 1;
                findex++;
            }

            // one line
            if (findex == 5 && tradeIndex < tradeSize) {
                trade = get(tradeIndex);
                trades[tradeIndex] = trade;
                tradeIndex++;
                findex = 0;
            }
        }

        // don't contains \r\n
        if (findex == 4) {
            trade.setExposure(doubleToLong(filesBytes, lastIndex, i));
        }

        fc.close();
        fin.close();
        return trades;
    }

    @Override
    public void outputAfterSort(DirectMemoryTrade[] trades, File inputFile, File outputFile, File tempDir)
            throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        RandomAccessFile fout = new RandomAccessFile(outputFile, "rw");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer byteBuffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        byteBuffer.put(header);
        byteBuffer.put(Sort.NEW_LINE);
        // for (DirectMemoryTrade trade : trades) {
        // trade.appendTo(byteBuffer);
        // }
        fout.close();

    }

    // 16
    public static long toLong(byte[] bb, int s, int e) {
        long r = 0;
        for (int i = s; i < e; i++) {
            r <<= 4;
            r |= bb[s] & 0x0f;
        }
        return r;
    }

    public static long longToByte(byte[] bb, int s, int e) {
        long r = 0;
        for (int i = s; i < e; i++) {
            r <<= 4;
            r |= bb[s] & 0x0f;
        }
        return r;
    }

    // 08/10/2008
    public static long dateToLong(byte[] bb, int s, int e) {
        long r = 0;
        r <<= 8;
        r |= bb[s + 6] & 0xFF;
        r |= bb[s + 7] & 0xFF;
        r |= bb[s + 8] & 0xFF;
        r |= bb[s + 9] & 0xFF;
        r |= bb[s + 0] & 0xFF;
        r |= bb[s + 1] & 0xFF;
        r |= bb[s + 3] & 0xFF;
        r |= bb[s + 4] & 0xFF;
        return r;
    }

    // 329486.36973333836
    public static long[] doubleToLong(byte[] bb, int s, int e) {
        long[] r = new long[2];

        int f = 0;
        for (int i = s; i < e; i++) {
            if (bb[s] == 46)
                f = 1;
            r[f] <<= 4;
            r[f] |= bb[s] & 0x0f;
        }
        return r;
    }

    // 329486.36973333836
    public static long[] stringToLong(byte[] bb, int s, int e) {
        long[] r = new long[2];

        int f = 0;
        for (int i = s, c = 0; i < e; i++, c++) {
            if (c != 0 && c % 8 == 0)
                f++;
            r[f] <<= 8;
            r[f] |= bb[s] & 0xFF;
        }
        return r;
    }

    private DirectMemoryTrade get(int index) {
        final long offset = address + (index * DirectMemoryTrade.getObjectSize());
        DirectMemoryTrade trade = new DirectMemoryTrade();
        trade.setObjectOffset(offset);
        return trade;
    }

    public static class DirectMemoryTrade implements Comparable<DirectMemoryTrade> {

        private static long offset = 0;

        private static final long facilityIdOffset = offset += 0;
        private static final long productTypeOffset = offset += 8;
        private static final long hostIdOffSet = offset += 16;
        private static final long maturityDateOffset = offset += 8;
        private static final long exposureOffet = offset += 8;
        private static final long objectSize = offset += 16;

        private long objectOffset;

        public static long getObjectSize() {
            return objectSize;
        }

        void setObjectOffset(final long objectOffset) {
            this.objectOffset = objectOffset;
        }

        public long getFacilityId() {
            return unsafe.getInt(objectOffset + facilityIdOffset);
        }

        public void setFacilityId(long facilityId) {
            unsafe.putLong(objectOffset + facilityIdOffset, facilityId);
        }

        public long[] getProductType() {
            long[] l = new long[2];
            l[0] = unsafe.getLong(objectOffset + exposureOffet);
            l[1] = unsafe.getLong(objectOffset + exposureOffet + 8);
            return l;
        }

        public void setProductType(long[] l) {
            unsafe.putLong(objectOffset + productTypeOffset, l[0]);
            unsafe.putLong(objectOffset + productTypeOffset + 8, l[1]);
        }

        public long getHostId() {
            return unsafe.getLong(objectOffset + hostIdOffSet);
        }

        public void setHostId(long hostId) {
            unsafe.putLong(objectOffset + hostIdOffSet, hostId);
        }

        public long getMaturityDate() {
            return unsafe.getInt(objectOffset + maturityDateOffset);
        }

        public void setMaturityDate(long maturityDate) {
            unsafe.putLong(objectOffset + maturityDateOffset, maturityDate);
        }

        public long[] getExposure() {
            long[] l = new long[2];
            l[0] = unsafe.getLong(objectOffset + exposureOffet);
            l[1] = unsafe.getLong(objectOffset + exposureOffet + 8);
            return l;
        }

        public void setExposure(long l[]) {
            unsafe.putLong(objectOffset + exposureOffet, l[0]);
            unsafe.putLong(objectOffset + exposureOffet + 8, l[1]);
        }

        @Override
        public int compareTo(DirectMemoryTrade o) {
            int res = this.getFacilityId() > o.getFacilityId() ? 1
                    : (this.getFacilityId() < o.getFacilityId() ? -1 : 0);
            if (res != 0) {
                return res;
            }

            long[] l = this.getProductType();
            long[] s = o.getProductType();

            res = l[0] > s[0] ? 1 : (l[0] < s[0] ? -1 : 0);
            if (res != 0) {
                return res;
            }

            res = l[1] > s[1] ? 1 : (l[1] < s[1] ? -1 : 0);
            if (res != 0) {
                return res;
            }
            res = this.getHostId() > o.getHostId() ? 1 : (this.getHostId() < o.getHostId() ? -1 : 0);
            if (res != 0) {
                return res;
            }

            res = this.getMaturityDate() > o.getMaturityDate() ? 1 : (this.getMaturityDate() < o.getMaturityDate() ? -1
                    : 0);
            if (res != 0) {
                return res;
            }

            l = this.getExposure();
            s = o.getExposure();

            res = l[0] > s[0] ? 1 : (l[0] < s[0] ? -1 : 0);
            if (res != 0) {
                return res;
            }

            res = l[1] > s[1] ? 1 : (l[1] < s[1] ? -1 : 0);
            if (res != 0) {
                return res;
            }
            return res;
        }

    }

}
