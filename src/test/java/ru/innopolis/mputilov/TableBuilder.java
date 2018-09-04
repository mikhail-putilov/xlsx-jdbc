package ru.innopolis.mputilov;

import ru.innopolis.mputilov.sql.db.Tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

import static java.util.Arrays.asList;

class TableBuilder implements Iterable<Tuple> {
    private List<Tuple> rows = new ArrayList<>();

    static TableBuilder table() {
        return new TableBuilder();
    }

    @Override
    public String toString() {
        return rows.toString();
    }

    TableBuilder row(String... cols) {
        rows.add(new Tuple(asList((Object[]) cols)));
        return this;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Tuple> iterator() {
        return rows.iterator();
    }

    @Override
    public Spliterator<Tuple> spliterator() {
        return rows.spliterator();
    }
}
