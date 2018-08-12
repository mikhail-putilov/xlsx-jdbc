package ru.innopolis.mputilov.sql.db_impl;

import java.util.List;

public class CompoundStringPrimaryKey extends PrimaryKey<List<String>> {
    public CompoundStringPrimaryKey(List<String> key) {
        super(key);
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
