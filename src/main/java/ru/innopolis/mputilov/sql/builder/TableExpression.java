package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.Arrays;
import java.util.List;

public class TableExpression implements Expression<Table> {
    private TableAliasPair tableAliasPair;

    public TableExpression(TableAliasPair tableAliasPair) {
        this.tableAliasPair = tableAliasPair;
    }

    @Override
    public Table eval(Context ctx) {
        Columns projectedColumns = ctx.getProjectedColumnsFor(tableAliasPair.getAlias());

        return new Table();
    }
}
