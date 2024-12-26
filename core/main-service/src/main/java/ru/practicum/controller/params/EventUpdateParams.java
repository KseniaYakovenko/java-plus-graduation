package ru.practicum.controller.params;

import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;

public record EventUpdateParams(
        Long userId,
        UpdateEventUserRequest updateEventUserRequest,
        UpdateEventAdminRequest updateEventAdminRequest
) {
}
