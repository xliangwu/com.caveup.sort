package com.citi.ets;

import java.nio.ByteBuffer;

public class TradeByte implements Comparable<TradeByte> {

    private static final byte COMMA_BYTE = 44;
    private static final int[] F_TYPES = { 0, 1, 0, 2, 3 };
    private byte[][] srcInputBytes;

    public void appendTo(final ByteBuffer buffer) {
        int len = srcInputBytes.length;
        for (int i = 0; i < len; i++) {
            buffer.put(srcInputBytes[i]);
            if (i < len - 1) {
                buffer.put(COMMA_BYTE);
            }
        }

        buffer.put(Sort.NEW_LINE);
    }

    public TradeByte(byte[][] inputBytes) {
        this.srcInputBytes = inputBytes;
    }

    @Override
    public String toString() {
        int len = srcInputBytes.length;
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < len; i++) {
            res.append(new String(srcInputBytes[i]));
            if (i < len - 1) {
                res.append(",");
            }
        }
        return res.toString();
    }

    @Override
    public int compareTo(TradeByte o) {
        int len = srcInputBytes.length;
        for (int i = 0; i < len; i++) {
            int r = compare(srcInputBytes[i], o.getSrcInputBytes()[i], F_TYPES[i]);
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }

    private int compare(byte[] o1, byte[] o2, int type) {
        int len1 = o1.length;
        int len2 = o2.length;

        if (type == 0) {// int & long

        } else if (type == 1) {// string
            int lim = Math.min(len1, len2);
            int k = 0;
            while (k < lim) {
                byte c1 = o1[k];
                byte c2 = o2[k];
                if (c1 != c2) {
                    return c1 - c2;
                }
                k++;
            }
            return len1 - len2;
        } else if (type == 2) {// date 03/21/2017
            for (int i = 6; i < len1; i++) {
                byte c1 = o1[i];
                byte c2 = o2[i];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            for (int i = 0; i < 5; i++) {
                byte c1 = o1[i];
                byte c2 = o2[i];
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            return 0;
        }

        return 0;
    }

    public byte[][] getSrcInputBytes() {
        return srcInputBytes;
    }

    public void setSrcInputBytes(byte[][] srcInputBytes) {
        this.srcInputBytes = srcInputBytes;
    }

}
