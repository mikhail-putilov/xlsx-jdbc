package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.innopolis.mputilov.sql.builder.Columns;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

@RequiredArgsConstructor
public class Table {
    @Getter
    private final String id;
    private Columns columns;
    @Getter(AccessLevel.PACKAGE)
    private Map<PrimaryKey, List<String>> data = new TreeMap<>();
    private List<List<String>> rawTuples = new ArrayList<>();

    public boolean isReccurent() {
        return false;
    }

    private boolean isRawTuplesAvailable() {
        return !rawTuples.isEmpty();
    }

    private boolean isEmpty() {
        return data.isEmpty() && rawTuples.isEmpty();
    }

    private void addRawTuple(List<String> rawTuple) {
        rawTuples.add(rawTuple);
    }

    public Table join(Table other) {
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
            joined = new Table(Joiner.on("_").join("Joined", id, other.id));
            joined.columns = columns.combine(other.columns);
        } else {
            isLeftSmall = false;
            small = other.getData();
            big = getData();
            joined = new Table(Joiner.on("_").join("Joined", other.id, id));
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

    public Table join(Table rhs, Function<List<String>, List<String>> lhsKeyExtractor, Function<List<String>, List<String>> rhsKeyExtractor) {
        if (isReccurent()) {
            throw new UnsupportedOperationException("cannot join reccurent table " + getId());
        }
        if (rhs.isReccurent()) {
            throw new UnsupportedOperationException("cannot join reccurent table" + rhs.getId());
        }
        if (isEmpty()) {
            throw new IllegalStateException("Lhs table is empty, nothing to join");
        }
        if (rhs.isEmpty()) {
            throw new IllegalStateException("Rhs table is empty, nothing to join");
        }
        if (isRawTuplesAvailable()) {
            rehash(lhsKeyExtractor);
        } else {
            throw new IllegalStateException("Lhs table does not have raw tuples to rehash");
        }
        if (rhs.isRawTuplesAvailable()) {
            rhs.rehash(rhsKeyExtractor);
        } else {
            throw new IllegalStateException("Rhs table does not have raw tuple to rehash");
        }
        return this.join(rhs);
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
