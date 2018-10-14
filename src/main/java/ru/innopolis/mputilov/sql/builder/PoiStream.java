package ru.innopolis.mputilov.sql.builder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class PoiStream {
    private PoiStream() {
    }

    static Stream<Cell> stream(Sheet sheet) {
        return StreamSupport.stream(sheet.getRow(0).spliterator(), false);
    }
}
