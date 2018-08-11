package ru.innopolis.mputilov.sql.jdbc;

import org.apache.poi.ss.usermodel.Workbook;

public interface XlsStatementFactory {
    XlsStatement create(Workbook wb);
}
