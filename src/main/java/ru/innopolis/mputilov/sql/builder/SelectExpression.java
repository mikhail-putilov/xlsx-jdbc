package ru.innopolis.mputilov.sql.builder;

public class SelectExpression implements Expression<Columns> {

    private Columns columns;

    public SelectExpression(Column... column) {
        this.columns = new Columns(column);
    }

    @Override
    public Columns eval(Context ctx) {
        // hoist all information inside evaluation context
        columns.getAllAliases()
                .forEach(tableAlias -> ctx.addProjectionColumns(tableAlias, columns.getColumnsForTableWithAlias(tableAlias)));
        return columns;
    }
}
