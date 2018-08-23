package ru.innopolis.mputilov.sql.builder;

import java.util.Objects;
import java.util.function.BiFunction;

public enum Operation implements BiFunction<Object, Object, Object> {
    EQ(Objects::equals),
    NOT_EQ((o1, o2) -> !Objects.equals(o1, o2));

    private BiFunction<Object, Object, Boolean> delegate;

    Operation(BiFunction<Object, Object, Boolean> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Boolean apply(Object o1, Object o2) {
        return delegate.apply(o1, o2);
    }
}
