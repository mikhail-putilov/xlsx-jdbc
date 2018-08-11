package ru.innopolis.mputilov;

import com.google.common.io.Resources;
import org.junit.Test;
import ru.innopolis.mputilov.sql.jdbc.XlsConnection;
import ru.innopolis.mputilov.sql.jdbc.XlsManager;
import ru.innopolis.mputilov.sql.jdbc.XlsResultSet;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.innopolis.mputilov.sql.builder.StatementBuilder.select;

public class TableTest {
    @Test
    public void trivial() {
        URL url = Resources.getResource("test.xlsx");
        XlsConnection conn = XlsManager.getConnection(url);
        XlsResultSet set = conn.createStatement().executeQuery(select("col1", "col2", "col3", "col4", "col5")
                .from("Sheet1")
                .join("Sheet2").on("col1").eq("col5"));
        conn.close();
        assertThat(set.next(), is(true));
    }
}