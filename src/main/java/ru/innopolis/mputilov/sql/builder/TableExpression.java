package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.function.Supplier;

@Setter
@Getter
@RequiredArgsConstructor
public class TableExpression implements Expression<Table> {
    private final TableAliasPair tableAliasPair;
    private Table table;

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
