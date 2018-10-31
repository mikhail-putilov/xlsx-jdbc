package ru.innopolis.mputilov.sql.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor(staticName = "createFully")
@RequiredArgsConstructor(staticName = "createPartly")
public class TableHeader {
    private final String tableName;
    private final String tableAlias;
    private Columns columns;
}
