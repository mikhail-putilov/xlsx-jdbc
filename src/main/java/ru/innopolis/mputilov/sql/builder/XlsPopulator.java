package ru.innopolis.mputilov.sql.builder;

public class XlsPopulator implements Visitor {
    private EvaluationContext evaluationContext;

    public XlsPopulator(EvaluationContext evaluationContext) {

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
