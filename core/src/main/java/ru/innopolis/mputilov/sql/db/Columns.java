package ru.innopolis.mputilov.sql.db;

import ru.innopolis.mputilov.sql.db.vo.ColumnAliasPair;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Columns {
    private final List<ColumnAliasPair> columns;

    public Columns() {
        columns = new ArrayList<>();
    }

    private Columns(List<ColumnAliasPair> columns) {
        this.columns = columns;
    }

    public Columns combine(Columns cols) {
        List<ColumnAliasPair> copy = new ArrayList<>(this.columns);
        copy.addAll(cols.columns);
        return new Columns(copy);
    }

    public Columns combineDistinct(Columns cols) {
        Set<ColumnAliasPair> copy = new HashSet<>(this.columns);
        copy.addAll(cols.columns);
        return new Columns(new ArrayList<>(copy));
    }

    private void add(ColumnAliasPair c) {
        columns.add(c);
    }

    public void addDistinct(ColumnAliasPair c) {
        if (columns.contains(c)) {
            return;
        }
        columns.add(c);
    }

    public Columns getColumnsForTableWithAlias(String tableAlias) {
        return columns.stream()
                .filter(c -> c.getTableAlias().equals(tableAlias))
                .collect(Collector.of(Columns::new, Columns::add, Columns::combine));
    }

    public List<String> getAllAliases() {
        return columns.stream().filter(Objects::nonNull).map(ColumnAliasPair::getTableAlias).collect(Collectors.toList());
    }

    public boolean containsTableAlias(String tableAlias) {
        return columns.stream().anyMatch(c -> c.getTableAlias().equals(tableAlias));
    }

    public int size() {
        return columns.size();
    }

    public Stream<ColumnAliasPair> stream() {
        return columns.stream();
    }

    public int getIndexOf(ColumnAliasPair columnExp) {
        return columns.indexOf(columnExp);
    }
}
