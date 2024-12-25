package ru.practicum.client;

import ru.practicum.HitDto;
import ru.practicum.HitStatDto;

import java.util.List;

public interface StatClient {

    void saveHit(HitDto hitDto);

    List<HitStatDto> getStats(final String startTime,
                              final String endTime,
                              final List<String> uris,
                              final Boolean unique);
}
