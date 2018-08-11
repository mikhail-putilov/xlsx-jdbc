package ru.innopolis.mputilov.sql.event;

import ru.innopolis.mputilov.sql.jdbc.XlsConnection;

public class ConnectionClosedEvent implements Event {
    private XlsConnection connection;

    public ConnectionClosedEvent(XlsConnection connection) {
        this.connection = connection;
    }

    public XlsConnection getConnection() {
        return connection;
    }
}
