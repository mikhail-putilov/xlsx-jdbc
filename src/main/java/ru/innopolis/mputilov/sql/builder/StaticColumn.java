package ru.innopolis.mputilov.sql.builder;

public class StaticColumn implements Column, ValueAware {
    private Integer num;

    public StaticColumn(Integer i) {
        num = i;
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
    public Object getValue() {
        return num;
    }
}
