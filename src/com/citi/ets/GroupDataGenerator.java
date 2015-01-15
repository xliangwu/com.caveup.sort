package com.citi.ets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GroupDataGenerator {

    static Random rand = new Random();

    public static void main(String[] args) throws Exception {

        long time = System.currentTimeMillis();
        int rows = 5000 * 1000 * 1;
        File file = new File("input.csv");
        try (PrintWriter out = new PrintWriter(file);) {
            out.println("Facility ID(Integer/GROUP BY/SORT ASC),Product Type(String/GROUP BY/SORT DESC),Trade Count(Integer/SUM/),Maturity(Date/MAX/),Maturity2(Date/MIN/),Exposure(Long/SUM/)");
            new GroupDataGenerator(
            //
                    new IntGenerator(1, 50000, 100)// FacilityID
                    , new StringGenerator(10, 100)// ProductyType
                    , new IntGenerator(100, 1000, 100)// Host ID
                    , new DateGenerator("01/01/2000", "12/31/2020", 100)// Maturity
                    , new DateGenerator("01/01/2000", "12/31/2020", 100)// Maturity
                    , new IntGenerator(0, 10000, rows)// Exposure
            //
            ).generate(rows, out);
            time = System.currentTimeMillis() - time;
            System.out.println("time " + time);
        }
    }

    ColumnGenerator[] generators;

    public GroupDataGenerator(ColumnGenerator... generators) {
        super();
        this.generators = generators;
    }

    void generate(int rows, PrintWriter out) throws FileNotFoundException {
        for (int i = 0; i < rows; i++) {
            if (i % 1000000 == 0) {
                System.out.println(i);
            }
            int length = generators.length;
            for (int j = 0; j < length; j++) {
                ColumnGenerator g = generators[j];
                out.print(g.gen());
                if (j < length - 1) {
                    out.print(",");
                } else {
                    out.println();
                }
            }
        }
    }
}

interface ColumnGenerator {

    String gen();
}

class IntGenerator implements ColumnGenerator {

    long min;

    long max;

    int total;

    Random rand = new Random();

    int scope;

    public IntGenerator(int min, int max, int total) {
        super();
        this.min = min;
        this.max = max;
        this.total = total;
        scope = max - min;
    }

    @Override
    public String gen() {
        return Long.toString((min + rand.nextInt(total) * scope / total));
    }

}

class DoubleGenerator implements ColumnGenerator {

    double min;

    double max;

    double scope;

    int total;

    Random rand = new Random();

    public DoubleGenerator(double min, double max, int total) {
        super();
        this.min = min;
        this.max = max;
        this.total = total;
        scope = max - min;
    }

    @Override
    public String gen() {
        return "" + (min + rand.nextDouble() * scope);
    }
}

class DateGenerator implements ColumnGenerator {

    long min;

    long max;

    long scope;

    int total;

    Random rand = new Random();

    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

    public DateGenerator(String min, String max, int total) throws ParseException {
        super();
        this.min = df.parse(min).getTime();
        this.max = df.parse(max).getTime();
        this.total = total;
        scope = this.max - this.min;
    }

    @Override
    public String gen() {
        return df.format(new Date((min + rand.nextInt(total) * scope / total)));
    }
}

class StringGenerator implements ColumnGenerator {

    private static final String CHARS = " abcdef ghijklmn opqrst uvwxyz ABCDEFG HIJKLMN OPQRST UVWXYZ 01234 56789 ";

    int len;

    int total;

    Random rand = new Random();

    List<String> list = new ArrayList<String>();

    public StringGenerator(int len, int total) {
        super();
        this.len = len;
        this.total = total;
        for (int i = 0; i < total; i++) {
            list.add(randString(len));
        }
    }

    char[] chars = CHARS.toCharArray();

    String randString(int len) {
        len = len / 2 + rand.nextInt(len / 2);
        char[] chars = this.chars;
        int clen = chars.length;
        char[] str = new char[len];
        for (int i = 0; i < len; i++) {
            str[i] = chars[rand.nextInt(clen)];
        }
        return new String(str);
    }

    @Override
    public String gen() {
        return list.get(rand.nextInt(list.size()));
    }
}
