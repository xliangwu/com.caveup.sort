package com.citi.ets.sort.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import com.citi.ets.Trade;

public class Produce implements Callable<List<String>> {

    private Trade[] trades;
    private int start;
    private int end;

    public Produce(final Trade[] trades, int start, int end) {
        this.trades = trades;
        this.start = start;
        this.end = end;
    }

    @Override
    public List<String> call() {
        List<String> res = new ArrayList<String>((end - start + 1));
        for (int i = start; i < end; i++) {
            Trade trade = trades[i];
            res.add(trade.toString());
            trade = null;
        }
        return res;
    }

}
