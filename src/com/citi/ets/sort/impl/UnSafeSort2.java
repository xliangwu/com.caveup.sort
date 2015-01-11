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

public class UnSafeSort2 extends AbstractInMemorySort<Object> {

    private static final Unsafe unsafe;

    private static long address;
    private byte[] header = null;

    private static final DirectMemoryTrade flyweight = new DirectMemoryTrade();
    private static final byte[] LONG_TO_BYTES = new byte[16];
    private static final byte[] LONG_TO_DATE_BYTES = new byte[8];
    private static final byte[] LONG_TO_DOUBLE_BYTES = new byte[33];
    private static final ThreadLocal<DirectMemoryTrade[]> thread_local_flyweight = new ThreadLocal<DirectMemoryTrade[]>() {

        @Override
        protected DirectMemoryTrade[] initialValue() {
            return new DirectMemoryTrade[] { new DirectMemoryTrade(), new DirectMemoryTrade() };
        }

    };
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
        File inputFile = new File("E:\\resources\\Citi\\input.csv");
        File outputFile = new File("E:\\resources\\Citi\\output2.csv");
        File tempDir = new File("E:\\resources\\Citi");

        long start = System.currentTimeMillis();
        UnSafeSort2 sort = new UnSafeSort2();
        sort.sort(inputFile, outputFile, tempDir);
        long end = System.currentTimeMillis();
        System.out.println("Use #" + (end - start) + "ms");
        unsafe.freeMemory(address);
    }

    @Override
    public Object[] loadTrades(File inputFile, File outputFile, File tempDir) throws Exception {
        long start = System.currentTimeMillis();
        int tradeSize = 5000000;
        Object[] referIndexs = new Object[tradeSize];

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
        get(tradeIndex);
        referIndexs[tradeIndex] = tradeIndex;
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
                    flyweight.setFacilityId(toLong(filesBytes, lastIndex, i));
                if (findex == 1)
                    flyweight.setProductType(stringToLong(filesBytes, lastIndex, i));
                if (findex == 2)
                    flyweight.setHostId(toLong(filesBytes, lastIndex, i));
                if (findex == 3) {
                    flyweight.setMaturityDate(dateToLong(filesBytes, lastIndex, i));
                }
                if (findex == 4) {
                    flyweight.setExposure(doubleToLong(filesBytes, lastIndex, i));
                }

                next = 1;
                findex++;
            }

            // one line
            if (findex == 5 && tradeIndex < tradeSize) {
                get(tradeIndex);
                referIndexs[tradeIndex] = tradeIndex;
                tradeIndex++;
                findex = 0;
            }
        }

        // don't contains \r\n
        if (findex == 4) {
            flyweight.setExposure(doubleToLong(filesBytes, lastIndex, i));
        }

        fc.close();
        fin.close();
        return referIndexs;
    }

    @Override
    public void outputAfterSort(Object[] trades, File inputFile, File outputFile, File tempDir) throws Exception {
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();
        RandomAccessFile fout = new RandomAccessFile(outputFile, "rw");
        FileChannel fc = fout.getChannel();

        MappedByteBuffer byteBuffer = fc.map(MapMode.READ_WRITE, 0, inputFile.length());
        byteBuffer.put(header);
        byteBuffer.put(Sort.NEW_LINE);
        for (Object index : trades) {
            get((int) index);
            putByte(byteBuffer, longToByte(flyweight.getFacilityId()));
            byteBuffer.put(COM_SEP);
            putStringByte(byteBuffer, longToString(flyweight.getProductType()));
            byteBuffer.put(COM_SEP);
            putByte(byteBuffer, longToByte(flyweight.getHostId()));
            byteBuffer.put(COM_SEP);
            putDateByte(byteBuffer, longToDate(flyweight.getMaturityDate()));
            byteBuffer.put(COM_SEP);
            putByte(byteBuffer, doubleToBytes(flyweight.getExposure()));
            byteBuffer.put(NEW_LINE);
        }
        fout.close();

    }

    private static void putByte(ByteBuffer buffer, byte[] bs) {
        for (byte b : bs) {
            if (b == 47) {
                continue;
            }
            buffer.put(b);
        }
    }

    private static void putStringByte(ByteBuffer buffer, byte[] bs) {
        for (int i = 7; i >= 0; i--) {
            if (bs[i] != '\0') {
                buffer.put(bs[i]);
            }
        }
        for (int i = 15; i >= 8; i--) {
            if (bs[i] != '\0') {
                buffer.put(bs[i]);
            }
        }
    }

    private static void putDateByte(ByteBuffer buffer, byte[] bs) {
        buffer.put(bs[4]);
        buffer.put(bs[5]);
        buffer.put((byte) 47);
        buffer.put(bs[6]);
        buffer.put(bs[7]);
        buffer.put((byte) 47);
        buffer.put(bs[0]);
        buffer.put(bs[1]);
        buffer.put(bs[2]);
        buffer.put(bs[3]);
    }

    // 16
    public static long toLong(byte[] bb, int s, int e) {
        long r = 0;
        for (int i = s; i < e; i++) {
            r <<= 4;
            r |= (bb[i] - 47) & 0x0F;
        }
        return r;
    }

    public static byte[] longToByte(long l) {
        for (int i = 0; i < 16; i++) {
            LONG_TO_BYTES[15 - i] = (byte) (((l >> 4 * i) & 0x0F) + 47);
        }
        return LONG_TO_BYTES;
    }

    public static byte[] longToDate(long l) {
        for (int i = 0; i < 8; i++) {
            LONG_TO_DATE_BYTES[7 - i] = (byte) ((l >> 8 * i) & 0xFF);
        }
        return LONG_TO_DATE_BYTES;
    }

    // 08/10/2008
    public static long dateToLong(byte[] bb, int s, int e) {
        long r = 0;
        r <<= 8;
        r |= bb[s + 6] & 0xFF;
        r <<= 8;
        r |= bb[s + 7] & 0xFF;
        r <<= 8;
        r |= bb[s + 8] & 0xFF;
        r <<= 8;
        r |= bb[s + 9] & 0xFF;
        r <<= 8;
        r |= bb[s + 0] & 0xFF;
        r <<= 8;
        r |= bb[s + 1] & 0xFF;
        r <<= 8;
        r |= bb[s + 3] & 0xFF;
        r <<= 8;
        r |= bb[s + 4] & 0xFF;

        return r;
    }

    // 329486.36973333836
    public static long[] doubleToLong(byte[] bb, int s, int e) {
        long[] r = new long[2];

        int f = 0;
        for (int i = s; i < e; i++) {
            if (bb[i] == 46) {
                f = 1;
                continue;
            }

            r[f] <<= 4;
            r[f] |= (bb[i] - 47) & 0x0f;
        }
        return r;
    }

    // 329486.36973333836
    public static byte[] doubleToBytes(long[] l) {

        LONG_TO_DOUBLE_BYTES[16] = (byte) 47;
        for (int i = 0; i < 16; i++) {
            LONG_TO_DOUBLE_BYTES[32 - i] = (byte) (((l[1] >> 4 * i) & 0x0F) + 47);
            if (LONG_TO_DOUBLE_BYTES[32 - i] != 47) {
                LONG_TO_DOUBLE_BYTES[16] = (byte) 46;
            }
        }
        for (int i = 0; i < 16; i++) {
            LONG_TO_DOUBLE_BYTES[15 - i] = (byte) (((l[0] >> 4 * i) & 0x0F) + 47);
        }
        return LONG_TO_DOUBLE_BYTES;
    }

    // aGwHVH jH
    public static long[] stringToLong(byte[] bb, int s, int e) {
        long[] r = new long[2];

        int f = 0;
        for (int i = s, c = 0; i < e; i++, c++) {
            if (c != 0 && c % 8 == 0) {
                f++;
            }
            r[f] <<= 8;
            r[f] |= bb[i] & 0xFF;
        }
        return r;
    }

    // aGwHVH jH
    public static byte[] longToString(long[] l) {
        byte[] r = new byte[16];

        for (int i = 0; i < 8; i++) {
            r[i] = (byte) (l[0] >> (i * 8) & 0xFF);
        }

        for (int i = 0; i < 8; i++) {
            r[8 + i] = (byte) (l[1] >> (i * 8) & 0xFF);
        }

        return r;
    }

    private void get(int index) {
        final long offset = address + (index * DirectMemoryTrade.getObjectSize());
        flyweight.setObjectOffset(offset);
    }

    private DirectMemoryTrade get(int index, final DirectMemoryTrade trade) {
        final long offset = address + (index * DirectMemoryTrade.getObjectSize());
        trade.setObjectOffset(offset);
        return trade;
    }

    public class ReferIndex implements Comparable<ReferIndex> {

        private int index = 0;

        public ReferIndex(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(ReferIndex o) {
            DirectMemoryTrade[] trades = thread_local_flyweight.get();
            get(index, trades[0]);
            get(o.getIndex(), trades[1]);
            return trades[0].compareTo(trades[1]);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

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
            return unsafe.getLong(objectOffset + facilityIdOffset);
        }

        public void setFacilityId(long facilityId) {
            unsafe.putLong(objectOffset + facilityIdOffset, facilityId);
        }

        public long[] getProductType() {
            long[] l = new long[2];
            l[0] = unsafe.getLong(objectOffset + productTypeOffset);
            l[1] = unsafe.getLong(objectOffset + productTypeOffset + 8);
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
            return unsafe.getLong(objectOffset + maturityDateOffset);
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
            long f1 = this.getFacilityId();
            long f2 = o.getFacilityId();
            int res = f1 > f2 ? 1 : (f1 < f2 ? -1 : 0);
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

            f1 = this.getHostId();
            f2 = o.getHostId();
            res = f1 > f2 ? 1 : (f1 < f2 ? -1 : 0);
            if (res != 0) {
                return res;
            }

            f1 = this.getMaturityDate();
            f2 = o.getMaturityDate();
            res = f1 > f2 ? 1 : (f1 < f2 ? -1 : 0);

            if (res != 0) {
                return res;
            }

            l = this.getExposure();
            s = o.getExposure();

            res = l[0] > s[0] ? 1 : (l[0] < s[0] ? -1 : 0);
            if (res != 0) {
                return res;
            }
            // r
            res = l[1] > s[1] ? -1 : (l[1] < s[1] ? 1 : 0);
            if (res != 0) {
                return res;
            }
            return res;
        }

    }

}
