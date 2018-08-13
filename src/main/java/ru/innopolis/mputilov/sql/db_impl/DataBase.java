package ru.innopolis.mputilov.sql.db_impl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.innopolis.mputilov.sql.builder.StatementBuilder;
import ru.innopolis.mputilov.sql.event.ConnectionClosedEvent;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;
import ru.innopolis.mputilov.sql.jdbc.XlsConnectionFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataBase {
    private final XlsConnectionFactory xlsConnectionFactory;
    private final List<XlsConnection> openedConnections = new ArrayList<>();
    private final Map<StatementBuilder.TableAliasPair, Table> tableCache = new LinkedHashMap<>();

    @Inject
    DataBase(EventBus eventBus, XlsConnectionFactory xlsConnectionFactory) {
        this.xlsConnectionFactory = xlsConnectionFactory;
        eventBus.register(this);
    }

    @SneakyThrows(IOException.class)
    public XlsConnection createXlsConnection(URL filename) {
        XSSFWorkbook workbook = new XSSFWorkbook(filename.openStream());
        XlsConnection xlsConnection = xlsConnectionFactory.create(filename, workbook);
        openedConnections.add(xlsConnection);
        return xlsConnection;
    }

    @Subscribe
    public void closedConnection(ConnectionClosedEvent event) {
        openedConnections.remove(event.getConnection());
        if (openedConnections.isEmpty()) {
            tableCache.clear();
        }
    }

    /**
     * Retrieves existing table from cache or creates new one. Sets it to given tableInfo
     *
     * @param tableInfo id of desirable table
     * @return tableInfo with set backing table
     */
    public TableInfo setBackingTable(TableInfo tableInfo) {
        StatementBuilder.TableAliasPair id = tableInfo.getTableNameOrAlias();
        Table table = tableCache.get(id);
        if (table == null) {
            table = new Table();
            tableCache.put(id, table);
        }
        tableInfo.setBackingTable(table);
        return tableInfo;
    }
}
