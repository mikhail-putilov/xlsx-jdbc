package ru.innopolis.mputilov.sql.jdbc.api;

import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;

import java.net.URL;

public interface XlsConnectionFactory {
    XlsConnection create(URL filename, Workbook workbook);
}
