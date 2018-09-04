package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.Tuple;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SelectExp implements Expression<Table> {

    private Columns columns;
    /**
     * Given unordered tuple:
     *      0 1 2
     *      | | |
     *      v v v
     * A = [b a c]
     * and desirable order:
     *      0 1 2
     *      | | |
     *      v v v
     * D = [a b c]
     *
     * permutation field maps D'th indexes to indexes in A
     * So, permutation would be in that example:
     *      0 1 2
     *      | | |
     *      v v v
     * P = [1 0 2]
     *
     */
    private List<Integer> permutation;

    public SelectExp(ColumnExp... columnExp) {
        this.columns = new Columns(columnExp);
    }

    @Override
    public Table eval(Context ctx) {
        if (ctx.getCurrentContextState() == ContextState.PROCESSING_TABLE) {
            Table table = ctx.getCurrentProcessingTable();
            Columns actual = table.getColumns();
            permutation = columns.stream().map(actual::getIndexOf).collect(Collectors.toList());
            reorderTuples(table.getRawTuples());
            return table;
        } else {
            throw new UnsupportedOperationException("Static processing is not supported yet");
        }
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
