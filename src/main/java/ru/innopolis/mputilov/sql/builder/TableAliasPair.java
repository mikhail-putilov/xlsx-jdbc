package ru.innopolis.mputilov.sql.builder;

import lombok.Data;

@Data
public final class TableAliasPair {
    private final String alias;
    private final String tableName;
}
