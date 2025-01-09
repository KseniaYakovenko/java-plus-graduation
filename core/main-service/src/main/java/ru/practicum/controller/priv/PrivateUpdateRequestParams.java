package ru.practicum.controller.priv;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;

public record PrivateUpdateRequestParams(
        long userId,
        long eventId,
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
) {
}
