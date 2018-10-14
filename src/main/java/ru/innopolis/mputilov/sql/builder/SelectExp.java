package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.vo.ColumnExp;
import ru.innopolis.mputilov.sql.db.Columns;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.Tuple;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SelectExp implements Expression<Table> {

    private ColumnsExp columns;
    /**
     * Given unordered tuple:
     * <pre>
     *      0 1 2
     *      | | |
     *      v v v
     * A = [b a c]
     * </pre>
     * and desirable order:
     * <pre>
     *      0 1 2
     *      | | |
     *      v v v
     * D = [a b c]
     * </pre>
     * permutation field maps D'th indexes to indexes in A
     * So, permutation would be in that example:
     * <pre>
     *      0 1 2
     *      | | |
     *      v v v
     * P = [1 0 2]
     * </pre>
     */
    private List<Integer> permutation;

    public SelectExp(ColumnExp... columnExp) {
        this.columns = new ColumnsExp(columnExp);
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        Table table = ctx.getCurrentProcessingTable();
        Columns actual = table.getColumns();
        permutation = columns.stream()
                .map(ColumnExp::toColumnAliasPair)
                .map(actual::getIndexOf)
                .collect(Collectors.toList());
        reorderTuples(table.getRawTuples());
        return table;
    }

    private void reorderTuples(List<Tuple> tuples) {
        for (Tuple t : tuples) {
            List<Object> unordered = t.getTuple();
            Object[] copy = unordered.toArray();
            int i = 0;
            for (Integer indexInUnorderedTuple : permutation) {
                unordered.set(i++, copy[indexInUnorderedTuple]);
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSelectExpression(this);
    }
}
