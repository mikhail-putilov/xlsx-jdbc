package ru.innopolis.mputilov;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import ru.innopolis.mputilov.sql.db_impl.Tuple;

import java.util.Iterator;
import java.util.Objects;

class TableMatchers extends TypeSafeMatcher<Iterable<Tuple>> {

    private TableBuilder expected;
    private String reason;
    public TableMatchers(TableBuilder expected) {
        this.expected = expected;
    }

    public static TableMatchers matchesTable(TableBuilder tableBuilder) {
        return new TableMatchers(tableBuilder);
    }

    @Override
    protected boolean matchesSafely(Iterable<Tuple> actual) {
        Iterator<Tuple> expectedIt = this.expected.iterator();
        Iterator<Tuple> actualIt = actual.iterator();
        int j = 0;
        while (expectedIt.hasNext() || actualIt.hasNext()) {
            Tuple expectedTuple = expectedIt.next();
            Tuple actualTuple = actualIt.next();
            if (expectedTuple.size() != actualTuple.size()) {
                reason = String.format("%dth tuples have same sizes:\nExpected:\n%s\nActual:\n%s", j, expectedTuple, actualTuple);
                return false;
            }
            for (int i = 0; i < expectedTuple.size(); i++) {
                if (!Objects.equals(expectedTuple.get(i), actualTuple.get(i))) {
                    reason = String.format("%dth tuples have same values at position %d.\nExpected:\n%s\nActual:\n%s", j, i, expectedTuple, actualTuple);
                    return false;
                }
            }
            j++;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(reason);
    }
}
