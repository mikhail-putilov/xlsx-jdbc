package ru.innopolis.mputilov.sql.jdbc;

import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.builder.StatementBuilder;
import ru.innopolis.mputilov.sql.db_impl.DataBase;
import ru.innopolis.mputilov.sql.db_impl.StatementModel;
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

    public XlsResultSet executeQuery(StatementBuilder.TerminalStep terminalStep) {
        StatementModel statement = terminalStep.build();
        Table resultTable;
        if (statement.isJoined()) {
            Table leftTable = db.retrieveTable(statement.getLeftTableName());
            Table joined = leftTable.join(db.retrieveTable(statement.getRightTableName()));
            resultTable = joined.doTheRest(statement);
        } else {
            Table table = db.retrieveTable(statement.getFromTableName());
            resultTable = table.doTheRest(statement);
        }
        return resultTable.getResultSet();
    }
}
