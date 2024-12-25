package ru.practicum.service;

import ru.practicum.controller.priv.PrivateUpdateRequestParams;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto create(long userId, long eventId);

    List<ParticipationRequestDto> getAllOwnRequests(long userId);

    ParticipationRequestDto cancel(long userId, long requestId);

    List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params);

}
