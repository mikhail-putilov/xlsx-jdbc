package ru.innopolis.mputilov.sql.builder;

import com.google.common.collect.Maps;

import java.util.Map;

public class EvaluationContext implements Context {
    /**
     * id of expression -> projected columns
     */
    private Map<String, Columns> projectionInfo = Maps.newLinkedHashMap();

    @Override
    public void addProjectionColumns(String tableAlias, Columns columns) {
        Columns exists = projectionInfo.get(tableAlias);
        if (exists != null) {
            exists.combineDistinct(columns);
        } else {
            projectionInfo.put(tableAlias, columns);
        }
    }

    @Override
    public void addProjectionColumn(Column column) {
        projectionInfo.putIfAbsent(column.getTableAlias(), new Columns());
        projectionInfo.get(column.getTableAlias()).addDistinct(column);
    }

    @Override
    public Columns getProjectedColumnsFor(String tableAlias) {
        return projectionInfo.get(tableAlias);
    }
}
