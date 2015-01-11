package com.citi.ets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CompareTwoFile {

    public static void main(String[] args) throws Exception {
        File inputFile1 = new File("E:\\resources\\Citi\\output_correct.csv");
        File inputFile2 = new File("E:\\resources\\Citi\\output2.csv");
        Map<Integer, String> inputMap1 = new HashMap<Integer, String>();

        BufferedReader reader1 = new BufferedReader(new FileReader(inputFile1), 32768);
        BufferedReader reader2 = new BufferedReader(new FileReader(inputFile2), 32768);

        String line = null;
        int count = 0;
        while ((line = reader1.readLine()) != null) {
            inputMap1.put(count++, line);
        }
        System.out.println("Load one done #" + count);
        count = 0;
        while ((line = reader2.readLine()) != null) {
            if (!line.equals(inputMap1.get(count++))) {
                System.out.println("Not same by #" + count + "***" + line);
                break;
            }
        }
        System.out.println("Same");

        reader1.close();
        reader2.close();
    }
}
