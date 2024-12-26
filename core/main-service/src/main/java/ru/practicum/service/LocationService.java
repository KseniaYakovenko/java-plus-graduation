package ru.practicum.service;

import ru.practicum.dto.location.LocationDto;

import java.util.List;

public interface LocationService {

    LocationDto addLike(long userId, long locationId);

    void deleteLike(long userId, long locationId);

    List<LocationDto> getTop(long userId, Integer count);
}
