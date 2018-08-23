package ru.innopolis.mputilov.sql.builder;

public interface ColumnExp extends Expression<Object> {
    String getName();

    String getTableAlias();

    boolean isStatic();

    Object getStatic();
}
