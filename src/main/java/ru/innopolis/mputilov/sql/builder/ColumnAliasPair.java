package ru.innopolis.mputilov.sql.builder;

import lombok.Data;

@Data
public final class ColumnAliasPair implements ColumnExp {
    final String tableAlias;
    final String name;

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public Object getStatic() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object eval(Context ctx) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
