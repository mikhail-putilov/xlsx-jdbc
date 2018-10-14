package ru.innopolis.mputilov;

import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.innopolis.mputilov.sql.builder.*;
import ru.innopolis.mputilov.sql.builder.vo.ColumnExp;
import ru.innopolis.mputilov.sql.db.ResultSet;
import ru.innopolis.mputilov.sql.db.vo.TableAliasPair;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;
import ru.innopolis.mputilov.sql.jdbc.XlsManager;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static ru.innopolis.mputilov.TableBuilder.table;
import static ru.innopolis.mputilov.TableMatchers.matchesTable;
import static ru.innopolis.mputilov.sql.builder.Operation.EQ;

public class TableTest {

    private XlsConnection conn;

    @Before
    public void setUp() {
        URL url = Resources.getResource("test.xlsx");
        conn = XlsManager.getConnection(url);
    }

    @After
    public void tearDown() {
        conn.close();
    }

    @Test
    public void trivialJoin() {
        ResultSet set = conn.createStatement().executeQuery(new SqlExp(
                new SelectExp(
                        ColumnExp.of("s1", "col1"),
                        ColumnExp.of("s1", "col2"),
                        ColumnExp.of("s1", "col3"),
                        ColumnExp.of("s2", "col5"),
                        ColumnExp.of("s2", "col6")),
                new JoinEq(
                        new TableExp(new TableAliasPair("s1", "Sheet1")),
                        new TableExp(new TableAliasPair("s2", "Sheet2")),
                        "j",
                        new WhereExp(
                                EQ,
                                ColumnExp.of("s1", "col1"),
                                ColumnExp.of("s2", "col5"))),
                null,
                null));
        assertThat(set, matchesTable(table()
                .row("1.0", "2.0", "3.0", "1.0", "bla")
                .row("4.0", "5.0", "6.0", "4.0", "blabla")
                .row("7.0", "8.0", "9.0", "7.0", "blablabla")
        ));
    }

    @Test
    public void trivialJoinAndWhere() {
        ResultSet set = conn.createStatement().executeQuery(new SqlExp(
                new SelectExp(
                        ColumnExp.of("s1", "col1"),
                        ColumnExp.of("s1", "col2"),
                        ColumnExp.of("s1", "col3"),
                        ColumnExp.of("s2", "col5"),
                        ColumnExp.of("s2", "col6")),
                new JoinEq(
                        new TableExp(new TableAliasPair("s1", "Sheet1")),
                        new TableExp(new TableAliasPair("s2", "Sheet2")),
                        "j",
                        new WhereExp(
                                EQ,
                                ColumnExp.of("s1", "col1"),
                                ColumnExp.of("s2", "col5"))),
                new WhereExp(
                        EQ,
                        ColumnExp.of("s1", "col1"),
                        ColumnExp.staticColumn("4.0")),
                null));
        assertThat(set, matchesTable(table()
                .row("4.0", "5.0", "6.0", "4.0", "blabla")
        ));
    }

    @Test
    public void trivialReordering() {
        ResultSet set = conn.createStatement().executeQuery(new SqlExp(
                new SelectExp(
                        ColumnExp.of("s1", "col3"),
                        ColumnExp.of("s1", "col2"),
                        ColumnExp.of("s2", "col5"),
                        ColumnExp.of("s1", "col1"),
                        ColumnExp.of("s2", "col6")),
                new JoinEq(
                        new TableExp(new TableAliasPair("s1", "Sheet1")),
                        new TableExp(new TableAliasPair("s2", "Sheet2")),
                        "j",
                        new WhereExp(
                                EQ,
                                ColumnExp.of("s1", "col1"),
                                ColumnExp.of("s2", "col5"))),
                new WhereExp(
                        EQ,
                        ColumnExp.of("s1", "col1"),
                        ColumnExp.staticColumn("4.0")),
                null));
        assertThat(set, matchesTable(table()
                .row("6.0", "5.0", "4.0", "4.0", "blabla")
        ));
    }

}