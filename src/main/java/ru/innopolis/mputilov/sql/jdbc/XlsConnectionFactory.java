package ru.innopolis.mputilov.sql.jdbc;

import org.apache.poi.ss.usermodel.Workbook;

import java.net.URL;

public interface XlsConnectionFactory {
    XlsConnection create(URL filename, Workbook workbook);
}
