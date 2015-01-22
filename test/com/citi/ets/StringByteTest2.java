package com.citi.ets;

import java.util.Arrays;
import org.junit.Test;

public class StringByteTest2 {

    @Test
    public void testString() {
        String s = "a";
        // 68
        long l[] = stringToLong(s.getBytes(), 0, s.length());
        System.out.println(s + "==>\t" + Arrays.toString(l));
        byte[] b = longToString(l);
        System.out.println(new String(b));
        s = "b";
        // 68
        long l2[] = stringToLong(s.getBytes(), 0, s.length());
        System.out.println(s + "==>\t" + Arrays.toString(l2));
        b = longToString(l2);
        System.out.println(new String(b));
        s = "ab";
        // 68
        l2 = stringToLong(s.getBytes(), 0, s.length());
        System.out.println(s + "==>\t" + Arrays.toString(l2));
        b = longToString(l2);
        System.out.println(new String(b));

        s = "ab";
        // 68
        l2 = stringToLong(s.getBytes(), 0, s.length());
        System.out.println(s + "==>\t" + Arrays.toString(l2));
        b = longToString(l2);
        System.out.println(new String(b));

        s = "ba";
        // 68
        l2 = stringToLong(s.getBytes(), 0, s.length());
        System.out.println(s + "==>\t" + Arrays.toString(l2));
        byte[] b2 = longToString(l2);
        System.out.println(new String(b2));
        System.out.println(new String(b).compareTo(new String(b2)));

    }

    // aGwHVH jH
    public static long[] stringToLong(byte[] bb, int s, int e) {
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

    // aGwHVH jH
    public static byte[] longToString(long[] l) {
        byte[] r = new byte[16];

        for (int i = 0; i < 8; i++) {
            r[i] = (byte) (l[0] >> ((7 - i) * 8) & 0xFF);
        }

        for (int i = 0; i < 8; i++) {
            r[8 + i] = (byte) (l[1] >> ((7 - i) * 8) & 0xFF);
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
