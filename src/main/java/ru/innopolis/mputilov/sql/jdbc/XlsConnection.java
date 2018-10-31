package ru.innopolis.mputilov.sql.jdbc;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import ru.innopolis.mputilov.sql.event.ConnectionClosedEvent;
import ru.innopolis.mputilov.sql.jdbc.api.XlsStatementFactory;
import ru.innopolis.mputilov.sql.jdbc.api.XlsPopulatorFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Represents a connection to a xls database
 * Warning: autocloseable
 */
@EqualsAndHashCode(of = {"filename"})
public class XlsConnection implements AutoCloseable, XlsStatementFactory {
    private final URL filename;
    private final Workbook workbook;
    private final EventBus eventBus;
    private final XlsPopulatorFactory xlsPopulatorFactory;

    @Inject
    public XlsConnection(@Assisted URL filename,
                         @Assisted Workbook workbook,
                         EventBus eventBus,
                         XlsPopulatorFactory xlsPopulatorFactory) {
        this.filename = filename;
        this.workbook = workbook;
        this.eventBus = eventBus;
        this.xlsPopulatorFactory = xlsPopulatorFactory;
    }

    @Override
    @SneakyThrows(IOException.class)
    public void close() {
        eventBus.post(new ConnectionClosedEvent(this));
        workbook.close();
    }

    @Override
    public XlsStatement createStatement() {
        return new XlsStatement(workbook, xlsPopulatorFactory);
    }
}
