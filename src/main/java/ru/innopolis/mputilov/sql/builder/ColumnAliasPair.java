package ru.innopolis.mputilov.sql.builder;

import lombok.Data;

@Data
public final class ColumnAliasPair implements Column {
    final String tableAlias;
    final String name;
    final boolean isStatic = false;
}
