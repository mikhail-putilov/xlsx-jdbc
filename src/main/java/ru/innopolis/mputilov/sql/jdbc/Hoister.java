package ru.innopolis.mputilov.sql.jdbc;

import ru.innopolis.mputilov.sql.builder.*;

/**
 * Hoist definition of columns from whole sql expression and fill context with that data
 */
public class Hoister implements Visitor {
    private Context evaluationContext;

    Hoister(Context evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public void visitJoinEqExpression(JoinEqExpression expression) {
        //no-op
    }

    @Override
    public void visitSelectExpression(SelectExpression expression) {
        Columns columns = expression.getColumns();
        columns.getAllAliases()
                .forEach(tableAlias -> evaluationContext.addProjectionColumns(tableAlias, columns.getColumnsForTableWithAlias(tableAlias)));
    }

    @Override
    public void visitTableExpression(TableExpression expression) {
        // no-op
    }

    @Override
    public void visitTuplePredicateExpression(PredicateExpression expression) {
        Column lhs = expression.getLhsColumn();
        Column rhs = expression.getRhsColumn();
        evaluationContext.addProjectionColumn(lhs);
        evaluationContext.addProjectionColumn(rhs);
    }

    @Override
    public void visitSqlExpression(SqlExpression expression) {
        //no-op
    }
}
