package com.citi.ets;

import java.util.Arrays;
import org.junit.Test;

public class StringByteTest {

    @Test
    public void test() {
        String s = "aGwHVH  jH";
        System.out.println(Arrays.toString(s.getBytes()));
        String s1 = "1680";
        System.out.println(getInt(s1.getBytes(), 0));
        System.out.println(getInt(s1.getBytes(), true));
        System.out.println(1234 & 0xFF >> 8);
        byte[] a = intToByteArray1(1234);
        System.out.println(Arrays.toString(a));

    }

    @Test
    public void test2() {
        System.out.println(Arrays.toString(".".getBytes()));
        System.out.println(getLong("1".getBytes(), 0));
        System.out.println(getLong("2".getBytes(), 0));
        System.out.println(getLong("12".getBytes(), 0));
        System.out.println(getLong("1234".getBytes(), 0));
        System.out.println(getLong("12344".getBytes(), 0));
        System.out.println(getLong("923456789012346".getBytes(), 0));
        System.out.println(getLong("999999999999999".getBytes(), 0));
        System.out.println(getLong("-999999999999999".getBytes(), 0));
        System.out.println(getLong("1".getBytes(), 0));
        System.out.println(getLong("92345678901234567".getBytes(), 0));
        System.out.println(getLong("12345678901234568".getBytes(), 0));
        System.out.println(getLong("123456789012345622228".getBytes(), 0));
    }

    @Test
    public void test3() {
        String s = "1";
        long l = toLong(s.getBytes(), 0, s.length());
        System.out.println(Long.toBinaryString(l));
        System.out.println(l);
        System.out.println(Arrays.toString(longToByte(l)));
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

    public static byte[] longToByte(long l) {
        byte[] b = new byte[16];
        for (int i = 15; i >= 0; i--) {
            b[i] = (byte) ((i >> 4 * i) & 0x0F);
        }
        return b;
    }

    public static int getInt(byte[] bb, int index) {
        return (int) ((((bb[index + 3] & 0xff) << 24) | ((bb[index + 2] & 0xff) << 16) | ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));
    }

    public static byte[] intToByteArray1(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public final static int getInt(byte[] buf, boolean asc) {
        int r = 0;
        if (asc)
            for (int i = buf.length - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        else
            for (int i = 0; i < buf.length; i++) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        return r;
    }

    // 16
    public static long getLong(byte[] bb, int index) {
        long r = 0;
        for (int i = 0; i < bb.length; i++) {
            r <<= 4;
            r |= bb[index + i] & 0x0f;
        }
        return r;
    }

}
