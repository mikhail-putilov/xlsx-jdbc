package ru.innopolis.mputilov.sql.builder;

public class SelectExpression implements Expression<Columns> {

    private Columns columns;

    public SelectExpression(Column... column) {
        this.columns = new Columns(column);
    }

    public SelectExpression(Columns columns) {
        this.columns = columns;
    }

    @Override
    public Columns eval(Context ctx) {
        // hoist all information inside evaluation context
        columns.getAllAliases()
                .forEach(tableAlias -> ctx.addProjectionColumns(tableAlias, columns.getColumnsForTableWithAlias(tableAlias)));
        return columns;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSelectExpression(this);
    }
}
