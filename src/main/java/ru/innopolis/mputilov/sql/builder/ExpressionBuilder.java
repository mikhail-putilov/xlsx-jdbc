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
public class ExpressionBuilder {
    private static final Function<String, String[]> splitByDot = s -> s.split("\\.");
    private List<SelectAliasPair> select = new ArrayList<>();
    private List<TableAliasPair> tables = new ArrayList<>();
    private List<WhereEqPair> where = new ArrayList<>();
    private List<JoinConditionAliasPair> joinConditions = new ArrayList<>();

    private ExpressionBuilder(String[] select) {
        Arrays.stream(select)
                .map(splitByDot)
                .map(split -> new SelectAliasPair(split[0], split[1]))
                .collect(Collectors.toCollection(() -> this.select));
    }

    public static FromStep select(String... select) {
        ExpressionBuilder sql = new ExpressionBuilder(select);
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
    public static final class JoinConditionAliasPair {
        final String alias;
        final String column;
    }

    public class AliasStepAfterFrom {
        private String from;

        AliasStepAfterFrom(String from) {
            this.from = from;
        }

        public WhereOrJoin alias(String alias) {
            ExpressionBuilder.this.tables.add(new TableAliasPair(alias, from));
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
            return new StatementModel(ExpressionBuilder.this, db);
        }
    }

    public class AliasStepAfterJoin {
        private String join;

        AliasStepAfterJoin(String join) {
            this.join = join;
        }

        public JoinOnStep alias(String alias) {
            ExpressionBuilder.this.tables.add(new TableAliasPair(alias, join));
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
            Arrays.stream(on)
                    .map(splitByDot)
                    .map(split -> new JoinConditionAliasPair(split[0], split[1]))
                    .collect(Collectors.toCollection(() -> joinConditions));
            return new JoinEqStep();
        }
    }

    public class JoinEqStep {
        public WhereOrJoin eq(String... eq) {
            Arrays.stream(eq)
                    .map(splitByDot)
                    .map(split -> new JoinConditionAliasPair(split[0], split[1]))
                    .collect(Collectors.toCollection(() -> joinConditions));
            return new WhereOrJoin();
        }
    }

    public class WhereEqStep {
        private String where;

        WhereEqStep(String where) {
            this.where = where;
        }

        public WhereOrJoin eq(String whereEq) {
            ExpressionBuilder.this.where.add(new WhereEqPair(where, whereEq));
            return new WhereOrJoin();
        }
    }

}
