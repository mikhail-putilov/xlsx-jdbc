package ru.innopolis.mputilov.sql.jdbc.api;

import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.builder.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.XlsPopulator;

public interface XlsPopulatorFactory {
    XlsPopulator create(EvaluationContext evaluationContext, Workbook workbook);
}
