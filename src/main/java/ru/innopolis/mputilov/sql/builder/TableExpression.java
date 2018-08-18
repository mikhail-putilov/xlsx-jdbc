package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db_impl.Table;

@Setter
@Getter
@RequiredArgsConstructor
public class TableExpression implements Expression<Table> {
    private final TableAliasPair tableAliasPair;
    private final Table table;

    public TableExpression(TableAliasPair tableAliasPair) {
        this(tableAliasPair, new Table(tableAliasPair));
    }

    @Override
    public Table eval(Context ctx) {
        return table;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTableExpression(this);
    }
}
