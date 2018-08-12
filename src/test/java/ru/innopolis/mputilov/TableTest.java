package ru.innopolis.mputilov;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;
import ru.innopolis.mputilov.sql.jdbc.XlsManager;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static ru.innopolis.mputilov.TableMatchers.matchesTable;
import static ru.innopolis.mputilov.sql.builder.StatementBuilder.select;

public class TableTest {
    @Test
    public void trivial() {
        URL url = Resources.getResource("test.xlsx");
        XlsConnection conn = XlsManager.getConnection(url);
        XlsResultSet set = conn.createStatement().executeQuery(select("s1.col1", "s1.col2", "s1.col3", "s2.col5", "s2.col6")
                .from("Sheet1").alias("s1")
                .join("Sheet2").alias("s2").on("s1.col1").eq("s2.col5")
                .where("s1.col1").eq("1"));
        conn.close();
        assertThat(set, matchesTable(TableBuilder.create()
                .row("1.0", "2.0", "3.0", "1.0", "bla")
                .row("4.0", "5.0", "6.0", "4.0", "blabla")
                .row("7.0", "8.0", "9.0", "7.0", "blablabla")
        ));
    }

}