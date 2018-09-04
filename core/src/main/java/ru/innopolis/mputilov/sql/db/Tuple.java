package ru.innopolis.mputilov.sql.db;

import com.google.common.collect.ImmutableList;
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

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Tuple o) {
        for (int i = 0; i < tuple.size(); i++) {
            //noinspection unchecked
            int result = ((Comparable<Object>) tuple.get(i)).compareTo(o.getTuple().get(i));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    public int size() {
        return tuple.size();
    }

    public Object get(int i) {
        return tuple.get(i);
    }
}
