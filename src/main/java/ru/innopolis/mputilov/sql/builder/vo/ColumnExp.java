package ru.innopolis.mputilov.sql.builder.vo;

import lombok.Data;
import lombok.Getter;
import ru.innopolis.mputilov.sql.db.vo.ColumnAliasPair;

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

    public ColumnAliasPair toColumnAliasPair() {
        return new ColumnAliasPair(tableAlias, columnName);
    }

    public boolean isStatic() {
        return staticValue != null;
    }
}
