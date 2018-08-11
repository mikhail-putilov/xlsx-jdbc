package ru.innopolis.mputilov.sql.db_impl;

import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Table<T extends Serializable> implements Serializable {
    private Map<PrimaryKey, T> data = new TreeMap<>();


    public Table join(Table table) {
        return this;
    }

    public Table doTheRest(StatementModel statement) {
        return this;
    }

    public XlsResultSet getResultSet() {
        return new XlsResultSet();
    }
}
