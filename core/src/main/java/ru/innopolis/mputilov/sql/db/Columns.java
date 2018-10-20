package ru.innopolis.mputilov.sql.db;

import java.util.*;

public class Columns {
    private final List<ColumnAliasPair> columns;

    public Columns(List<ColumnAliasPair> columns) {
        this.columns = columns;
    }

    Columns combine(Columns cols) {
        List<ColumnAliasPair> copy = new ArrayList<>(this.columns);
        copy.addAll(cols.columns);
        return new Columns(copy);
    }

    public int getIndexOf(ColumnAliasPair columnExp) {
        return columns.indexOf(columnExp);
    }
}
