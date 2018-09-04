package ru.innopolis.mputilov;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import ru.innopolis.mputilov.sql.db.Tuple;

import java.util.Iterator;
import java.util.Objects;

import static java.lang.String.format;

class TableMatchers extends TypeSafeMatcher<Iterable<Tuple>> {

    private TableBuilder expected;
    private String messageExpected;
    private String messageActual;
    private TableMatchers(TableBuilder expected) {
        this.expected = expected;
    }

    static TableMatchers matchesTable(TableBuilder tableBuilder) {
        return new TableMatchers(tableBuilder);
    }

    @Override
    protected void describeMismatchSafely(Iterable<Tuple> item, Description mismatchDescription) {
        mismatchDescription.appendText(messageActual);
    }

    @Override
    protected boolean matchesSafely(Iterable<Tuple> actual) {
        Iterator<Tuple> expectedIt = this.expected.iterator();
        Iterator<Tuple> actualIt = actual.iterator();
        if (!expectedIt.hasNext()) {
            return !actualIt.hasNext();
        }
        if (!actualIt.hasNext()) {
            messageActual = "actual table is empty";
            messageExpected = format("first row must be %s", expectedIt.next());
            return false;
        }
        int j = 0;
        while (expectedIt.hasNext() || actualIt.hasNext()) {
            Tuple expectedTuple = expectedIt.next();
            Tuple actualTuple = actualIt.next();
            if (expectedTuple.size() != actualTuple.size()) {
                messageExpected = format("%dth tuple is expected to be %d in size", j, expectedTuple.size());
                messageActual = format("%dth tuple is %d in size", j, actualTuple.size());
                return false;
            }
            for (int i = 0; i < expectedTuple.size(); i++) {
                if (!Objects.equals(expectedTuple.get(i), actualTuple.get(i))) {
                    messageExpected = format("%dth tuple, %d column is expected to be %s", j, i, expectedTuple.get(i));
                    messageActual = format("%dth tuple, %d column is %s", j, i, actualTuple.get(i));
                    return false;
                }
            }
            j++;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(messageExpected);
    }
}
