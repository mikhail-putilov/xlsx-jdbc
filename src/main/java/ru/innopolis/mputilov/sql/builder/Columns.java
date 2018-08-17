package ru.innopolis.mputilov.sql.builder;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Columns {
    private List<Column> columns;

    public Columns() {
        columns = new ArrayList<>();
    }

    public Columns(List<Column> columns) {
        this.columns = columns;
    }

    public Columns(Column... columns) {
        this.columns = Arrays.asList(columns);
    }

    public Columns combine(Columns cols) {
        List<Column> copy = new ArrayList<>(this.columns);
        copy.addAll(cols.columns);
        return new Columns(copy);
    }

    public Columns combineDistinct(Columns cols) {
        Set<Column> copy = new HashSet<>(this.columns);
        copy.addAll(cols.columns);
        return new Columns(new ArrayList<>(copy));
    }

    public void add(Column c) {
        columns.add(c);
    }

    public void addDistinct(Column c) {
        if (columns.contains(c)) {
            return;
        }
        columns.add(c);
    }

    public Columns getColumnsForTableWithAlias(String alias) {
        return columns.stream()
                .filter(c -> c.getTableAlias().equals(alias))
                .collect(Collector.of(Columns::new, Columns::add, Columns::combine));
    }

    public List<String> getAllAliases() {
        return columns.stream().filter(Objects::nonNull).map(Column::getTableAlias).collect(Collectors.toList());
    }

    public boolean containsTableName(String tableName) {
        return columns.stream().anyMatch(c -> c.getName().equals(tableName));
    }

    public int size() {
        return columns.size();
    }

    public Stream<Column> stream() {
        return columns.stream();
    }

    public int getIndexOf(Column column) {
        return columns.indexOf(column);
    }
}
