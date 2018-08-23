package ru.innopolis.mputilov.sql.builder;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.db_impl.Table;
import ru.innopolis.mputilov.sql.db_impl.Tuple;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class WhereExp implements Expression<Table> {
    private final Operation operation;
    private final ColumnExp lhsColumnExp;
    private final ColumnExp rhsColumnExp;
    private Function<Tuple, Tuple> lhsKeyExtractor;
    private Function<Tuple, Tuple> rhsKeyExtractor;

    void setLhsKeyExtractor(Table lhs) {
        int indexOfLhsColumn = lhs.getColumns().getIndexOf(lhsColumnExp);
        lhsKeyExtractor = list -> Tuple.of(list.get(indexOfLhsColumn));
    }

    void setRhsKeyExtractor(Table rhs) {
        int indexOfRhsColumn = rhs.getColumns().getIndexOf(rhsColumnExp);
        rhsKeyExtractor = list -> Tuple.of(list.get(indexOfRhsColumn));
    }

    @Override
    public Table eval(Context ctx) {
        if (ctx.getCurrentContextState() != ContextState.PROCESSING_TABLE) {
            throw new IllegalStateException();
        }
        Table currentProcessingTable = ctx.getCurrentProcessingTable();
        if (lhsColumnExp.isStatic() && rhsColumnExp.isStatic()) {
            Boolean match = operation.apply(lhsColumnExp.getStatic(), rhsColumnExp.getStatic());
            if (match) {
                return currentProcessingTable;
            } else {
                currentProcessingTable.removeIf(tuple -> true);
                return currentProcessingTable;
            }
        }
        if (!lhsColumnExp.isStatic() && !rhsColumnExp.isStatic()) {
            Preconditions.checkState(lhsKeyExtractor == null, "current predicate already has lhsKeyExtractor");
            Preconditions.checkState(rhsKeyExtractor == null, "current predicate already has rhsKeyExtractor");
            setLhsKeyExtractor(currentProcessingTable);
            setRhsKeyExtractor(currentProcessingTable);
            currentProcessingTable.removeIf(t -> !operation.apply(lhsKeyExtractor.apply(t), rhsKeyExtractor.apply(t)));
        }
        if (!rhsColumnExp.isStatic() && lhsColumnExp.isStatic()) {
            Preconditions.checkState(rhsKeyExtractor == null, "current predicate already has rhsKeyExtractor");
            setRhsKeyExtractor(currentProcessingTable);
            final Object lhsStatic = lhsColumnExp.getStatic();
            currentProcessingTable.removeIf(t -> !operation.apply(lhsStatic, rhsKeyExtractor.apply(t)));
        } else if (rhsColumnExp.isStatic() && !lhsColumnExp.isStatic()) {
            Preconditions.checkState(lhsKeyExtractor == null, "current predicate already has lhsKeyExtractor");
            setLhsKeyExtractor(currentProcessingTable);
            final Tuple rhsStatic = Tuple.of(rhsColumnExp.getStatic());
            currentProcessingTable.removeIf(t -> {
                Tuple key = lhsKeyExtractor.apply(t);
                return !operation.apply(key, rhsStatic);
            });
        } else {
            throw new IllegalStateException();
        }
        return currentProcessingTable;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTuplePredicateExpression(this);
    }
}
