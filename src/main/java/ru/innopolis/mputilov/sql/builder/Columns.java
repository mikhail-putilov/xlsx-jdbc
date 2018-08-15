package ru.innopolis.mputilov.sql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public void add(Column c) {
        columns.add(c);
    }

    public Columns getColumnsForTableWithAlias(String alias) {
        return columns.stream()
                .filter(c -> c.getTableAlias().equals(alias))
                .collect(Collector.of(Columns::new, Columns::add, Columns::combine));
    }

    public List<String> getAllAliases() {
        return columns.stream().map(Column::getTableAlias).collect(Collectors.toList());
    }
}
