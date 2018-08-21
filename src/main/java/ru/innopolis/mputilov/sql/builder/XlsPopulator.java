package ru.innopolis.mputilov.sql.builder;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.*;
import ru.innopolis.mputilov.sql.db_impl.DataBase;
import ru.innopolis.mputilov.sql.db_impl.Table;
import ru.innopolis.mputilov.sql.db_impl.Tuple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class XlsPopulator implements Visitor {
    private Context evaluationContext;
    private DataBase db;
    private Workbook workbook;

    @Inject
    XlsPopulator(@Assisted Context evaluationContext, @Assisted Workbook workbook, DataBase db) {
        this.evaluationContext = evaluationContext;
        this.db = db;
        this.workbook = workbook;
    }

    @Override
    public void visitJoinEqExpression(JoinEqExpression expression) {

    }

    @Override
    public void visitSelectExpression(SelectExpression expression) {

    }

    @Override
    public void visitTableExpression(TableExpression expression) {
        expression.initTable(() -> db.getOrCreateTable(expression.getTableAliasPair()));
        Table table = expression.getTable();
        table.populateTable(this::fromWorkbook);
    }

    @Override
    public void visitTuplePredicateExpression(PredicateExpression expression) {

    }

    @Override
    public void visitSqlExpression(SqlExpression expression) {

    }


    private void fromWorkbook(Table table) {
        Sheet sheet = workbook.getSheet(table.getTableName());
        Columns projectedColumns = evaluationContext.getProjectedColumnsFor(table.getTableAlias());
        table.setColumns(projectedColumns);

        Map<Column, Integer> nameToIndexInXls = StreamSupport.stream(sheet.getRow(0).spliterator(), false)
                .filter(cell -> projectedColumns.containsTableName(cell.getStringCellValue()))
                .collect(Collectors.toMap(c -> new ColumnAliasPair(table.getTableAlias(), c.getStringCellValue()), Cell::getColumnIndex));
        if (projectedColumns.size() != nameToIndexInXls.size()) {
            throw new IllegalStateException("Not all columns found in xls");
        }

        for (Row row : Iterables.skip(sheet, 1)) {
            row.forEach(c -> c.setCellType(CellType.STRING));
            // preserve order of columns
            List<Object> tuple = projectedColumns.stream()
                    .map(columnName -> row.getCell(nameToIndexInXls.get(columnName)).getStringCellValue())
                    .collect(Collectors.toList());
            table.addRawTuple(new Tuple(tuple));
        }
    }
}
