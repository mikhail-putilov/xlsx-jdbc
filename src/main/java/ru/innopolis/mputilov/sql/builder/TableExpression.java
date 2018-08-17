package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db_impl.Table;

@Setter
@Getter
public class TableExpression implements Expression<Table> {
    private TableAliasPair tableAliasPair;
    private Table table;

    public TableExpression(TableAliasPair tableAliasPair) {
        this.tableAliasPair = tableAliasPair;
        this.table = new Table(tableAliasPair);
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
