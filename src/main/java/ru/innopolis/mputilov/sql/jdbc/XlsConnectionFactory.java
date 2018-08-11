package ru.innopolis.mputilov.sql.jdbc;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URL;

public interface XlsConnectionFactory {
    XlsConnection create(URL filename, XSSFWorkbook workbook);
}
