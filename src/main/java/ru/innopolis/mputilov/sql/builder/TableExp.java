package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.vo.TableAliasPair;

import java.util.function.Supplier;

@Setter
@Getter
public class TableExp implements Expression<Table> {
    protected TableAliasPair tableAliasPair;
    protected Table table;

    public TableExp(TableAliasPair tableAliasPair) {
        this.tableAliasPair = tableAliasPair;
    }

    void initTable(Supplier<Table> supplier) {
        if (table == null) {
            table = supplier.get();
        }
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
