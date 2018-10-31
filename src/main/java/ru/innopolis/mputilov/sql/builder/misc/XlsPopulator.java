package ru.innopolis.mputilov.sql.builder.misc;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.poi.ss.usermodel.*;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.TableHeader;
import ru.innopolis.mputilov.sql.db.Tuple;
import ru.innopolis.mputilov.sql.jdbc.DataBase;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
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
    public void visitJoinEqExpression(JoinEqExp expression) {

    }

    @Override
    public void visitSelectExpression(SelectExp expression) {

    }

    @Override
    public void visitTableExpression(TableExp expression) {
        expression.setTable(db.getOrCreateTable(expression.getTableAliasPair()));
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
        TableHeader header = table.getHeader();
        Sheet sheet = workbook.getSheet(header.getTableName());
        ColumnsExp projectedColumns = evaluationContext.getProjectedColumnsFor(header.getTableAlias());
        header.setColumns(projectedColumns.toColumns());

        Map<ColumnExp, Integer> columnNameToColumnIndexInXls = PoiStream.stream(sheet)
                .filter(isColumnOfProjection(projectedColumns))
                .collect(toMap(header));
        if (projectedColumns.size() != columnNameToColumnIndexInXls.size()) {
            throw new IllegalStateException("Not all columns found in xls");
        }

        for (Row row : Iterables.skip(sheet, 1)) {
            // quick aspirin for Apache POI implementation:
            row.forEach(c -> c.setCellType(CellType.STRING));
            // preserve order of columns
            List<Object> tuple = extractColumnsPreserveOrder(projectedColumns, columnNameToColumnIndexInXls, row);
            table.addRawTuple(new Tuple(tuple));
        }
    }

    private List<Object> extractColumnsPreserveOrder(ColumnsExp order,
                                                     Map<ColumnExp, Integer> columnNameToColumnIndexInXls,
                                                     Row row) {
        return order.stream()
                .map(columnName -> row.getCell(columnNameToColumnIndexInXls.get(columnName)))
                .map(Cell::getStringCellValue)
                .collect(Collectors.toList());
    }

    private Collector<Cell, ?, Map<ColumnExp, Integer>> toMap(TableHeader header) {
        Function<Cell, ColumnExp> keyMapper = cell -> ColumnExp.of(header.getTableAlias(), cell.getStringCellValue());
        Function<Cell, Integer> valueMapper = Cell::getColumnIndex;
        return Collectors.toMap(keyMapper, valueMapper);
    }

    private Predicate<Cell> isColumnOfProjection(ColumnsExp projectedColumns) {
        return cell -> {
            String tableName = cell.getStringCellValue();
            return projectedColumns.containsColumnName(tableName);
        };
    }

}
