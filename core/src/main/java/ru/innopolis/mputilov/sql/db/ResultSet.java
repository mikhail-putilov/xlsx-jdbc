package ru.innopolis.mputilov.sql.db;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSet implements Iterable<Tuple> {
    private Iterable<Tuple> tuples;

    public ResultSet(Iterable<Tuple> tuples) {
        this.tuples = tuples;
    }

    @Override
    public Iterator<Tuple> iterator() {
        return tuples.iterator();
    }

    @Override
    public void forEach(Consumer<? super Tuple> action) {
        tuples.forEach(action);
    }

    @Override
    public Spliterator<Tuple> spliterator() {
        return tuples.spliterator();
    }
}
