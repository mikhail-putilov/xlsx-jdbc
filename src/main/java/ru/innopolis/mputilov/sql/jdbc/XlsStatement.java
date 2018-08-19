package ru.innopolis.mputilov.sql.jdbc;

import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.db_impl.DataBase;
import ru.innopolis.mputilov.sql.db_impl.Table;

import javax.inject.Inject;

/**
 * Encapsulates one particular statement which must be computed
 */
public class XlsStatement {
    private final Workbook workbook;
    private final DataBase db;

    @Inject
    XlsStatement(@Assisted Workbook workbook,
                 DataBase db) {
        this.workbook = workbook;
        this.db = db;
    }

    public XlsResultSet executeQuery(Expression<Table> expression) {
        Context evaluationContext = new EvaluationContext();
        Visitor hoister = new Hoister(evaluationContext);
        expression.accept(hoister);

        Visitor populator = new XlsPopulator(evaluationContext, workbook);
        expression.accept(populator);

        Table resultTable = expression.eval(evaluationContext);
        return resultTable.getResultSet();
    }
}
