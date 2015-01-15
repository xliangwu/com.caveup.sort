package com.citi.ets.funs;

public class SumFunction<T extends Number> implements Function<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T execute(Number a, Number b) {
        Class<?> clz = a.getClass();

        if (clz.equals(Long.class)) {
            return (T) Long.valueOf(a.longValue() + b.longValue());
        }

        if (clz.equals(Integer.class)) {
            return (T) Integer.valueOf(a.intValue() + b.intValue());
        }

        if (clz.equals(Double.class)) {
            return (T) Double.valueOf(a.doubleValue() + b.doubleValue());
        }

        return null;
    }
}
