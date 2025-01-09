package ru.practicum.controller.params.search;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PublicSearchParams {

    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;

}
