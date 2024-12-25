package ru.practicum.dto.request;

import ru.practicum.entity.RequestStatus;

import java.time.LocalDateTime;

public record ParticipationRequestDto(

        LocalDateTime created,

        Long event,

        Long id,

        Long requester,

        RequestStatus status
) {
}
