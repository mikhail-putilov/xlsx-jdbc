package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.collect.Lists;
import ru.innopolis.mputilov.sql.builder.ExpressionBuilder;

import java.util.List;
import java.util.stream.Collectors;

public class StatementModel {

    private ExpressionBuilder dataHolder;
    private DataBase db;

    public StatementModel(ExpressionBuilder statement, DataBase db) {
        this.dataHolder = statement;
        this.db = db;
    }

    public boolean isJoined() {
        return dataHolder.getTables().size() > 1 && !dataHolder.getJoinConditions().isEmpty();
    }

    public List<TableInfo> getTableInfosWithBackingTable() {
        ExpressionBuilder.TableAliasPair firstTable = dataHolder.getTables().get(0);
        ExpressionBuilder.TableAliasPair secondTable = dataHolder.getTables().get(1);
        return Lists.newArrayList(createTableInfoForAlias(firstTable), createTableInfoForAlias(secondTable));
    }

    private TableInfo createTableInfoForAlias(ExpressionBuilder.TableAliasPair tableNameOrAlias) {
        String alias = tableNameOrAlias.getAlias();

        List<ExpressionBuilder.SelectAliasPair> columnsFromSelect = dataHolder.getSelect().stream()
                .filter(s -> s.getAlias().equals(alias))
                .collect(Collectors.toList());

        List<ExpressionBuilder.JoinConditionAliasPair> columnsFromJoin = dataHolder.getJoinConditions().stream()
                .filter(s -> s.getAlias().equals(alias))
                .collect(Collectors.toList());

        return db.setBackingTable(new TableInfo(tableNameOrAlias, columnsFromSelect, columnsFromJoin));
    }
}
