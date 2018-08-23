package ru.innopolis.mputilov.sql.builder;

import static com.google.common.base.MoreObjects.firstNonNull;

public class StaticColumnExp implements ColumnExp, ValueAware {
    private Integer num;
    private String value;

    public StaticColumnExp(Integer i) {
        num = i;
    }

    public StaticColumnExp(String i) {
        value = i;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTableAlias() {
        return null;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public Object getStatic() {
        return firstNonNull(num, value);
    }

    @Override
    public Object getValue() {
        return firstNonNull(num, value);
    }

    @Override
    public Object eval(Context ctx) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {

    }
}
