package ru.innopolis.mputilov.sql.jdbc;

import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.builder.Context;
import ru.innopolis.mputilov.sql.builder.XlsPopulator;

public interface XlsPopulatorFactory {
    XlsPopulator create(Context evaluationContext, Workbook workbook);
}
