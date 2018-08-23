package ru.innopolis.mputilov.sql.builder;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Columns {
    private List<ColumnExp> columnExps;

    public Columns() {
        columnExps = new ArrayList<>();
    }

    public Columns(List<ColumnExp> columnExps) {
        this.columnExps = columnExps;
    }

    public Columns(ColumnExp... columnExps) {
        this.columnExps = Arrays.asList(columnExps);
    }

    public boolean isEmpty() {
        return columnExps.isEmpty();
    }

    public boolean contains(Object o) {
        return columnExps.contains(o);
    }

    public Iterator<ColumnExp> iterator() {
        return columnExps.iterator();
    }

    public Object[] toArray() {
        return columnExps.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return columnExps.toArray(a);
    }

    public boolean remove(Object o) {
        return columnExps.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return columnExps.containsAll(c);
    }

    public boolean addAll(Collection<? extends ColumnExp> c) {
        return columnExps.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends ColumnExp> c) {
        return columnExps.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return columnExps.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return columnExps.retainAll(c);
    }

    public void replaceAll(UnaryOperator<ColumnExp> operator) {
        columnExps.replaceAll(operator);
    }

    public void sort(Comparator<? super ColumnExp> c) {
        columnExps.sort(c);
    }

    public void clear() {
        columnExps.clear();
    }

    public ColumnExp get(int index) {
        return columnExps.get(index);
    }

    public ColumnExp set(int index, ColumnExp element) {
        return columnExps.set(index, element);
    }

    public void add(int index, ColumnExp element) {
        columnExps.add(index, element);
    }

    public ColumnExp remove(int index) {
        return columnExps.remove(index);
    }

    public int indexOf(Object o) {
        return columnExps.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return columnExps.lastIndexOf(o);
    }

    public ListIterator<ColumnExp> listIterator() {
        return columnExps.listIterator();
    }

    public ListIterator<ColumnExp> listIterator(int index) {
        return columnExps.listIterator(index);
    }

    public List<ColumnExp> subList(int fromIndex, int toIndex) {
        return columnExps.subList(fromIndex, toIndex);
    }

    public Spliterator<ColumnExp> spliterator() {
        return columnExps.spliterator();
    }

    public boolean removeIf(Predicate<? super ColumnExp> filter) {
        return columnExps.removeIf(filter);
    }

    public Stream<ColumnExp> parallelStream() {
        return columnExps.parallelStream();
    }

    public void forEach(Consumer<? super ColumnExp> action) {
        columnExps.forEach(action);
    }

    public Columns combine(Columns cols) {
        List<ColumnExp> copy = new ArrayList<>(this.columnExps);
        copy.addAll(cols.columnExps);
        return new Columns(copy);
    }

    public Columns combineDistinct(Columns cols) {
        Set<ColumnExp> copy = new HashSet<>(this.columnExps);
        copy.addAll(cols.columnExps);
        return new Columns(new ArrayList<>(copy));
    }

    public void add(ColumnExp c) {
        columnExps.add(c);
    }

    public void addDistinct(ColumnExp c) {
        if (columnExps.contains(c)) {
            return;
        }
        columnExps.add(c);
    }

    public Columns getColumnsForTableWithAlias(String alias) {
        return columnExps.stream()
                .filter(c -> c.getTableAlias().equals(alias))
                .collect(Collector.of(Columns::new, Columns::add, Columns::combine));
    }

    public List<String> getAllAliases() {
        return columnExps.stream().filter(Objects::nonNull).map(ColumnExp::getTableAlias).collect(Collectors.toList());
    }

    public boolean containsTableName(String tableName) {
        return columnExps.stream().anyMatch(c -> c.getName().equals(tableName));
    }

    public int size() {
        return columnExps.size();
    }

    public Stream<ColumnExp> stream() {
        return columnExps.stream();
    }

    public int getIndexOf(ColumnExp columnExp) {
        return columnExps.indexOf(columnExp);
    }
}
