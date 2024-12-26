package ru.practicum.utils;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
}