package com.hybridframework.utils.excelUtils;

import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExcelConfigManager {

    private static final Logger logger = LoggerUtils.getLogger(ExcelConfigManager.class);

    /**
     * Reads data from an Excel file and converts it to a list of maps.
     *
     * @param filePath  The path to the Excel file
     * @param sheetName The name of the sheet to read
     * @return List of maps where each map represents a row of data
     * @throws ExcelOperationException if there are issues reading the file
     */
    public static List<Map<String, String>> loadExcelDataAsList(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();

        try (WorkbookManager workbookManager = new WorkbookManager(filePath)) {
            Sheet sheet = workbookManager.getSheet(sheetName);
            processSheet(sheet, dataList);
        } catch (IOException error) {
            ErrorHandler.logError(error, "readExcelData", "Failed to read Excel Data");
            throw new ExcelOperationException("Error closing workbook", error);
        }

        return dataList;
    }

    private static void processSheet(Sheet sheet, List<Map<String, String>> dataList) {
        try {
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount <= 1) {
                logger.warn("Sheet is empty or contains only headers");
                return;
            }

            Row headerRow = sheet.getRow(0);
            List<String> headers = getHeaders(headerRow);

            for (int i = 1; i < rowCount; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow != null) {
                    Map<String, String> rowData = processRow(currentRow, headers);
                    if (!rowData.isEmpty()) {
                        dataList.add(rowData);
                    }
                }
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "processSheet", "Failed to process sheet");
            throw new ExcelOperationException("Error closing workbook", error);
        }
    }

    private static List<String> getHeaders(Row headerRow) {
        try {
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                if (CellReader.isValidCell(cell)) {
                    headers.add(CellReader.getCellValueAsString(cell));
                }
            }
            return headers;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getHeaders", "Failed to get headers");
            throw new ExcelOperationException("Error closing workbook", error);
        }
    }

    private static Map<String, String> processRow(Row row, List<String> headers) {
        try {
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (CellReader.isValidCell(cell)) {
                    rowData.put(headers.get(j), CellReader.getCellValueAsString(cell));
                }
            }
            return rowData;
        } catch (Exception error) {
            ErrorHandler.logError(error, "processRow", "Failed to process row");
            throw new ExcelOperationException("Error closing workbook", error);
        }
    }
}