package com.hybridframework.dataProviders;

public class TestDataConfig {

    private final String fileName;
    private final String sheetName;

    public TestDataConfig(String fileName, String sheetName) {
        this.fileName = fileName;
        this.sheetName = sheetName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSheetName() {
        return sheetName;
    }
}
