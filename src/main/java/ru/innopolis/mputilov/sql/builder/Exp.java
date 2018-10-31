package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.builder.misc.EvaluationContext;
import ru.innopolis.mputilov.sql.builder.misc.Visitor;

public interface Exp<T> {
    T eval(EvaluationContext ctx);

    void accept(Visitor visitor);
}
