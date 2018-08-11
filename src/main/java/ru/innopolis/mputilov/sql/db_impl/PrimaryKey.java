package ru.innopolis.mputilov.sql.db_impl;

import java.io.Serializable;
import java.util.Objects;

public class PrimaryKey<T extends Serializable> {
    private final T key;

    public PrimaryKey(T key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimaryKey<?> that = (PrimaryKey<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public T getKey() {
        return key;
    }
}
