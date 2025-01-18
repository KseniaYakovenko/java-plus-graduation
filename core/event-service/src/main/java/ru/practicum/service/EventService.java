package ru.practicum.service;

import ru.practicum.controller.params.EventGetByIdParams;
import ru.practicum.controller.params.EventUpdateParams;
import ru.practicum.controller.params.search.EventSearchParams;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;

import java.util.List;

public interface EventService {

    EventFullDto create(long userId, NewEventDto newEventDto);

    EventFullDto getById(EventGetByIdParams params);

    EventFullDto update(long eventId, EventUpdateParams updateParams);

    List<EventFullDto> getAllByAdmin(EventSearchParams searchParams);

    EventShortDto addLike(long userId, long eventId);

    void deleteLike(long userId, long eventId);

    List<EventShortDto> getAllByInitiator(EventSearchParams searchParams);

    List<EventShortDto> getAllByPublic(EventSearchParams searchParams);

    List<EventShortDto> getTopEvent(Integer count);

    List<EventShortDto> getTopViewEvent(Integer count);

    EventFullDto getByIdInternal(long eventId);
}
