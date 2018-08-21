package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.db_impl.Table;

@Getter
public class JoinEqExpression implements Expression<Table> {
    private Expression<Table> lhs;
    private Expression<Table> rhs;
    private String joinedTableAlias;
    private PredicateExpression predicate;

    public JoinEqExpression(Expression<Table> lhs,
                            Expression<Table> rhs,
                            String joinedTableAlias,
                            PredicateExpression predicate) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.joinedTableAlias = joinedTableAlias;
        this.predicate = predicate;
    }

    @Override
    public Table eval(Context ctx) {
        Table lhs = this.lhs.eval(ctx);
        Table rhs = this.rhs.eval(ctx);

        predicate.setRhsKeyExtractor(rhs);
        predicate.setLhsKeyExtractor(lhs);

        return lhs.join(rhs, joinedTableAlias, predicate);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitJoinEqExpression(this);
        lhs.accept(visitor);
        rhs.accept(visitor);
        predicate.accept(visitor);
    }
}
