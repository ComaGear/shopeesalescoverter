package com.colbertlum.constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimePattern {
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static LocalDate getLocalDate(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate();
    }

    public static String parseString(LocalDate localDate){
        return localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
    }
}
