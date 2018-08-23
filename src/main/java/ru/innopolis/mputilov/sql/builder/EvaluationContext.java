package ru.innopolis.mputilov.sql.builder;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import ru.innopolis.mputilov.sql.db_impl.Table;

import java.util.Map;

@Getter
@Setter
public class EvaluationContext implements Context {
    private ContextState currentContextState;
    private Table currentProcessingTable;
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
    public void addProjectionColumn(ColumnExp columnExp) {
        projectionInfo.putIfAbsent(columnExp.getTableAlias(), new Columns());
        projectionInfo.get(columnExp.getTableAlias()).addDistinct(columnExp);
    }

    @Override
    public Columns getProjectedColumnsFor(String tableAlias) {
        return projectionInfo.get(tableAlias);
    }
}
