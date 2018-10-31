package ru.innopolis.mputilov.sql.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class Tuple implements Comparable<Tuple> {
    private final List<Object> tuple;

    public Tuple(List<Object> tuple) {
        this.tuple = tuple;
    }

    private Tuple(Tuple left, Tuple right) {
        this.tuple = new ArrayList<>(left.getTuple().size() + right.getTuple().size());
        this.tuple.addAll(left.getTuple());
        this.tuple.addAll(right.getTuple());
    }

    public static Tuple of(List<Object> multipleObj) {
        return new Tuple(multipleObj);
    }

    public static Tuple of(Object oneObj) {
        return new Tuple(ImmutableList.of(oneObj));
    }

    static Tuple of(Tuple left, Tuple right) {
        return new Tuple(left, right);
    }

    public int size() {
        return tuple.size();
    }

    public Object get(int i) {
        return tuple.get(i);
    }

    Tuple reorder(List<Integer> permutation) {
        List<Object> orderedData = new ArrayList<>(tuple.size());
        for (Integer indexInThisTuple : permutation) {
            orderedData.add(tuple.get(indexInThisTuple));
        }
        return new Tuple(orderedData);
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") Tuple other) {
        //noinspection unchecked
        CmpSaver cmpSaver = new CmpSaver();
        // results of anyMatch we don't need to use
        //noinspection ResultOfMethodCallIgnored,unchecked
        Streams.zip(this.tuple.stream().map(data -> (Comparable<Object>) data),
                other.tuple.stream(),
                Comparable::compareTo)
                .anyMatch(cmpSaver::isNotNull);
        return cmpSaver.lastCmp;
    }

    private static class CmpSaver {
        Integer lastCmp;

        boolean isNotNull(Integer cmp) {
            lastCmp = cmp;
            return cmp != 0;
        }
    }
}
