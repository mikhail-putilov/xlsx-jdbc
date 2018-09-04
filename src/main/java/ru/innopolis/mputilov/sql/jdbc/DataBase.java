package ru.innopolis.mputilov.sql.jdbc;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.innopolis.mputilov.sql.db.Table;
import ru.innopolis.mputilov.sql.db.vo.TableAliasPair;
import ru.innopolis.mputilov.sql.event.ConnectionClosedEvent;
import ru.innopolis.mputilov.sql.jdbc.api.TableFactory;
import ru.innopolis.mputilov.sql.jdbc.api.XlsConnectionFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataBase implements TableFactory {
    private final XlsConnectionFactory xlsConnectionFactory;
    private final List<XlsConnection> openedConnections = new ArrayList<>();
    private final Map<TableAliasPair, Table> tableCache = new LinkedHashMap<>();

    @Inject
    DataBase(EventBus eventBus, XlsConnectionFactory xlsConnectionFactory) {
        this.xlsConnectionFactory = xlsConnectionFactory;
        eventBus.register(this);
    }

    @SneakyThrows(IOException.class)
    public XlsConnection createXlsConnection(URL filename) {
        Workbook workbook = new XSSFWorkbook(filename.openStream());
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
     */
    @Override
    public Table getOrCreateTable(TableAliasPair id) {
        return tableCache.computeIfAbsent(id, _id -> new Table(id));
    }
}
