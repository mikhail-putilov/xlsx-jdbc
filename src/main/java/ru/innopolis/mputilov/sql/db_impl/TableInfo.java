package ru.innopolis.mputilov.sql.db_impl;


import lombok.AccessLevel;
import lombok.Getter;
import ru.innopolis.mputilov.sql.builder.StatementBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class TableInfo {
    private static final String FORGOTTEN_MAP_ERROR_MESSAGE = "The map 'indexesOfKeyColumns' must be set before trying to put raw tuples from xls";

    @Getter
    private final StatementBuilder.TableAliasPair tableNameOrAlias;
    @Getter(AccessLevel.PACKAGE)
    private final List<StatementBuilder.SelectAliasPair> columnsFromSelect;
    @Getter(AccessLevel.PACKAGE)
    private final List<StatementBuilder.EqAliasPair> columnsFromJoinClause;
    @Getter(AccessLevel.PACKAGE)
    private final List<StatementBuilder.OnAliasPair> columnsFromJoinClause2;
    private Table backingTable;
    private List<Integer> indexesOfKeyColumns;

    public TableInfo(StatementBuilder.TableAliasPair tableNameOrAlias, List<StatementBuilder.SelectAliasPair> columnsFromSelect, List<StatementBuilder.EqAliasPair> columnsFromJoinClause, List<StatementBuilder.OnAliasPair> columnsFromJoinClause2) {
        this.tableNameOrAlias = tableNameOrAlias;
        this.columnsFromSelect = columnsFromSelect;
        this.columnsFromJoinClause = columnsFromJoinClause;
        this.columnsFromJoinClause2 = columnsFromJoinClause2;
    }

    public Set<String> getInterestedColumns() {
        Set<String> names = new LinkedHashSet<>(columnsFromJoinClause.size() + columnsFromJoinClause.size() + columnsFromJoinClause2.size());
        columnsFromJoinClause.forEach(p -> names.add(p.getEq()));
        columnsFromJoinClause2.forEach(p -> names.add(p.getOn()));
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

    public void setBackingTable(Table table) {
        this.backingTable = table;
    }

    public TableInfo join(TableInfo rightTable) {
        Table joined = this.backingTable.join(rightTable.getBackingTable());

        String joinedTableName = new StringJoiner("_")
                .add("Joined")
                .add(getTableNameOrAlias().toString())
                .add(rightTable.getTableNameOrAlias().toString())
                .toString();
        StatementBuilder.TableAliasPair joinedId = new StatementBuilder.TableAliasPair(null, joinedTableName);

        List<StatementBuilder.SelectAliasPair> joinedColumnsFromSelect = new ArrayList<>(columnsFromSelect);
        joinedColumnsFromSelect.addAll(rightTable.getColumnsFromSelect());

        ArrayList<StatementBuilder.EqAliasPair> joinedColumnsFromJoin = new ArrayList<>(columnsFromJoinClause);
        joinedColumnsFromJoin.addAll(rightTable.getColumnsFromJoinClause());

        ArrayList<StatementBuilder.OnAliasPair> joinedColumnsFromJoin2 = new ArrayList<>(columnsFromJoinClause2);
        joinedColumnsFromJoin2.addAll(rightTable.getColumnsFromJoinClause2());

        TableInfo tableInfo = new TableInfo(joinedId, joinedColumnsFromSelect, joinedColumnsFromJoin, joinedColumnsFromJoin2);
        tableInfo.setBackingTable(joined);
        return tableInfo;
    }

    public void setNameToIndexMap(Map<String, Integer> nameToIndexInXlsx) {
        Objects.requireNonNull(nameToIndexInXlsx, "indexesOfKeyColumns map cannot be constructed when given argument is null");

        indexesOfKeyColumns = columnsFromJoinClause.stream()
                .map(StatementBuilder.EqAliasPair::getEq)
                .map(nameToIndexInXlsx::get)
                .collect(Collectors.toList());
        List<Integer> joinOn = columnsFromJoinClause2.stream()
                .map(StatementBuilder.OnAliasPair::getOn)
                .map(nameToIndexInXlsx::get)
                .collect(Collectors.toList());
        indexesOfKeyColumns.addAll(joinOn);

        if (indexesOfKeyColumns.stream().anyMatch(Objects::isNull)) {
            throw new IllegalStateException("column indexes for a key are not found in xls");
        }
    }
}
