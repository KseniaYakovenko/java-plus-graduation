package ru.practicum.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.practicum.entity.RequestStatus;

import java.util.List;

public record EventRequestStatusUpdateRequest(

        List<Long> requestIds,

        @NotNull
        RequestStatus status

) {
}
