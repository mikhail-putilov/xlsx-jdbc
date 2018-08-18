package ru.innopolis.mputilov.sql.builder;

import org.junit.Test;
import ru.innopolis.mputilov.sql.db_impl.Table;

import static org.junit.Assert.*;

public class SqlExpressionTest {
    @Test
    public void trivial() {
        Expression<Table> exp = new SqlExpression(
                new SelectExpression(
                        new ColumnAliasPair("t1", "col1"),
                        new ColumnAliasPair("t1", "col2"),
                        new ColumnAliasPair("t1", "col3"),
                        new ColumnAliasPair("t2", "col5")),
                new JoinEqExpression(
                        new TableExpression(new TableAliasPair("t1", "table1")),
                        new TableExpression(new TableAliasPair("t2", "table2")),
                        "j",
                        new TuplePredicateExpression(
                                new ColumnAliasPair("t1", "col1"),
                                new ColumnAliasPair("t2", "col2"))),
                new TuplePredicateExpression(
                        new ColumnAliasPair("t1", "col1"),
                        new StaticColumn(5)));
        Context ctx = new EvaluationContext();
        Table joinedResult = exp.eval(ctx);
    }
}