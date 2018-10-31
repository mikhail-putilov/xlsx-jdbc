package ru.innopolis.mputilov.sql.builder;

public interface Expression<T> {
    T eval(EvaluationContext ctx);

    void accept(Visitor visitor);
}
