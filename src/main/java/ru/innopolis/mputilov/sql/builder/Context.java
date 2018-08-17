package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.List;

interface Context {

    List<String> getLhsTuple();

    List<String> getRhsTuple();

    Table getResult();

    void addProjectionColumns(String tableAlias, Columns columns);

    void addProjectionColumn(String tableAlias, Column column);

    void addProjectionColumn(Column column);

    Columns getProjectedColumnsFor(String tableAlias);
}
