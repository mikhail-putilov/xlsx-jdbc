package ru.innopolis.mputilov.sql.builder;

interface Column {
    String getName();

    String getTableAlias();

    boolean isStatic();
}
