package com.hybridframework.utils.excelUtils;

import com.hybridframework.utils.logging.ErrorHandler;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class WorkbookManager implements AutoCloseable {

    private final XSSFWorkbook workbook;

    public WorkbookManager(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            this.workbook = new XSSFWorkbook(fis);
        } catch (IOException error) {
            ErrorHandler.logError(error, "WorkbookManager", "Failed to load workbook");
            throw new ExcelOperationException("Failed to load workbook: " + filePath, error);
        }
    }

    public Sheet getSheet(String sheetName) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new ExcelOperationException("Sheet not found: " + sheetName);
            }
            return sheet;
        } catch (Exception error) {
            ErrorHandler.logError(error, "WorkbookManager", "Failed to get sheet: " + sheetName);
            throw new ExcelOperationException("Failed to get sheet: " + sheetName, error);
        }
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }
}
