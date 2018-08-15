package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db_impl.Table;

public class JoinEqExpression implements Expression<Table> {
    private Expression<Table> lhs;
    private Expression<Table> rhs;
    private TuplePredicateExpression predicate;

    public JoinEqExpression(Expression<Table> lhs,
                            Expression<Table> rhs,
                            TuplePredicateExpression predicate) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.predicate = predicate;
    }

    @Override
    public Table eval(Context ctx) {
        ctx.addProjectionColumn(predicate.getLhs().getTableAlias(), predicate.getLhs());
        ctx.addProjectionColumn(predicate.getRhs().getTableAlias(), predicate.getRhs());
        Table lhs = this.lhs.eval(ctx);
        Table rhs = this.rhs.eval(ctx);

        return lhs.join(rhs, predicate.getLhsKeyExtractor(), predicate.getRhsKeyExtractor());
    }
}
