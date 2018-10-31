package ru.innopolis.mputilov.sql.db;

import lombok.Data;

@Data
public final class TableAliasPair {
    private final String alias;
    private final String tableName;
}
