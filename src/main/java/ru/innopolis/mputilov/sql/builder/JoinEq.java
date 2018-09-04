package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.vo.TableAliasPair;

import java.util.UUID;

@Getter
public class JoinEq extends TableExp {
    private Expression<Table> lhs;
    private Expression<Table> rhs;
    private String joinedTableAlias;
    private WhereExp predicate;

    public JoinEq(Expression<Table> lhs,
                  Expression<Table> rhs,
                  String joinedTableAlias,
                  WhereExp predicate) {
        super(new TableAliasPair(joinedTableAlias, "Joined-" + UUID.randomUUID().toString()));
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

        table = lhs.join(rhs, joinedTableAlias, predicate);
        return table;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitJoinEqExpression(this);
        lhs.accept(visitor);
        rhs.accept(visitor);
        predicate.accept(visitor);
    }
}
