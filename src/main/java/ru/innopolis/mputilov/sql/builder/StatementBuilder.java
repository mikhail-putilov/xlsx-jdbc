package ru.innopolis.mputilov.sql.builder;

import lombok.Data;
import lombok.Getter;
import ru.innopolis.mputilov.sql.db_impl.DataBase;
import ru.innopolis.mputilov.sql.db_impl.StatementModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class StatementBuilder {
    private List<SelectAliasPair> select;
    private TableAliasPair from;
    private List<WhereEqPair> where = new ArrayList<>();
    private TableAliasPair join;
    private List<OnAliasPair> joinOn = new ArrayList<>();
    private List<EqAliasPair> joinEq = new ArrayList<>();
    private final Function<String, String[]> splitByDot = s -> s.split("\\.");

    private StatementBuilder(String[] select) {
        this.select = Arrays.stream(select)
                .map(splitByDot)
                .map(split -> new SelectAliasPair(split[0], split[1]))
                .collect(Collectors.toList());
    }

    public static FromStep select(String... select) {
        StatementBuilder sql = new StatementBuilder(select);
        return sql.new FromStep();
    }

    @Data
    public static final class SelectAliasPair {
        final String alias;
        final String select;
    }

    @Data
    public static final class WhereEqPair {
        final String where;
        final String eq;
    }

    @Data
    public static final class TableAliasPair {
        final String alias;
        final String tableName;
    }

    @Data
    public static final class OnAliasPair {
        final String alias;
        final String on;
    }

    @Data
    public static final class EqAliasPair {
        final String alias;
        final String eq;
    }

    public class AliasStepAfterFrom {
        private String from;

        public AliasStepAfterFrom(String from) {
            this.from = from;
        }

        public WhereOrJoin alias(String alias) {
            if (from != null) {
                StatementBuilder.this.from = new TableAliasPair(alias, from);
            }
            return new WhereOrJoin();
        }
    }

    public class FromStep {
        public AliasStepAfterFrom from(String from) {
            return new AliasStepAfterFrom(from);
        }
    }

    public class TerminalStep {
        public StatementModel build(DataBase db) {
            return new StatementModel(StatementBuilder.this, db);
        }
    }

    public class AliasStepAfterJoin {
        private String join;

        public AliasStepAfterJoin(String join) {
            this.join = join;
        }

        public JoinOnStep alias(String alias) {
            StatementBuilder.this.join = new TableAliasPair(alias, join);
            return new JoinOnStep();
        }
    }

    public class WhereOrJoin extends TerminalStep {
        public WhereEqStep where(String where) {
            return new WhereEqStep(where);
        }

        public AliasStepAfterJoin join(String join) {
            return new AliasStepAfterJoin(join);
        }
    }

    public class JoinOnStep {

        public JoinEqStep on(String... on) {
            StatementBuilder.this.joinOn = Arrays.stream(on)
                    .map(splitByDot)
                    .map(split -> new OnAliasPair(split[0], split[1]))
                    .collect(Collectors.toList());
            return new JoinEqStep();
        }
    }

    public class JoinEqStep {
        public WhereOrJoin eq(String... eq) {
            StatementBuilder.this.joinEq = Arrays.stream(eq)
                    .map(splitByDot)
                    .map(split -> new EqAliasPair(split[0], split[1]))
                    .collect(Collectors.toList());
            return new WhereOrJoin();
        }
    }

    public class WhereEqStep {
        private String where;

        WhereEqStep(String where) {
            this.where = where;
        }

        public WhereOrJoin eq(String whereEq) {
            StatementBuilder.this.where.add(new WhereEqPair(where, whereEq));
            return new WhereOrJoin();
        }
    }

}
