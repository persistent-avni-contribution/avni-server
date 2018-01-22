package org.openchs.excel.data;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

public class ImportFile {
    private final XSSFWorkbook workbook;

    public ImportFile(InputStream inputStream) throws IOException {
        workbook = new XSSFWorkbook(inputStream);
    }

    public int getNumberOfSheets() {
        return workbook.getNumberOfSheets();
    }

    public ImportSheet getSheet(String sheetName) {
        return new ImportSheet(workbook.getSheet(sheetName));
    }

    public void close() {
        try {
            workbook.close();
        } catch (Exception e) {

        }
    }
}