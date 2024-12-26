package ru.practicum.mapper;

import jakarta.validation.ValidationException;
import org.mapstruct.*;
import ru.practicum.dto.event.*;
import ru.practicum.entity.*;
import ru.practicum.entity.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    @Mapping(target = "createOn", expression = "java(getCurrentLocalDatetime())")
    @Mapping(target = "state", expression = "java(getPendingEventState())")
    @Mapping(target = "id", ignore = true)
    public abstract Event newEventDtoToEvent(
            NewEventDto newEventDto, User initiator, Category category, Location location, LocalDateTime createOn);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "location", ignore = true)
    public abstract void updateEventUserRequestToEvent(@MappingTarget Event event, UpdateEventUserRequest updateEventUserRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "publishedOn", expression =
            "java(getPublishedOn(updateEventAdminRequest))")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "state", expression = "java(getAdminEventState(updateEventAdminRequest))")
    @Mapping(target = "location", ignore = true)
    public abstract void updateEventAdminRequestToEvent(
            @MappingTarget Event event, UpdateEventAdminRequest updateEventAdminRequest);

    public abstract EventFullDto eventToEventFullDto(Event event);

    @Mapping(source = "likes", target = "likesCount")
    public abstract EventShortDto eventToEventShortDto(Event event);

    @Named("getCurrentLocalDatetime")
    LocalDateTime getCurrentLocalDatetime() {
        return LocalDateTime.now();
    }

    @Named("getPendingEventState")
    EventState getPendingEventState() {
        return EventState.PENDING;
    }

    @Named("getAdminEventState")
    EventState getAdminEventState(UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.stateAction() != null) {
            switch (updateEventAdminRequest.stateAction()) {
                case PUBLISH_EVENT -> {
                    return EventState.PUBLISHED;
                }
                case REJECT_EVENT -> {
                    return EventState.CANCELED;
                }
                default -> throw new ValidationException("EventMapper: Invalid state action");
            }
        } else return EventState.PENDING;
    }

    @Named("getPublishedOn")
    LocalDateTime getPublishedOn(UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.stateAction() != null) {
            switch (updateEventAdminRequest.stateAction()) {
                case PUBLISH_EVENT -> {
                    return getCurrentLocalDatetime();
                }
                case REJECT_EVENT -> {
                    return null;
                }
                default -> throw new ValidationException("EventMapper: Invalid state action");
            }
        } else {
            return null;
        }
    }

}
