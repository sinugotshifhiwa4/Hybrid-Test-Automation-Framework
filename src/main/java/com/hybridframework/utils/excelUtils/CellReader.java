package com.hybridframework.utils.excelUtils;

import com.hybridframework.utils.logging.ErrorHandler;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class CellReader {

    public static String getCellValueAsString(Cell cell) {
        try {
            if (cell == null) {
                return "";
            }

            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> {
                    try {
                        yield cell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        yield String.valueOf(cell.getNumericCellValue());
                    }
                }
                default -> "";
            };
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCellValueAsString", "Failed to get cell value");
            throw new RuntimeException("Failed to get cell value", error);
        }
    }

    public static boolean isValidCell(Cell cell) {
        return cell != null && cell.getCellType() != CellType._NONE;
    }
}
