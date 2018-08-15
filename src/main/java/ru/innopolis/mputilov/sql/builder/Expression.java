package ru.innopolis.mputilov.sql.builder;

interface Expression<T> {
    T eval(Context ctx);
}
