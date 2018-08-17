package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class TuplePredicateExpression implements Expression<Boolean> {
    private Function<List<String>, List<String>> lhsKeyExtractor;
    private Function<List<String>, List<String>> rhsKeyExtractor;
    private final Column lhsColumn;
    private final Column rhsColumn;

    void setLhsKeyExtractor(Table lhs) {
        int indexOfLhsColumn = lhs.getColumns().getIndexOf(lhsColumn);
        lhsKeyExtractor = list -> Collections.singletonList(list.get(indexOfLhsColumn));
    }

    void setRhsKeyExtractor(Table rhs) {
        int indexOfRhsColumn = rhs.getColumns().getIndexOf(rhsColumn);
        rhsKeyExtractor = list -> Collections.singletonList(list.get(indexOfRhsColumn));
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
