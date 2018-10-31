package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db.Columns;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColumnsExp {
    private List<ColumnExp> columnExpressions;

    public ColumnsExp(ColumnExp... columnExpressions) {
        this.columnExpressions = new ArrayList<>();
        Collections.addAll(this.columnExpressions, columnExpressions);
    }

    private ColumnsExp(List<ColumnExp> columnExpressions) {
        this.columnExpressions = columnExpressions;
    }

    public void addDistinct(ColumnExp tail) {
        if (columnExpressions.contains(tail)) {
            return;
        }
        columnExpressions.add(tail);
    }

    public List<String> getAllAliases() {
        return columnExpressions.stream()
                .filter(Objects::nonNull)
                .map(ColumnExp::getTableAlias)
                .collect(Collectors.toList());
    }

    public ColumnsExp combineDistinct(ColumnsExp cols) {
        Set<ColumnExp> copy = new LinkedHashSet<>(this.columnExpressions);
        copy.addAll(cols.columnExpressions);
        return new ColumnsExp(copy.toArray(new ColumnExp[0]));
    }

    private ColumnsExp combine(ColumnsExp cols) {
        List<ColumnExp> copy = new ArrayList<>(this.columnExpressions);
        copy.addAll(cols.columnExpressions);
        return new ColumnsExp(copy);
    }

    private void add(ColumnExp c) {
        this.columnExpressions.add(c);
    }

    public ColumnsExp getColumnsForTableWithAlias(String tableAlias) {
        return this.columnExpressions.stream()
                .filter(c -> c.getTableAlias().equals(tableAlias))
                .collect(Collector.of(ColumnsExp::new, ColumnsExp::add, ColumnsExp::combine));
    }

    public int size() {
        return this.columnExpressions.size();
    }

    public Stream<ColumnExp> stream() {
        return this.columnExpressions.stream();
    }

    public Columns toColumns() {
        return new Columns(columnExpressions.stream().map(ColumnExp::toColumnAliasPair).collect(Collectors.toList()));
    }

    public boolean containsColumnName(String columnName) {
        return columnExpressions.stream()
                .map(ColumnExp::getColumnName)
                .anyMatch(Predicate.isEqual(columnName));
    }
}
