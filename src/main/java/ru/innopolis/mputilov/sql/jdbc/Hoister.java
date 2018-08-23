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
    public void visitJoinEqExpression(JoinEq expression) {
        //no-op
    }

    @Override
    public void visitSelectExpression(SelectExp expression) {
        Columns columns = expression.getColumns();
        columns.getAllAliases()
                .forEach(tableAlias -> evaluationContext.addProjectionColumns(tableAlias, columns.getColumnsForTableWithAlias(tableAlias)));
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
