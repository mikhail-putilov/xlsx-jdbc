package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.innopolis.mputilov.sql.builder.Columns;
import ru.innopolis.mputilov.sql.builder.TableAliasPair;
import ru.innopolis.mputilov.sql.builder.TuplePredicateExpression;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public class Table {
    private final String tableName;
    private final String tableAlias;
    @Setter
    private Columns columns;
    @Getter(AccessLevel.PACKAGE)
    private Map<PrimaryKey, List<String>> data = new TreeMap<>();
    private List<List<String>> rawTuples = new ArrayList<>();

    public Table(TableAliasPair tableAliasPair) {
        this.tableName = tableAliasPair.getTableName();
        this.tableAlias = tableAliasPair.getAlias();
    }

    public boolean isReccurent() {
        return false;
    }

    private boolean isRawTuplesAvailable() {
        return !rawTuples.isEmpty();
    }

    private boolean isEmpty() {
        return data.isEmpty() && rawTuples.isEmpty();
    }

    public void addRawTuple(List<String> rawTuple) {
        rawTuples.add(rawTuple);
    }

    private Table probing(Table other, String joinedTableAlias) {
        if (data.isEmpty()) {
            throw new IllegalStateException("Cannot join raw tables, use #put(PrimaryKey, List<String>) method");
        }
        Map<PrimaryKey, List<String>> small;
        Map<PrimaryKey, List<String>> big;
        Table joined;
        boolean isLeftSmall = true;
        if (getData().size() < other.getData().size()) {
            small = getData();
            big = other.getData();
            joined = new Table(Joiner.on("_").join("Joined", tableName, other.tableName), joinedTableAlias);
            joined.columns = columns.combine(other.columns);
        } else {
            isLeftSmall = false;
            small = other.getData();
            big = getData();
            joined = new Table(Joiner.on("_").join("Joined", other.tableName, tableName), joinedTableAlias);
            joined.columns = other.columns.combine(columns);
        }
        final boolean isLeftSmallFinal = isLeftSmall;
        small.forEach((key, tupleFromSmall) -> {
            List<String> tupleFromBig = big.get(key);
            List<String> joinedTuple = new ArrayList<>(tupleFromSmall.size() + tupleFromBig.size());
            if (isLeftSmallFinal) {
                joinedTuple.addAll(tupleFromSmall);
                joinedTuple.addAll(tupleFromBig);
            } else {
                joinedTuple.addAll(tupleFromBig);
                joinedTuple.addAll(tupleFromSmall);
            }
            joined.addRawTuple(joinedTuple);
        });
        return joined;
    }

    public XlsResultSet getResultSet() {
        return new XlsResultSet(rawTuples);
    }

    public void put(PrimaryKey key, List<String> tuple) {
        data.put(key, tuple);
    }

    public Table join(Table rhs, String joinedTableAlias, TuplePredicateExpression predicate) {
        if (isReccurent()) {
            throw new UnsupportedOperationException("cannot join reccurent table " + tableName);
        }
        if (rhs.isReccurent()) {
            throw new UnsupportedOperationException("cannot join reccurent table" + rhs.tableName);
        }
        if (isEmpty()) {
            throw new IllegalStateException("Lhs table is empty, nothing to join");
        }
        if (rhs.isEmpty()) {
            throw new IllegalStateException("Rhs table is empty, nothing to join");
        }
        if (isRawTuplesAvailable()) {
            rehash(predicate.getLhsKeyExtractor());
        } else {
            throw new IllegalStateException("Lhs table does not have raw tuples to rehash");
        }
        if (rhs.isRawTuplesAvailable()) {
            rhs.rehash(predicate.getRhsKeyExtractor());
        } else {
            throw new IllegalStateException("Rhs table does not have raw tuple to rehash");
        }
        return this.probing(rhs, joinedTableAlias);
    }

    private void rehash(Function<List<String>, List<String>> keyExtractor) {
        if (!data.isEmpty()) {
            data.clear();
        }
        rawTuples.forEach(t -> {
            data.put(CompoundStringPrimaryKey.of(keyExtractor, t), t);
        });
    }
}
