package ru.innopolis.mputilov.sql.builder;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.builder.misc.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.misc.Operation;
import ru.innopolis.mputilov.sql.builder.misc.Visitor;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.Tuple;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class WhereExp implements Exp<Table> {
    private final Operation operation;
    private final ColumnExp lhsColumnExp;
    private final ColumnExp rhsColumnExp;
    private Function<Tuple, Tuple> lhsKeyExtractor;
    private Function<Tuple, Tuple> rhsKeyExtractor;

    void createLhsKeyExtractor(Table lhs) {
        int indexOfLhsColumn = lhs.getHeader().getColumns().getIndexOf(lhsColumnExp.toColumnAliasPair());
        lhsKeyExtractor = list -> Tuple.of(list.get(indexOfLhsColumn));
    }

    void createRhsKeyExtractor(Table rhs) {
        int indexOfRhsColumn = rhs.getHeader().getColumns().getIndexOf(rhsColumnExp.toColumnAliasPair());
        rhsKeyExtractor = list -> Tuple.of(list.get(indexOfRhsColumn));
    }

    @Override
    public Table eval(EvaluationContext ctx) {
        Table currentProcessingTable = ctx.getCurrentProcessingTable();
        if (lhsColumnExp.isStatic() && rhsColumnExp.isStatic()) {
            Boolean match = operation.apply(lhsColumnExp.getStaticValue(), rhsColumnExp.getStaticValue());
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
            createLhsKeyExtractor(currentProcessingTable);
            createRhsKeyExtractor(currentProcessingTable);
            currentProcessingTable.removeIf(t -> !operation.apply(lhsKeyExtractor.apply(t), rhsKeyExtractor.apply(t)));
        }
        if (!rhsColumnExp.isStatic() && lhsColumnExp.isStatic()) {
            Preconditions.checkState(rhsKeyExtractor == null, "current predicate already has rhsKeyExtractor");
            createRhsKeyExtractor(currentProcessingTable);
            final Object lhsStatic = lhsColumnExp.getStaticValue();
            currentProcessingTable.removeIf(t -> !operation.apply(lhsStatic, rhsKeyExtractor.apply(t)));
        } else if (rhsColumnExp.isStatic() && !lhsColumnExp.isStatic()) {
            Preconditions.checkState(lhsKeyExtractor == null, "current predicate already has lhsKeyExtractor");
            createLhsKeyExtractor(currentProcessingTable);
            final Tuple rhsStatic = Tuple.of(rhsColumnExp.getStaticValue());
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
