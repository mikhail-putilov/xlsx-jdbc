package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Getter
public class JoinEqExpression implements Expression<Table> {
    private Expression<Table> lhs;
    private Expression<Table> rhs;
    private String joinedTableAlias;
    private TuplePredicateExpression predicate;

    public JoinEqExpression(Expression<Table> lhs,
                            Expression<Table> rhs,
                            String joinedTableAlias,
                            TuplePredicateExpression predicate) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.joinedTableAlias = joinedTableAlias;
        this.predicate = predicate;
    }

    @Override
    public Table eval(Context ctx) {
        ctx.addProjectionColumn(predicate.getLhsColumn().getTableAlias(), predicate.getLhsColumn());
        ctx.addProjectionColumn(predicate.getRhsColumn().getTableAlias(), predicate.getRhsColumn());

        Table lhs = this.lhs.eval(ctx);
        Table rhs = this.rhs.eval(ctx);

        Columns lhsColumns = lhs.getColumns();
        Columns rhsColumns = rhs.getColumns();

        int indexOfLhsColumn = lhsColumns.getIndexOf(predicate.getLhsColumn());
        int indexOfRhsColumn = rhsColumns.getIndexOf(predicate.getRhsColumn());

        Function<List<String>, List<String>> lhsExtractor = list -> Arrays.asList(list.get(indexOfLhsColumn));
        Function<List<String>, List<String>> rhsExtractor = list -> Arrays.asList(list.get(indexOfRhsColumn));

        predicate.setLhsKeyExtractor(lhsExtractor);
        predicate.setRhsKeyExtractor(rhsExtractor);

        return lhs.join(rhs, joinedTableAlias, predicate.getLhsKeyExtractor(), predicate.getRhsKeyExtractor());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitJoinEqExpression(this);
        lhs.accept(visitor);
        rhs.accept(visitor);
        predicate.accept(visitor);
    }
}
