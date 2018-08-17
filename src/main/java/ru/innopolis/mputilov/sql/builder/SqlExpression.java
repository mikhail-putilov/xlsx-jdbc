package ru.innopolis.mputilov.sql.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.UUID;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class SqlExpression implements Expression<Table> {
    private final Expression<Columns> columns;
    private final Expression<Table> from;
    private final TuplePredicateExpression where;
    private TableAliasPair tableAliasPair;

    @Override
    public Table eval(Context ctx) {
        // set in context projected columns for each mentioned table in select expression
        Columns columns = this.columns.eval(ctx);
        // set in context projected columns for this particular sql expression (relation)
        if (tableAliasPair != null) {
            ctx.addProjectionColumns(tableAliasPair.getAlias(), columns);
        }

//        where.eval(ctx);
        Table eval = from.eval(ctx);
        if (tableAliasPair != null) {
            tableAliasPair = new TableAliasPair(tableAliasPair.getAlias(), eval.getTableName());
        }
        return eval;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSqlExpression(this);
        columns.accept(visitor);
        from.accept(visitor);
        where.accept(visitor);
    }


}
