package ru.innopolis.mputilov.sql.db_impl;

import java.util.List;
import java.util.function.Function;

public class CompoundStringPrimaryKey extends PrimaryKey<List<String>> {
    public CompoundStringPrimaryKey(List<String> key) {
        super(key);
    }

    public static CompoundStringPrimaryKey of(Function<List<String>, List<String>> keyExtractor, List<String> tuple) {
        return new CompoundStringPrimaryKey(keyExtractor.apply(tuple));
    }

    @Override
    public int compareTo(PrimaryKey<List<String>> o) {
        int res = 0;
        List<String> key = getKey();
        List<String> oKey = o.getKey();
        for (int i = 0; i < key.size(); i++) {
            res = key.get(i).compareTo(oKey.get(i));
            if (res != 0) {
                return res;
            }
        }
        return res;
    }
}
