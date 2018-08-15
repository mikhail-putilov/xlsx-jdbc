package ru.innopolis.mputilov.sql.jdbc;

import com.google.common.collect.Iterables;
import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.*;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.db_impl.DataBase;
import ru.innopolis.mputilov.sql.db_impl.StatementModel;
import ru.innopolis.mputilov.sql.db_impl.Table;
import ru.innopolis.mputilov.sql.db_impl.TableInfo;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Encapsulates one particular statement which must be computed
 */
public class XlsStatement {
    private final Workbook workbook;
    private final DataBase db;
    private StatementModel statement;
    @Inject
    XlsStatement(@Assisted Workbook workbook,
                 DataBase db) {
        this.workbook = workbook;
        this.db = db;
    }

    public XlsResultSet executeQuery(Expression<Table> expression) {
        EvaluationContext evaluationContext = new EvaluationContext();
        Visitor hoister = new Hoister(evaluationContext);
        expression.accept(hoister);

        Visitor populator = new XlsPopulator(evaluationContext);
        expression.accept(populator);

        Table resultTable = expression.eval(evaluationContext);
        return resultTable.getResultSet();
    }

    public XlsResultSet executeQuery(ExpressionBuilder.TerminalStep terminalStep) {
        statement = terminalStep.build(db);
        // table1 join table2 on t1 = t2 join table3 on t3=t2
        Table resultTable;
        if (statement.isJoined()) {
            // tableinfo = table + columns that we are interested in (columns that participate in join and select statements)
            List<TableInfo> infos = statement.getTableInfosWithBackingTable();
            infos.forEach(this::populateTable);
            TableInfo joined = infos.stream().reduce(TableInfo::join).orElseThrow(() -> new IllegalStateException("Cannot join tables"));
            resultTable = joined.getBackingTable();
        } else {
//            Table table = db.setBackingTable(statement.getFromTableName());
//            resultTable = table.doTheRest(statement);
            resultTable = null;
        }
        return resultTable.getResultSet();
    }

    private void populateTable(TableInfo table) {
        Sheet sheet = workbook.getSheet(table.getSheetName());
        Set<String> interestedColumns = table.getInterestedColumns();

        Map<String, Integer> nameToIndexInXls = StreamSupport.stream(sheet.getRow(0).spliterator(), false)
                .filter(cell -> interestedColumns.contains(cell.getStringCellValue()))
                .collect(Collectors.toMap(Cell::getStringCellValue, Cell::getColumnIndex));
        if (interestedColumns.size() != nameToIndexInXls.size()) {
            throw new IllegalStateException("Not all columns found in xls");
        }
        table.setNameToIndexMap(nameToIndexInXls);


        for (Row row : Iterables.skip(sheet, 1)) {
            row.forEach(c -> c.setCellType(CellType.STRING));
            // preserve order of columns
            List<String> tuple = interestedColumns.stream()
                    .map(columnName -> row.getCell(nameToIndexInXls.get(columnName)).getStringCellValue())
                    .collect(Collectors.toList());
            table.putTupleFromXls(tuple);
        }
    }
}
