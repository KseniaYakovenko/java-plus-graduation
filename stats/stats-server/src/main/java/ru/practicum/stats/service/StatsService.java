package ru.practicum.stats.service;

import ru.practicum.HitDto;
import ru.practicum.HitStatDto;

import java.util.List;

public interface StatsService {
    HitDto saveHit(HitDto hitDto);

    List<HitStatDto> getHits(String start, String end, List<String> uris, Boolean unique);
}
