package com.citi.ets;

import java.io.File;

public interface Sort {

    public static final byte[] COM_SEP = ",".getBytes();
    public static final byte[] NEW_LINE = "\r\n".getBytes();

    public void sort(File inputFile, File outputFile, File tempDir) throws Exception;
}
