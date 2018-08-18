package ru.innopolis.mputilov.sql.builder;

public interface Column {
    String getName();

    String getTableAlias();

    boolean isStatic();
}
