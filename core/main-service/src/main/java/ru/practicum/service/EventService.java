package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.controller.params.EventGetByIdParams;
import ru.practicum.controller.params.EventUpdateParams;
import ru.practicum.controller.params.search.EventSearchParams;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getById(EventGetByIdParams params, HitDto hitDto);

    EventFullDto update(long eventId, EventUpdateParams updateParams);

    List<EventFullDto> getAllByAdmin(EventSearchParams searchParams);

    EventShortDto addLike(long userId, long eventId);

    void deleteLike(long userId, long eventId);

    List<EventShortDto> getAllByInitiator(EventSearchParams searchParams);

    List<EventShortDto> getAllByPublic(EventSearchParams searchParams, HitDto hitDto);

    List<EventShortDto> getTopEvent(Integer count, HitDto hitDto);

    List<EventShortDto> getTopViewEvent(Integer count, HitDto hitDto);
}
