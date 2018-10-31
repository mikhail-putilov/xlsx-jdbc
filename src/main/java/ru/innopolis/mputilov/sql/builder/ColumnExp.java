package ru.innopolis.mputilov.sql.builder;

import lombok.Data;
import lombok.Getter;
import ru.innopolis.mputilov.sql.db.ColumnAliasPair;

@Data(staticConstructor = "of")
public final class ColumnExp {
    private final String tableAlias;
    private final String columnName;
    @Getter
    private Object staticValue;

    public static ColumnExp staticColumn(Object value) {
        ColumnExp column = ColumnExp.of(null, null);
        column.staticValue = value;
        return column;
    }

    ColumnAliasPair toColumnAliasPair() {
        return new ColumnAliasPair(tableAlias, columnName);
    }

    boolean isStatic() {
        return staticValue != null;
    }
}
