package ru.innopolis.mputilov.sql.db;

import lombok.Data;

@Data
public final class ColumnAliasPair {
    private final String tableAlias;
    private final String columnName;
}
