package ru.innopolis.mputilov.sql.db_impl;

import ru.innopolis.mputilov.sql.builder.TableAliasPair;

public interface TableFactory {
    Table getOrCreateTable(TableAliasPair tableAliasPair);
}
