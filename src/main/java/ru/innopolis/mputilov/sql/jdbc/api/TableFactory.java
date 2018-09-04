package ru.innopolis.mputilov.sql.jdbc.api;

import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.vo.TableAliasPair;

public interface TableFactory {
    Table getOrCreateTable(TableAliasPair tableAliasPair);
}
