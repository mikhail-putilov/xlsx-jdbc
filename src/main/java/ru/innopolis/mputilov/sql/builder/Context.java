package ru.innopolis.mputilov.sql.builder;

public interface Context {
    void addProjectionColumns(String tableAlias, Columns columns);

    void addProjectionColumn(Column column);

    Columns getProjectedColumnsFor(String tableAlias);
}
