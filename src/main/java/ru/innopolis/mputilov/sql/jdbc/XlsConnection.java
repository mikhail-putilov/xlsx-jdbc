package ru.innopolis.mputilov.sql.jdbc;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.innopolis.mputilov.sql.event.ConnectionClosedEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Opens connection to xlsx database
 * Keeps it open and closes when its appropriate
 */
public class XlsConnection implements AutoCloseable {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XlsConnection that = (XlsConnection) o;
        return Objects.equals(filename.getPath(), that.filename.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename.getPath());
    }

    @Override
    @SneakyThrows(IOException.class)
    public void close() {
        eventBus.post(new ConnectionClosedEvent(this));
        workbook.close();
    }

    public XlsStatement createStatement() {
        return new XlsStatement(workbook, xlsPopulatorFactory);
    }
}
