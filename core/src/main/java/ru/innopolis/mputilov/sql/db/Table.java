package ru.innopolis.mputilov.sql.db;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(staticName = "createEmpty")
public class Table {
    private final TableHeader header;
    private TreeMap<Tuple, Tuple> hashed = new TreeMap<>();
    private List<Tuple> rawTuples = new ArrayList<>();

    public Table(TableAliasPair tableAliasPair) {
        this.header = TableHeader.createPartly(tableAliasPair.getTableName(), tableAliasPair.getAlias());
    }

    private boolean isRecurrent() {
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

    private Table probe(Table other, String joinedTableAlias) {
        if (hashed.isEmpty()) {
            throw new IllegalStateException("Cannot join raw tables");
        }
        Map<Tuple, Tuple> small;
        Map<Tuple, Tuple> big;
        TableHeader header;
        boolean isLeftSmall = getHashed().size() < other.getHashed().size();
        if (isLeftSmall) {
            small = getHashed();
            big = other.getHashed();
            header = TableHeader.createFully(this.joinTableNames(other), joinedTableAlias, this.joinColumns(other));
        } else {
            small = other.getHashed();
            big = getHashed();
            header = TableHeader.createFully(other.joinTableNames(this), joinedTableAlias, other.joinColumns(this));
        }
        Table joined = Table.createEmpty(header);
        small.forEach((key, smallTuple) -> {
            Tuple bigTuple = big.get(key);
            Tuple joinedTuple = Tuple.of(smallTuple, bigTuple);
            joined.addRawTuple(joinedTuple);
        });
        return joined;
    }

    private Columns joinColumns(Table other) {
        return header.getColumns().combine(other.getHeader().getColumns());
    }

    private String joinTableNames(Table other) {
        return Joiner.on("_").join("Joined", header.getTableName(), other.header.getTableName());
    }

    public ResultSet getResultSet() {
        return new ResultSet(rawTuples);
    }

    public Table join(Table rhs, String joinedTableAlias, Function<Tuple, Tuple> lhsKeyExtractor, Function<Tuple, Tuple> rhsKeyExtractor) {
        if (isRecurrent()) {
            throw new UnsupportedOperationException("cannot join reccurent table " + header.getTableName());
        }
        if (rhs.isRecurrent()) {
            throw new UnsupportedOperationException("cannot join reccurent table" + rhs.header.getTableName());
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
        return this.probe(rhs, joinedTableAlias);
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

    public Table reorder(List<ColumnAliasPair> desiredOrder) {
        return reorderAll(desiredOrder.stream()
                .map(header.getColumns()::getIndexOf) // get the position of column A in a given order argument
                .collect(Collectors.toList()));
    }

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
     * permutation matrix maps D'th indexes to indexes in A
     * So, permutation would be in that example:
     * <pre>
     *      0 1 2
     *      | | |
     *      v v v
     * P = [1 0 2]
     * </pre>
     */
    private Table reorderAll(List<Integer> permutationMatrix) {
        Table table = new Table(header);
        for (Tuple t : getRawTuples()) {
            table.addRawTuple(t.reorder(permutationMatrix));
        }
        return table;
    }
}
