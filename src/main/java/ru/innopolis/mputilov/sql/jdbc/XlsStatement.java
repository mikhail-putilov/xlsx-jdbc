package ru.innopolis.mputilov.sql.jdbc;

import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.db.ResultSet;
import ru.innopolis.mputilov.sql.jdbc.api.XlsPopulatorFactory;
import ru.innopolis.mputilov.sql.db.Table;

import javax.inject.Inject;

/**
 * Encapsulates one particular statement which must be computed
 */
public class XlsStatement {
    private final Workbook workbook;
    private final XlsPopulatorFactory xlsPopulatorFactory;

    @Inject
    XlsStatement(@Assisted Workbook workbook, XlsPopulatorFactory xlsPopulatorFactory) {
        this.workbook = workbook;
        this.xlsPopulatorFactory = xlsPopulatorFactory;
    }

    public ResultSet executeQuery(Expression<Table> expression) {
        Context evaluationContext = new EvaluationContext();
        Visitor hoister = new Hoister(evaluationContext);
        expression.accept(hoister);

        Visitor populator = xlsPopulatorFactory.create(evaluationContext, workbook);
        expression.accept(populator);

        Table resultTable = expression.eval(evaluationContext);
        return resultTable.getResultSet();
    }
}
