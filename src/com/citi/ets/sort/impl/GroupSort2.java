package com.citi.ets.sort.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.citi.ets.Sort;
import com.citi.ets.cache.DateCache;
import com.citi.ets.cache.IntegerCache;
import com.citi.ets.cache.LongCache;
import com.citi.ets.ds.DataTable;
import com.citi.ets.funs.MaxFunction;
import com.citi.ets.funs.MinFunction;
import com.citi.ets.funs.SumFunction;
import com.citi.ets.meta.Column;
import com.citi.ets.meta.DateColumn;
import com.citi.ets.meta.DoubleColumn;
import com.citi.ets.meta.IntegerColumn;
import com.citi.ets.meta.LongColumn;
import com.citi.ets.meta.StringColumn;

public class GroupSort2 implements Sort {

    private String header = null;
    private DataTable dataTable = null;

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {
        long start = System.currentTimeMillis();

        loadGroupData(inputFile);
        System.out.println("Load completed use #" + (System.currentTimeMillis() - start) + "ms");

        DateCache.getInstance().clear();
        IntegerCache.getInstance().clear();
        LongCache.getInstance().clear();
        dataTable.sort();
        System.out.println("Sort completed use #" + (System.currentTimeMillis() - start) + "ms");

        doOutput(outputFile, tempDir);
        System.out.println("Output completed use #" + (System.currentTimeMillis() - start) + "ms");
    }

    private void loadGroupData(File inputFile) throws Exception {
        FileInputStream fin = new FileInputStream(inputFile);
        FileChannel fc = fin.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int) fc.size());
        fc.read(buffer);
        byte[] filesBytes = buffer.array();

        byte b = 0;
        int len = filesBytes.length;
        int i = 0, lastIndex = 0, findex = 0;
        int next = 1;

        for (i = 0; i < len; i++) {
            b = filesBytes[i];
            if (b == 13) {
                byte[] header = new byte[i];
                System.arraycopy(filesBytes, 0, header, 0, i);
                this.header = new String(header);
                this.dataTable = createTable(this.header);
                break;
            }
        }
        i++;
        int columnLen = dataTable.getColumns().length;
        String[] fields = new String[columnLen];
        for (; i < len; i++) {
            b = filesBytes[i];
            if (b != 13 && b != 10 && b != 44) {
                if (next == 1) {
                    lastIndex = i;
                    next = 0;
                }
                continue;
            }

            if (b == 44 || b == 13) {
                fields[findex] = new String(filesBytes, lastIndex, (i - lastIndex));
                next = 1;
                findex++;
            }

            // one line
            if (findex == columnLen) {
                dataTable.addRow(fields);
                fields = new String[columnLen];
                findex = 0;
            }
        }

        // don't contains \r\n
        if (findex == columnLen - 1) {
            fields[findex] = new String(filesBytes, lastIndex, (i - lastIndex));
            dataTable.addRow(fields);
        }

        fc.close();
        fin.close();
    }

    private void doOutput(File outputFile, File tempDir) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile), 32768);
        writer.write(header);
        writer.newLine();
        for (int i = 0; i < dataTable.getRowIndex(); i++) {
            writer.write(dataTable.getRow(i));
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DataTable createTable(String headerLine) {
        DataTable dataTable = new DataTable();
        // Facility ID(Integer/GROUP BY/SORT ASC),Product Type(String/GROUP BY/SORT DESC),Trade
        // Count(Integer/SUM/),Maturity(Date/MAX/),Maturity2(Date/MIN/),Exposure(Long/SUM/)
        String header = headerLine.toUpperCase();
        String[] fields = header.split(",");
        Column<?>[] columns = new Column[fields.length];

        String field = null;
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            if (field.contains("INTEGER")) {
                columns[i] = new IntegerColumn();
            }

            if (field.contains("STRING")) {
                columns[i] = new StringColumn();
            }

            if (field.contains("LONG")) {
                columns[i] = new LongColumn();
            }

            if (field.contains("DATE")) {
                columns[i] = new DateColumn();
            }
            if (field.contains("DOUBLE")) {
                columns[i] = new DoubleColumn();
            }

            if (field.contains("GROUP BY")) {
                columns[i].setGrouped(true);
            }

            if (field.contains("SORT ASC")) {
                columns[i].setOrder(true);
            } else if (field.contains("SORT DESC")) {
                columns[i].setOrder(false);
            }

            if (field.contains("MIN")) {
                columns[i].setFunction(new MinFunction());
            }

            if (field.contains("MAX")) {
                columns[i].setFunction(new MaxFunction());
            }

            if (field.contains("SUM")) {
                columns[i].setFunction(new SumFunction());
            }
        }

        dataTable.setColumns(columns);

        return dataTable;
    }
}
