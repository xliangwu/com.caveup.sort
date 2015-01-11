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
        System.out.println(getLong("1234".getBytes(), 0));
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
        String s = "33";
        long l = toLong(s.getBytes(), 0, s.length());
        System.out.println("****" + l);
        System.out.println(getLong("1234".getBytes(), 0));
        System.out.println("TO 2:" + Long.toBinaryString(l));
        System.out.println((byte) (l & 0x0F));
        byte[] b = longToByte(l);
        System.out.println(new String(b));
        for (int i = 0; i < b.length; i++) {
            if (b[i] != '\0') {
                System.out.print(b[i]);
            }
        }
        System.out.println((byte) '\0');
    }

    @Test
    public void testDouble() {
        String s = "329486.33";
        // 68
        long l[] = doubleToLong(s.getBytes(), 0, s.length());
        System.out.println("****" + Arrays.toString(l));
        byte[] b = doubleToBytes(l);
        System.out.println(new String(b));
        System.out.println(new String(longToByte(l[0])));
        System.out.println(new String(longToByte(l[1])));
        System.out.println((byte) '\0');
    }
    
    @Test
    public void testDouble2() {
        String s = "1736276.09";
        // 68
        long l[] = doubleToLong(s.getBytes(), 0, s.length());
        System.out.println("****" + Arrays.toString(l));
        byte[] b = doubleToBytes(l);
        String s2 = "1736276.09";
        long l2[] = doubleToLong(s2.getBytes(), 0, s2.length());
        System.out.println("****" + Arrays.toString(l2));
        System.out.println(new String(b));
        System.out.println(new String(longToByte(l[0])));
        System.out.println(new String(longToByte(l[1])));
        System.out.println((byte) '\0');
    }

    @Test
    public void testString() {
        String s = "aGwHVH  jH";
        // 68
        long l[] = stringToLong(s.getBytes(), 0, s.length());
        System.out.println("aGwHVH  jH****" + Arrays.toString(l));
        byte[] b = longToString(l);
        System.out.println(new String(b));
        System.out.println((byte) '\0');

        l = stringToLong("jH".getBytes(), 0, 2);
        System.out.println("jH****" + Arrays.toString(l));
        b = longToString(l);
        System.out.println(new String(b));
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
        byte[] b = new byte[33];

        b[16] = (byte) 47;
        for (int i = 0; i < 16; i++) {
            b[32 - i] = (byte) (((l[1] >> 4 * i) & 0x0F) + 47);
            if (b[32 - i] != 47) {
                b[16] = (byte) 46;
            }
        }
        for (int i = 0; i < 16; i++) {
            b[15 - i] = (byte) (((l[0] >> 4 * i) & 0x0F) + 47);
        }
        return b;
    }

    @Test
    public void testDate() {
        String s = "08/10/2008";
        long l = dateToLong(s.getBytes(), 0, s.length());
        System.out.println("****" + l);
        byte[] b = longToDate(l);
        System.out.println(new String(b));
    }

    public static byte[] longToDate(long l) {
        byte[] b = new byte[8];

        for (int i = 0; i < 8; i++) {
            b[7 - i] = (byte) ((l >> 8 * i) & 0xFF);
        }
        return b;
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

    // 14
    public static long toLong(byte[] bb, int s, int e) {
        long r = 0;
        for (int i = s; i < e; i++) {
            r <<= 4;
            r |= (bb[i] - 47) & 0x0F;
        }
        return r;
    }

    public static byte[] longToByte(long l) {
        byte[] b = new byte[16];

        for (int i = 0; i < 16; i++) {
            b[15 - i] = (byte) (((l >> 4 * i) & 0x0F) + 47);
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
