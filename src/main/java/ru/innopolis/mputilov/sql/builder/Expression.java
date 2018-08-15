package ru.innopolis.mputilov.sql.builder;

public interface Expression<T> {
    T eval(Context ctx);

    void accept(Visitor visitor);
}
