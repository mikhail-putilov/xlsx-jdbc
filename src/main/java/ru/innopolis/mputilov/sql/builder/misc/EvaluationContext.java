package ru.innopolis.mputilov.sql.builder.misc;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.builder.ColumnExp;
import ru.innopolis.mputilov.sql.builder.ColumnsExp;
import ru.innopolis.mputilov.sql.db.Table;

import java.util.Map;

@Getter
@Setter
public class EvaluationContext {
    private Table currentProcessingTable;
    /**
     * table alias -> projected columns
     */
    private Map<String, ColumnsExp> tableAlias2ProjectionColumns = Maps.newLinkedHashMap();

    void addProjectionColumns(String tableAlias, ColumnsExp columns) {
        ColumnsExp exists = tableAlias2ProjectionColumns.get(tableAlias);
        if (exists != null) {
            exists.combineDistinct(columns);
        } else {
            tableAlias2ProjectionColumns.put(tableAlias, columns);
        }
    }

    void addProjectionColumn(ColumnExp column) {
        tableAlias2ProjectionColumns.putIfAbsent(column.getTableAlias(), new ColumnsExp());
        tableAlias2ProjectionColumns.get(column.getTableAlias()).addDistinct(column);
    }

    ColumnsExp getProjectedColumnsFor(String tableAlias) {
        return tableAlias2ProjectionColumns.get(tableAlias);
    }
}
