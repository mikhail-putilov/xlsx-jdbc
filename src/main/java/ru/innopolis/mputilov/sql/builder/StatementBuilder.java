package ru.innopolis.mputilov.sql.builder;

import ru.innopolis.mputilov.sql.db_impl.StatementModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatementBuilder {
    public static final class WherePair {
        WherePair(String where, String eq) {
            this.where = where;
            this.eq = eq;
        }

        final String where;
        final String eq;
    }
    private List<String> select = new ArrayList<>();
    private String from;
    private List<WherePair> where = new ArrayList<>();
    private String join;
    private List<String> joinOn = new ArrayList<>();
    private List<String> joinEq = new ArrayList<>();

    public String getFrom() {
        return from;
    }

    public String getJoin() {
        return join;
    }

    private StatementBuilder(String[] select) {
        Collections.addAll(this.select, select);
    }

    public static FromStep select(String... select) {
        StatementBuilder sql = new StatementBuilder(select);
        return sql.new FromStep();
    }

    public class FromStep {
        public WhereOrJoin from(String from) {
            StatementBuilder.this.from = from;
            return new WhereOrJoin();
        }
    }

    public class TerminalStep {
        public StatementModel build() {
            return new StatementModel(StatementBuilder.this);
        }
    }

    public class WhereOrJoin extends TerminalStep {
        public WhereEqStep where(String where) {
            return new WhereEqStep(where);
        }

        public JoinOnStep join(String join) {
            StatementBuilder.this.join = join;
            return new JoinOnStep();
        }
    }
    public class JoinOnStep {

        public JoinEqStep on(String... on) {
            Collections.addAll(StatementBuilder.this.joinOn, on);
            return new JoinEqStep();
        }
    }

    public class JoinEqStep {
        public WhereOrJoin eq(String... eq) {
            Collections.addAll(StatementBuilder.this.joinEq, eq);
            return new WhereOrJoin();
        }
    }

    public class WhereEqStep {
        private String where;

        WhereEqStep(String where) {
            this.where = where;
        }

        public WhereOrJoin eq(String whereEq) {
            StatementBuilder.this.where.add(new WherePair(where, whereEq));
            return new WhereOrJoin();
        }
    }

}
