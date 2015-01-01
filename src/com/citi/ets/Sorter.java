package com.citi.ets;

import java.io.File;

/**
 * Class package and name must be same as this template.
 * 
 * 
 */
public class Sorter {

    /**
     * The method name and parameter types must keep as this.
     * 
     * @param inputFile
     *            Input CSV file.
     * @param outputFile
     *            output CSV file; the platform will compare this file, so make
     *            sure you write your result to it.
     * @param tempDir
     *            Temp dir, you can put your temporary files in it.
     * @throws Exception
     */
    public void call(File inputFile, File outputFile, File tempDir) throws Exception {
        SortFactory.getSort(SortMethod.DEFAULT).sort(inputFile, outputFile, tempDir);
    }

}
