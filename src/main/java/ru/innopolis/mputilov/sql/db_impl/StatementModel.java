package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.collect.Lists;
import ru.innopolis.mputilov.sql.builder.StatementBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class StatementModel {

    private StatementBuilder dataHolder;
    private DataBase db;

    public StatementModel(StatementBuilder statement, DataBase db) {
        this.dataHolder = statement;
        this.db = db;
    }

    public boolean isJoined() {
        return dataHolder.getJoin() != null;
    }

    public String getFromTableName() {
        return dataHolder.getFrom().getTableName();
    }

    public List<StatementBuilder.SelectAliasPair> getColumns() {
        return dataHolder.getSelect();
    }

    public List<TableInfo> getTableInfosWithBackingTable() {
        StatementBuilder.TableAliasPair firstTable = dataHolder.getFrom();
        StatementBuilder.TableAliasPair secondTable = dataHolder.getJoin();
        return Lists.newArrayList(createTableInfoForAlias(firstTable), createTableInfoForAlias(secondTable));
    }

    private TableInfo createTableInfoForAlias(StatementBuilder.TableAliasPair tableNameOrAlias) {
        String alias = tableNameOrAlias.getAlias();

        List<StatementBuilder.SelectAliasPair> columnsFromSelect = dataHolder.getSelect().stream()
                .filter(s -> s.getAlias().equals(alias))
                .collect(Collectors.toList());

        List<StatementBuilder.EqAliasPair> columnsFromJoinClause = dataHolder.getJoinEq().stream()
                .filter(s -> s.getAlias().equals(alias))
                .collect(Collectors.toList());

        List<StatementBuilder.OnAliasPair> columnsFromJoinClause2 = dataHolder.getJoinOn().stream()
                .filter(s -> s.getAlias().equals(alias))
                .collect(Collectors.toList());

        return db.setBackingTable(new TableInfo(tableNameOrAlias, columnsFromSelect, columnsFromJoinClause, columnsFromJoinClause2));
    }
}
