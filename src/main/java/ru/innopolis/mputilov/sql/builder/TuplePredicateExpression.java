package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
public class TuplePredicateExpression implements Expression<Boolean> {
    private Function<List<String>, List<String>> lhsKeyExtractor;
    private Function<List<String>, List<String>> rhsKeyExtractor;
    private Column lhsColumn;
    private Column rhsColumn;

    public TuplePredicateExpression(Column lhsColumn,
                                    Column rhsColumn) {
        this.lhsColumn = lhsColumn;
        this.rhsColumn = rhsColumn;
    }

    public void setLhsKeyExtractor(Table lhs) {
        int indexOfLhsColumn = lhs.getColumns().getIndexOf(lhsColumn);
        lhsKeyExtractor = list -> Collections.singletonList(list.get(indexOfLhsColumn));
    }

    public void setRhsKeyExtractor(Table rhs) {
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
