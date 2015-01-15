package com.citi.ets.funs;

public class MaxFunction<T extends Comparable<T>> implements Function<T> {

    @Override
    public T execute(T a, T b) {
        if (a.compareTo(b) > 0) {
            return a;
        } else {
            return b;
        }
    }
}
