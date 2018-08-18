package ru.innopolis.mputilov.sql.db_impl;


import lombok.AccessLevel;
import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.ExpressionBuilder;
import ru.innopolis.mputilov.sql.builder.TableAliasPair;

import java.util.*;
import java.util.stream.Collectors;

public class TableInfo {
    private static final String FORGOTTEN_MAP_ERROR_MESSAGE = "The map 'indexesOfKeyColumns' must be set before trying to put raw tuples from xls";

    @Getter
    private final TableAliasPair tableNameOrAlias;
    @Getter(AccessLevel.PACKAGE)
    private final List<ExpressionBuilder.SelectAliasPair> columnsFromSelect;
    @Getter(AccessLevel.PACKAGE)
    private final List<ExpressionBuilder.JoinConditionAliasPair> columnsFromJoin;
    private Table backingTable;
    private List<Integer> indexesOfKeyColumns = new ArrayList<>();

    TableInfo(TableAliasPair tableNameOrAlias,
                     List<ExpressionBuilder.SelectAliasPair> columnsFromSelect,
                     List<ExpressionBuilder.JoinConditionAliasPair> columnsFromJoin) {
        this.tableNameOrAlias = tableNameOrAlias;
        this.columnsFromSelect = columnsFromSelect;
        this.columnsFromJoin = columnsFromJoin;
    }

    public Set<String> getInterestedColumns() {
        Set<String> names = new LinkedHashSet<>(columnsFromJoin.size());
        columnsFromJoin.forEach(p -> names.add(p.getColumn()));
        columnsFromSelect.forEach(p -> names.add(p.getSelect()));

        return names;
    }

    public String getSheetName() {
        return tableNameOrAlias.getTableName();
    }

    /**
     * Grabs a key out of a raw tuple and puts it into backing table
     *
     * @param tuple tuple from xlsx
     */
    public void putTupleFromXls(List<String> tuple) {
        List<String> key = Objects.requireNonNull(indexesOfKeyColumns, FORGOTTEN_MAP_ERROR_MESSAGE).stream()
                .map(tuple::get)
                .collect(Collectors.toList());
        this.backingTable.put(new CompoundStringPrimaryKey(key), tuple);
    }

    public Table getBackingTable() {
        return this.backingTable;
    }

    void setBackingTable(Table table) {
        this.backingTable = table;
    }

    public TableInfo join(TableInfo rightTable) {
//        Table joined = this.backingTable.join(rightTable.getBackingTable());
//
//        String joinedTableName = new StringJoiner("_")
//                .add("Joined")
//                .add(getTableNameOrAlias().toString())
//                .add(rightTable.getTableNameOrAlias().toString())
//                .toString();
//        TableAliasPair joinedId = new TableAliasPair(null, joinedTableName);
//
//        List<ExpressionBuilder.SelectAliasPair> joinedColumnsFromSelect = new ArrayList<>(columnsFromSelect);
//        joinedColumnsFromSelect.addAll(rightTable.getColumnsFromSelect());
//
//        ArrayList<ExpressionBuilder.JoinConditionAliasPair> joinedColumnsFromJoin2 = new ArrayList<>(columnsFromJoin);
//        joinedColumnsFromJoin2.addAll(rightTable.getColumnsFromJoin());
//
//        TableInfo tableInfo = new TableInfo(joinedId, joinedColumnsFromSelect, joinedColumnsFromJoin2);
//        tableInfo.setBackingTable(joined);
//        return tableInfo;
        return null;
    }

    public void setNameToIndexMap(Map<String, Integer> nameToIndexInXlsx) {
        Objects.requireNonNull(nameToIndexInXlsx, "indexesOfKeyColumns map cannot be constructed when given argument is null");

        columnsFromJoin.stream()
                .map(ExpressionBuilder.JoinConditionAliasPair::getColumn)
                .map(nameToIndexInXlsx::get)
                .collect(Collectors.toCollection(() -> indexesOfKeyColumns));

        if (indexesOfKeyColumns.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("column indexes for a key are not found in xls");
        }
    }
}
