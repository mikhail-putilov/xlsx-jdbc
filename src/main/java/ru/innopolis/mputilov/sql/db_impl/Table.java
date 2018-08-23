package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.innopolis.mputilov.sql.builder.Columns;
import ru.innopolis.mputilov.sql.builder.TableAliasPair;
import ru.innopolis.mputilov.sql.builder.WhereExp;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class Table {
    private final String tableName;
    private final String tableAlias;
    @Setter
    private Columns columns;
    @Getter(AccessLevel.PACKAGE)
    private TreeMap<Tuple, Tuple> hashed = new TreeMap<>();
    private List<Tuple> rawTuples = new ArrayList<>();

    Table(TableAliasPair tableAliasPair) {
        this.tableName = tableAliasPair.getTableName();
        this.tableAlias = tableAliasPair.getAlias();
    }

    private boolean isReccurent() {
        // todo
        return false;
    }

    public void populateTable(Consumer<Table> consumer) {
        if (isRawTuplesAvailable() || !hashed.isEmpty()) {
            throw new IllegalStateException();
        }
        consumer.accept(this);
    }

    private boolean isRawTuplesAvailable() {
        return !rawTuples.isEmpty();
    }

    private boolean isEmpty() {
        return hashed.isEmpty() && rawTuples.isEmpty();
    }

    public void addRawTuple(Tuple rawTuple) {
        rawTuples.add(rawTuple);
    }

    private Table probing(Table other, String joinedTableAlias) {
        if (hashed.isEmpty()) {
            throw new IllegalStateException("Cannot join raw tables");
        }
        Map<Tuple, Tuple> small;
        Map<Tuple, Tuple> big;
        Table joined;
        boolean isLeftSmall = getHashed().size() < other.getHashed().size();
        if (isLeftSmall) {
            small = getHashed();
            big = other.getHashed();
            joined = new Table(Joiner.on("_").join("Joined", tableName, other.tableName), joinedTableAlias);
            joined.columns = columns.combine(other.columns);
        } else {
            small = other.getHashed();
            big = getHashed();
            joined = new Table(Joiner.on("_").join("Joined", other.tableName, tableName), joinedTableAlias);
            joined.columns = other.columns.combine(columns);
        }
        small.forEach((key, smallTuple) -> {
            Tuple bigTuple = big.get(key);
            Tuple joinedTuple = Tuple.of(smallTuple, bigTuple);
            joined.addRawTuple(joinedTuple);
        });
        return joined;
    }

    public XlsResultSet getResultSet() {
        return new XlsResultSet(rawTuples);
    }

    public Table join(Table rhs, String joinedTableAlias, WhereExp predicate) {
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

    private void rehash(Function<Tuple, Tuple> keyExtractor) {
        if (!hashed.isEmpty()) {
            hashed.clear();
        }
        rawTuples.forEach(t -> hashed.put(keyExtractor.apply(t), t));
    }

    public void removeIf(Predicate<Tuple> predicate) {
        if (!hashed.isEmpty()) {
            hashed.values().removeIf(predicate);
        } else {
            rawTuples.removeIf(predicate);
        }
    }
}
