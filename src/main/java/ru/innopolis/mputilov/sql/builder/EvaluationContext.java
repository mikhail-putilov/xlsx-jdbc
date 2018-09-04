package ru.innopolis.mputilov.sql.builder;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db.Table;

import java.util.Map;

@Getter
@Setter
public class EvaluationContext implements Context {
    private ContextState currentContextState;
    private Table currentProcessingTable;
    /**
     * table alias -> projected columns
     */
    private Map<String, Columns> tableAlias2ProjectionColumns = Maps.newLinkedHashMap();

    @Override
    public void addProjectionColumns(String tableAlias, Columns columns) {
        Columns exists = tableAlias2ProjectionColumns.get(tableAlias);
        if (exists != null) {
            exists.combineDistinct(columns);
        } else {
            tableAlias2ProjectionColumns.put(tableAlias, columns);
        }
    }

    @Override
    public void addProjectionColumn(ColumnExp column) {
        tableAlias2ProjectionColumns.putIfAbsent(column.getTableAlias(), new Columns());
        tableAlias2ProjectionColumns.get(column.getTableAlias()).addDistinct(column);
    }

    @Override
    public Columns getProjectedColumnsFor(String tableAlias) {
        return tableAlias2ProjectionColumns.get(tableAlias);
    }
}
