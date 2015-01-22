package com.citi.ets.meta;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Unsafe;

public class StringColumn extends AbstractColumn<String> {

    private static final Unsafe unsafe;
    private static final StringRef STRING_FLY_WEIGHT = new StringRef();
    private static final List<Long> ADDRESS_LIST = new ArrayList<Long>();
    private static final int DEFAULT_SIZE = 1 * 1000;

    private static int index = 0;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void check(int index) {
        if (index % DEFAULT_SIZE == 0) {
            long requiredHeap = DEFAULT_SIZE * StringRef.getObjectSize();
            long address = unsafe.allocateMemory(requiredHeap);
            ADDRESS_LIST.add(address);
        }
    }

    private void updateFlyWeightRef(int index) {
        long address = ADDRESS_LIST.get(index / DEFAULT_SIZE);
        final long offset = address + ((index % DEFAULT_SIZE) * StringRef.getObjectSize());
        STRING_FLY_WEIGHT.setObjectOffset(offset);
    }

    @Override
    public void appendToBuffer(final ByteBuffer buffer, int index) {
        byte[] bs = longToString((long[]) getData(index));
        for (byte b : bs) {
            if (b == 0) {
                break;
            }
            buffer.put(b);
        }
    }

    @Override
    public Object getData(int index) {
        check(index);
        updateFlyWeightRef(index);
        long[] l = STRING_FLY_WEIGHT.getValue();
        return l;
    }

    @Override
    public void addRow(String t) {
        check(index);
        updateFlyWeightRef(index);
        long[] l = stringToLong(t.getBytes());
        STRING_FLY_WEIGHT.setValue(l);
        index++;
    }

    private static byte[] longToString(long[] l) {
        byte[] r = new byte[16];

        for (int i = 0; i < 8; i++) {
            r[i] = (byte) (l[0] >> ((7 - i) * 8) & 0xFF);
        }

        for (int i = 0; i < 8; i++) {
            r[8 + i] = (byte) (l[1] >> ((7 - i) * 8) & 0xFF);
        }

        return r;
    }

    private static long[] stringToLong(byte[] bb) {
        long[] r = new long[2];

        int f = 0;
        for (int i = 0, c = 0; i < 16; i++, c++) {
            if (c != 0 && c % 8 == 0) {
                f++;
            }

            r[f] <<= 8;
            r[f] |= (i >= bb.length ? 0 : bb[i]) & 0xFF;
        }
        return r;
    }

    @Override
    public void mergeRow(String t, int index) {
        if (null != fun) {
            // String a = getData(index);
            // String b = t;
            // String res = fun.execute(a, b);
            // setData(index, res);
        }
    }

    @Override
    public void setData(int index, String t) {
        check(index);
        updateFlyWeightRef(index);
        long[] l = stringToLong(t.getBytes());
        STRING_FLY_WEIGHT.setValue(l);
    }

    private static class StringRef {

        private static long offset = 0;

        private static final long valueOffset = offset += 0;
        private static final long objectSize = offset += 16;

        private long objectOffset;

        public static long getObjectSize() {
            return objectSize;
        }

        void setObjectOffset(final long objectOffset) {
            this.objectOffset = objectOffset;
        }

        public long[] getValue() {
            long[] l = new long[2];
            l[0] = unsafe.getLong(objectOffset + valueOffset);
            l[1] = unsafe.getLong(objectOffset + valueOffset + 8);
            return l;
        }

        public void setValue(long[] l) {
            unsafe.putLong(objectOffset + valueOffset, l[0]);
            unsafe.putLong(objectOffset + valueOffset + 8, l[1]);
        }
    }

}
