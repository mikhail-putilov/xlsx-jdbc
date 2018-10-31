package ru.innopolis.mputilov.sql.builder.misc;

import ru.innopolis.mputilov.sql.builder.*;

public interface Visitor {
    void visitJoinEqExpression(JoinEqExp expression);

    void visitSelectExpression(SelectExp expression);

    void visitTableExpression(TableExp expression);

    void visitTuplePredicateExpression(WhereExp expression);

    void visitSqlExpression(SqlExp expression);
}
