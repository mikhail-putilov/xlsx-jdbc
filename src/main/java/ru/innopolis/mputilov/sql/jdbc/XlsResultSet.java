package ru.innopolis.mputilov.sql.jdbc;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class XlsResultSet implements Iterable<List<String>> {
    private Iterable<List<String>> tuples;

    public XlsResultSet(Iterable<List<String>> tuples) {
        this.tuples = tuples;
    }

    @Override
    public Iterator<List<String>> iterator() {
        return tuples.iterator();
    }

    @Override
    public void forEach(Consumer<? super List<String>> action) {
        tuples.forEach(action);
    }

    @Override
    public Spliterator<List<String>> spliterator() {
        return tuples.spliterator();
    }
}
