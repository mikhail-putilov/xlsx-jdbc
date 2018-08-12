package ru.innopolis.mputilov.sql.db_impl;

import lombok.AccessLevel;
import lombok.Getter;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Table {
    @Getter(AccessLevel.PACKAGE)
    private Map<PrimaryKey, List<String>> data = new TreeMap<>();
    private List<List<String>> rawTuples = new ArrayList<>();

    void addRawTuple(List<String> rawTuple) {
        rawTuples.add(rawTuple);
    }

    public Table join(Table other) {
        Map<PrimaryKey, List<String>> small;
        Map<PrimaryKey, List<String>> big;
        Table joined = new Table();
        boolean isLeftSmall = true;
        if (getData().size() < other.getData().size()) {
            small = getData();
            big = other.getData();
        } else {
            isLeftSmall = false;
            small = other.getData();
            big = getData();
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
}
