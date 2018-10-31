package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.misc.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.misc.Visitor;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.TableAliasPair;

import java.util.UUID;

@Getter
public class JoinEqExp extends TableExp {
    private Exp<Table> lhs;
    private Exp<Table> rhs;
    private String joinedTableAlias;
    private WhereExp predicate;

    public JoinEqExp(Exp<Table> lhs,
                     Exp<Table> rhs,
                     String joinedTableAlias,
                     WhereExp predicate) {
        super(new TableAliasPair(joinedTableAlias, "Joined-" + UUID.randomUUID().toString()));
        this.lhs = lhs;
        this.rhs = rhs;
        this.joinedTableAlias = joinedTableAlias;
        this.predicate = predicate;
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        Table lhs = this.lhs.eval(ctx);
        Table rhs = this.rhs.eval(ctx);

        predicate.createRhsKeyExtractor(rhs);
        predicate.createLhsKeyExtractor(lhs);

        table = lhs.join(rhs, joinedTableAlias, predicate.getLhsKeyExtractor(), predicate.getRhsKeyExtractor());
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
