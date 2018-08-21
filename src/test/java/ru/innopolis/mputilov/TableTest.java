package ru.innopolis.mputilov;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;
import ru.innopolis.mputilov.sql.jdbc.XlsManager;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static ru.innopolis.mputilov.TableMatchers.matchesTable;

public class TableTest {
    @Test
    public void trivial2() {
        URL url = Resources.getResource("test.xlsx");
        XlsConnection conn = XlsManager.getConnection(url);
        XlsResultSet set = conn.createStatement().executeQuery(new SqlExpression(
                new SelectExpression(
                        new ColumnAliasPair("s1", "col1"),
                        new ColumnAliasPair("s1", "col2"),
                        new ColumnAliasPair("s1", "col3"),
                        new ColumnAliasPair("s2", "col5"),
                        new ColumnAliasPair("s2", "col6")),
                new JoinEqExpression(
                        new TableExpression(new TableAliasPair("s1", "Sheet1")),
                        new TableExpression(new TableAliasPair("s2", "Sheet2")),
                        "j",
                        new PredicateExpression(
                                new ColumnAliasPair("s1", "col1"),
                                new ColumnAliasPair("s2", "col5"))),
                new PredicateExpression(
                        new ColumnAliasPair("s1", "col1"),
                        new StaticColumn(5))));
        conn.close();
        assertThat(set, matchesTable(TableBuilder.create()
                .row("1.0", "2.0", "3.0", "1.0", "bla")
                .row("4.0", "5.0", "6.0", "4.0", "blabla")
                .row("7.0", "8.0", "9.0", "7.0", "blablabla")
        ));
    }

}