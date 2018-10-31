package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.misc.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.misc.Visitor;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.TableAliasPair;

import javax.annotation.Nullable;

@Getter
public class SqlExp extends TableExp {
    private final SelectExp selectExp;
    private final TableExp fromExp;
    private final WhereExp whereExp;

    public SqlExp(SelectExp selectExp,
                  @Nullable TableExp from,
                  @Nullable WhereExp whereExp,
                  @Nullable TableAliasPair tableAliasPair) {
        super(tableAliasPair);
        this.selectExp = selectExp;
        this.fromExp = from;
        this.whereExp = whereExp;
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        if (fromExp != null) {
            Table eval = fromExp.eval(ctx);
            ctx.setCurrentProcessingTable(eval);
            if (whereExp != null) {
                Table filtered = whereExp.eval(ctx);
                ctx.setCurrentProcessingTable(filtered);
            }
        }
        return selectExp.eval(ctx);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSqlExpression(this);
        selectExp.accept(visitor);
        if (fromExp != null) {
            fromExp.accept(visitor);
        }
        if (whereExp != null) {
            whereExp.accept(visitor);
        }
    }


}
