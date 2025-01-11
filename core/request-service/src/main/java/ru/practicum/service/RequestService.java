package ru.practicum.service;

import ru.practicum.controller.PrivateUpdateRequestParams;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestService {

    ParticipationRequestDto create(long userId, long eventId);

    List<ParticipationRequestDto> getAllOwnRequests(long userId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params);

    long countByStatusAndEventId(RequestStatus status, long eventId);

    Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds);

}