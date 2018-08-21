package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.db_impl.Table;
import ru.innopolis.mputilov.sql.db_impl.Tuple;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class PredicateExpression implements Expression<Boolean> {
    private final Column lhsColumn;
    private final Column rhsColumn;
    private Function<Tuple, Tuple> lhsKeyExtractor;
    private Function<Tuple, Tuple> rhsKeyExtractor;

    void setLhsKeyExtractor(Table lhs) {
        int indexOfLhsColumn = lhs.getColumns().getIndexOf(lhsColumn);
        lhsKeyExtractor = list -> Tuple.of(list.get(indexOfLhsColumn));
    }

    void setRhsKeyExtractor(Table rhs) {
        int indexOfRhsColumn = rhs.getColumns().getIndexOf(rhsColumn);
        rhsKeyExtractor = list -> Tuple.of(list.get(indexOfRhsColumn));
    }

    @Override
    public Boolean eval(Context ctx) {
        // no-op
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTuplePredicateExpression(this);
    }
}
