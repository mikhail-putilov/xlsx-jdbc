package ru.innopolis.mputilov.sql.builder;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.*;
import ru.innopolis.mputilov.sql.builder.vo.ColumnExp;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.Tuple;
import ru.innopolis.mputilov.sql.jdbc.DataBase;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XlsPopulator implements Visitor {
    private EvaluationContext evaluationContext;
    private DataBase db;
    private Workbook workbook;

    @Inject
    XlsPopulator(@Assisted EvaluationContext evaluationContext, @Assisted Workbook workbook, DataBase db) {
        this.evaluationContext = evaluationContext;
        this.db = db;
        this.workbook = workbook;
    }

    @Override
    public void visitJoinEqExpression(JoinEq expression) {

    }

    @Override
    public void visitSelectExpression(SelectExp expression) {

    }

    @Override
    public void visitTableExpression(TableExp expression) {
        expression.initTable(() -> db.getOrCreateTable(expression.getTableAliasPair()));
        Table table = expression.getTable();
        table.populateTable(this::fromWorkbook);
    }

    @Override
    public void visitTuplePredicateExpression(WhereExp expression) {

    }

    @Override
    public void visitSqlExpression(SqlExp expression) {

    }


    private void fromWorkbook(Table table) {
        Sheet sheet = workbook.getSheet(table.getTableName());
        ColumnsExp projectedColumns = evaluationContext.getProjectedColumnsFor(table.getTableAlias());
        table.setColumns(projectedColumns.toColumns());

        Map<ColumnExp, Integer> columnNameToColumnIndexInXls = PoiStream.stream(sheet)
                .filter(cell -> {
                    String tableName = cell.getStringCellValue();
                    return projectedColumns.containsColumnName(tableName);
                })
                .collect(Collectors.toMap(c1 -> ColumnExp.of(table.getTableAlias(), c1.getStringCellValue()), Cell::getColumnIndex));
        if (projectedColumns.size() != columnNameToColumnIndexInXls.size()) {
            throw new IllegalStateException("Not all columns found in xls");
        }

        for (Row row : Iterables.skip(sheet, 1)) {
            row.forEach(c -> c.setCellType(CellType.STRING));
            // preserve order of columns
            List<Object> tuple = projectedColumns.stream()
                    .map(columnName -> row.getCell(columnNameToColumnIndexInXls.get(columnName)).getStringCellValue())
                    .collect(Collectors.toList());
            table.addRawTuple(new Tuple(tuple));
        }
    }
}
