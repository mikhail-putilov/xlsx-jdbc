package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.TableAliasPair;

@Setter
@Getter
public class TableExp implements Expression<Table> {
    protected TableAliasPair tableAliasPair;
    protected Table table;

    public TableExp(TableAliasPair tableAliasPair) {
        this.tableAliasPair = tableAliasPair;
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        return table;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTableExpression(this);
    }
}
