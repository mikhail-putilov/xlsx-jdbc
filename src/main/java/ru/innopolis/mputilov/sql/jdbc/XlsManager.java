package ru.innopolis.mputilov.sql.jdbc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.innopolis.mputilov.sql.DbModule;
import ru.innopolis.mputilov.sql.db_impl.DataBase;

import java.net.URL;

/**
 * Serves an entry point for xlsx-jdbc
 */
public class XlsManager {
    public static XlsConnection getConnection(URL xls) {
        Injector injector = Guice.createInjector(new DbModule());
        DataBase db = injector.getInstance(DataBase.class);

        return db.createXlsConnection(xls);
    }
}
