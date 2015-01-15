package com.citi.ets.sort.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import com.citi.ets.Sort;
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

public class GroupSort implements Sort {

    private String header = null;
    private DataTable dataTable = null;

    @Override
    public void sort(File inputFile, File outputFile, File tempDir) throws Exception {
        long start = System.currentTimeMillis();

        loadGroupData(inputFile);
        System.out.println("Load completed use #" + (System.currentTimeMillis() - start) + "ms");

        dataTable.sort();
        System.out.println("Sort completed use #" + (System.currentTimeMillis() - start) + "ms");

        doOutput(outputFile, tempDir);
        System.out.println("Output completed use #" + (System.currentTimeMillis() - start) + "ms");
    }

    private void loadGroupData(File inputFile) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile), 32768);

        String line = null;
        boolean headed = false;
        for (; (line = reader.readLine()) != null;) {
            if (!headed) {
                headed = true;
                this.header = line;
                this.dataTable = createTable(line);
                continue;
            }
            dataTable.addRow(line);
        }
        reader.close();
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
