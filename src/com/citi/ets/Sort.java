package com.citi.ets;

import java.io.File;

public interface Sort {

    public static final byte COM_SEP = (byte) 44;
    public static final byte[] NEW_LINE = { 10 };

    public void sort(File inputFile, File outputFile, File tempDir) throws Exception;
}
