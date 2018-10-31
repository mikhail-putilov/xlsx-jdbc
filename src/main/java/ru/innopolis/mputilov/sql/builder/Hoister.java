package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.builder.vo.ColumnExp;

/**
 * Hoist definition of columns from whole sql expression and fill context with that data
 */
public class Hoister implements Visitor {
    private EvaluationContext evaluationContext;

    public Hoister(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public void visitJoinEqExpression(JoinEq expression) {
        //no-op
    }

    @Override
    public void visitSelectExpression(SelectExp expression) {
        ColumnsExp columns = expression.getColumns();
        columns.getAllAliases()
                .forEach(tableAlias ->
                        evaluationContext.addProjectionColumns(tableAlias, columns.getColumnsForTableWithAlias(tableAlias)));
    }

    @Override
    public void visitTableExpression(TableExp expression) {
        // no-op
    }

    @Override
    public void visitTuplePredicateExpression(WhereExp expression) {
        ColumnExp lhs = expression.getLhsColumnExp();
        ColumnExp rhs = expression.getRhsColumnExp();
        evaluationContext.addProjectionColumn(lhs);
        evaluationContext.addProjectionColumn(rhs);
    }

    @Override
    public void visitSqlExpression(SqlExp expression) {
        //no-op
    }
}
