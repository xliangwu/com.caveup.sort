package com.citi.ets.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Unsafe;
import com.citi.ets.cache.LongCache;

public class LongColumn extends AbstractColumn<Long> {

    private static final Unsafe unsafe;
    private static final LongRef LONG_FLY_WEIGHT = new LongRef();
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
            long requiredHeap = DEFAULT_SIZE * LongRef.getObjectSize();
            long address = unsafe.allocateMemory(requiredHeap);
            ADDRESS_LIST.add(address);
        }
    }

    private void updateFlyWeightRef(int index) {
        long address = ADDRESS_LIST.get(index / DEFAULT_SIZE);
        final long offset = address + ((index % DEFAULT_SIZE) * LongRef.getObjectSize());
        LONG_FLY_WEIGHT.setObjectOffset(offset);
    }

    public Object getData(int index) {
        check(index);
        updateFlyWeightRef(index);
        return LONG_FLY_WEIGHT.getValue();
    }

    @Override
    public void addRow(String t) {
        check(index);
        updateFlyWeightRef(index);
        long l = LongCache.getInstance().getLong(t);
        LONG_FLY_WEIGHT.setValue(l);
        index++;
    }

    @Override
    public void mergeRow(String t, int index) {
        if (null != fun) {
            long a = (long) getData(index);
            long b = (long) Long.parseLong(t);
            Long res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, Long t) {
        check(index);
        updateFlyWeightRef(index);
        LONG_FLY_WEIGHT.setValue(t);
    }

    private static class LongRef {

        private static long offset = 0;

        private static final long valueOffset = offset += 0;
        private static final long objectSize = offset += 8;

        private long objectOffset;

        public static long getObjectSize() {
            return objectSize;
        }

        void setObjectOffset(final long objectOffset) {
            this.objectOffset = objectOffset;
        }

        public long getValue() {
            return unsafe.getLong(objectOffset + valueOffset);
        }

        public void setValue(long l) {
            unsafe.putLong(objectOffset + valueOffset, l);
        }
    }

}
