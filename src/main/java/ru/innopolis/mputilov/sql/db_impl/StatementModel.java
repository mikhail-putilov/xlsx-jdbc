package ru.innopolis.mputilov.sql.db_impl;

import ru.innopolis.mputilov.sql.builder.StatementBuilder;

public class StatementModel {

    private StatementBuilder dataHolder;

    public StatementModel(StatementBuilder statement) {
        this.dataHolder = statement;
    }

    public boolean isJoined() {
        return dataHolder.getJoin() != null;
    }

    public String getFromTableName() {
        return dataHolder.getFrom();
    }

    public String getLeftTableName() {
        return dataHolder.getFrom();
    }

    public String getRightTableName() {
        return dataHolder.getJoin();
    }
}
