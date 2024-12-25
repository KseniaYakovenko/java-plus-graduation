package ru.practicum.stats.utils;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String timestampPattern = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampPattern);
}