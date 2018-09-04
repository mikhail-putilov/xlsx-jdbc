package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db.Table;

public interface Context {
    void addProjectionColumns(String tableAlias, Columns columns);

    void addProjectionColumn(ColumnExp columnExp);

    Columns getProjectedColumnsFor(String tableAlias);

    Table getCurrentProcessingTable();

    void setCurrentProcessingTable(Table currentProcessingTable);

    ContextState getCurrentContextState();

    void setCurrentContextState(ContextState state);
}
