package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.Request;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class RequestMapper {

    @Mapping(target = "event", expression = "java(request.getEventId())")
    @Mapping(target = "requester", expression = "java(request.getRequesterId())")
    public abstract ParticipationRequestDto toParticipationRequestDto(Request request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "requesterId", source = "user.id")
    @Mapping(target = "status", expression = "java(setRequestStatus(event))")
    @Mapping(target = "created", expression = "java(getCurrentLocalDatetime())")
    public abstract Request toRequest(UserDto user, EventFullDto event);

    @Named("getCurrentLocalDatetime")
    LocalDateTime getCurrentLocalDatetime() {
        return LocalDateTime.now().withNano(0);
    }

    @Named("getPendingEventState")
    RequestStatus setRequestStatus(EventFullDto event) {
        if (!event.requestModeration() || event.participantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }
}