package ru.innopolis.mputilov.sql.jdbc;

import ru.innopolis.mputilov.sql.builder.*;

public class Hoister implements Visitor {
    private EvaluationContext evaluationContext;

    public Hoister(EvaluationContext evaluationContext) {

        this.evaluationContext = evaluationContext;
    }

    @Override
    public void visitJoinEqExpression(JoinEqExpression expression) {

    }

    @Override
    public void visitSelectExpression(SelectExpression expression) {

    }

    @Override
    public void visitTableExpression(TableExpression expression) {

    }

    @Override
    public void visitTuplePredicateExpression(TuplePredicateExpression expression) {

    }

    @Override
    public void visitSqlExpression(SqlExpression expression) {

    }
}
