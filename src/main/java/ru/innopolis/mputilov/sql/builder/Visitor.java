package ru.innopolis.mputilov.sql.builder;

public interface Visitor {
    void visitJoinEqExpression(JoinEqExpression expression);

    void visitSelectExpression(SelectExpression expression);

    void visitTableExpression(TableExpression expression);

    void visitTuplePredicateExpression(PredicateExpression expression);

    void visitSqlExpression(SqlExpression expression);
}
