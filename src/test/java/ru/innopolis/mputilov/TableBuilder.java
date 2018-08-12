package ru.innopolis.mputilov;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

class TableBuilder implements Iterable<List<String>> {
    private List<List<String>> rows = new ArrayList<>();

    public static TableBuilder create() {
        return new TableBuilder();
    }

    @Override
    public String toString() {
        return rows.toString();
    }

    public TableBuilder row(String... cols) {
        rows.add(Lists.newArrayList(cols));
        return this;
    }

    @Override
    public Iterator<List<String>> iterator() {
        return rows.iterator();
    }

    @Override
    public Spliterator<List<String>> spliterator() {
        return rows.spliterator();
    }
}
