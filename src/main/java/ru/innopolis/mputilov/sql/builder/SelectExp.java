package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.misc.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.misc.Visitor;
import ru.innopolis.mputilov.sql.db.ColumnAliasPair;
import ru.innopolis.mputilov.sql.db.Table;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SelectExp implements Exp<Table> {

    private ColumnsExp columns;

    public SelectExp(ColumnExp... columnExp) {
        this.columns = new ColumnsExp(columnExp);
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        Table table = ctx.getCurrentProcessingTable();
        List<ColumnAliasPair> desiredOrder = this.columns.stream()
                .map(ColumnExp::toColumnAliasPair)
                .collect(Collectors.toList());
        return table.reorder(desiredOrder);
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visitSelectExpression(this);
    }
}
