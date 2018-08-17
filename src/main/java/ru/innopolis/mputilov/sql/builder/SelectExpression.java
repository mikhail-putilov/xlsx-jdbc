package ru.innopolis.mputilov.sql.builder;

import lombok.Getter;

@Getter
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
        return columns;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitSelectExpression(this);
    }
}
