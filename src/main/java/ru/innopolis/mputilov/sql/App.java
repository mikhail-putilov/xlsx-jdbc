package ru.innopolis.mputilov.sql;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DbModule());
    }
}
