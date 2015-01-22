package com.citi.ets.meta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Unsafe;
import com.citi.ets.cache.IntegerCache;

public class IntegerColumn extends AbstractColumn<Integer> {

    private static final Unsafe unsafe;
    private static final IntegerRef INTEGER_FLY_WEIGHT = new IntegerRef();
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
            long requiredHeap = DEFAULT_SIZE * IntegerRef.getObjectSize();
            long address = unsafe.allocateMemory(requiredHeap);
            ADDRESS_LIST.add(address);
        }
    }

    private void updateFlyWeightRef(int index) {
        long address = ADDRESS_LIST.get(index / DEFAULT_SIZE);
        final long offset = address + ((index % DEFAULT_SIZE) * IntegerRef.getObjectSize());
        INTEGER_FLY_WEIGHT.setObjectOffset(offset);
    }

    @Override
    public Integer getData(int index) {
        check(index);
        updateFlyWeightRef(index);
        return INTEGER_FLY_WEIGHT.getValue();
    }

    @Override
    public void addRow(String t) {
        check(index);
        updateFlyWeightRef(index);
        int i = IntegerCache.getInstance().getInteger(t);
        INTEGER_FLY_WEIGHT.setValue(i);
        index++;
    }

    @Override
    public void mergeRow(String t, int index) {
        if (null != fun) {
            Integer a = getData(index);
            Integer b = Integer.parseInt(t);
            Integer res = fun.execute(a, b);
            setData(index, res);
        }
    }

    @Override
    public void setData(int index, Integer t) {
        check(index);
        updateFlyWeightRef(index);
        INTEGER_FLY_WEIGHT.setValue(t);
    }

    private static class IntegerRef {

        private static long offset = 0;

        private static final long valueOffset = offset += 0;
        private static final long objectSize = offset += 4;
        private long objectOffset;

        public static long getObjectSize() {
            return objectSize;
        }

        void setObjectOffset(final long objectOffset) {
            this.objectOffset = objectOffset;
        }

        public int getValue() {
            return unsafe.getInt(objectOffset + valueOffset);
        }

        public void setValue(int l) {
            unsafe.putInt(objectOffset + valueOffset, l);
        }
    }

}
