package ru.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.AnalyzerClient;
import ru.practicum.client.RequestServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.controller.params.EventGetByIdParams;
import ru.practicum.controller.params.EventUpdateParams;
import ru.practicum.controller.params.search.EventSearchParams;
import ru.practicum.controller.params.search.PublicSearchParams;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.Location;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.enums.StateAction;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.exception.AccessException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.IncorrectValueException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.mapper.LocationMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.practicum.entity.QEvent.event;
import static ru.practicum.utils.Constants.TIMESTAMP_PATTERN;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserServiceClient userServiceClient;
    private final EventMapper eventMapper;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestServiceClient requestServiceClient;
    private final LocationMapper locationMapper;

    private final AnalyzerClient analyzerClient;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);

    @Override
    public EventFullDto create(long userId, NewEventDto newEventDto) {
        UserDto initiator = userServiceClient.getById(userId);
        Category category = categoryRepository.findById(newEventDto.category())
                .orElseThrow(() -> new NotFoundException("Category with id " + newEventDto.category() + " not found"));
        Location location = locationRepository.save(
                locationMapper.locationDtoToLocation(newEventDto.location()));
        Event event = eventMapper.newEventDtoToEvent(
                newEventDto, initiator.id(), category, location, LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);
        savedEvent.setInitiator(initiator);
        savedEvent.setLocation(location);
        return eventMapper.eventToEventFullDto(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(EventSearchParams searchParams) {

        long initiatorId = searchParams.getPrivateSearchParams().getInitiatorId();

        UserDto userDto = userServiceClient.getById(initiatorId);
        Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());
        List<Event> receivedEvents = eventRepository.findAllByInitiatorId(initiatorId, page);
        for (Event event : receivedEvents) {
            event.setLikes(eventRepository.countLikesByEventId(event.getId()));
        }

        return receivedEvents.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByPublic(EventSearchParams searchParams) {

        Pageable page = PageRequest.of(searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        PublicSearchParams publicSearchParams = searchParams.getPublicSearchParams();

        if (publicSearchParams.getText() != null) { //наличие поиска по тексту
            booleanExpression = booleanExpression.andAnyOf(
                    event.annotation.likeIgnoreCase(publicSearchParams.getText()),
                    event.description.likeIgnoreCase(publicSearchParams.getText())
            );
        }

        if (publicSearchParams.getCategories() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.category.id.in((publicSearchParams.getCategories())));
        }

        if (publicSearchParams.getPaid() != null) { // наличие поиска по категориям
            booleanExpression = booleanExpression.and(
                    event.paid.eq(publicSearchParams.getPaid()));
        }

        LocalDateTime rangeStart = publicSearchParams.getRangeStart();
        LocalDateTime rangeEnd = publicSearchParams.getRangeEnd();

        if (rangeStart != null && rangeEnd != null) { // наличие поиска дате события
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd)
            );
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart)
            );
            rangeEnd = rangeStart.plusYears(100);
        } else if (publicSearchParams.getRangeEnd() != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd)
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
        }

        if (rangeEnd == null && rangeStart == null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(LocalDateTime.now())
            );
            rangeStart = LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
            rangeEnd = rangeStart.plusYears(100);
        }

        List<Event> eventListBySearch = eventListBySearch =
                eventRepository.findAll(booleanExpression, page).stream().toList();

        // analyzerClient.saveHit(hitDto);


        for (Event event : eventListBySearch) {

            Long view = 0L;

            event.setViews(view);
            event.setConfirmedRequests(
                    requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
            event.setLikes(eventRepository.countLikesByEventId(event.getId()));
        }

        return eventListBySearch.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getTopEvent(Integer count) {
        List<Event> eventTopList = getTopEvents(count);
        return eventTopList.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }


    private List<Event> getTopEvents(Integer count) {
        List<Event> eventTopList = eventRepository.findTop(count);
        for (Event event : eventTopList) {
            event.setConfirmedRequests(
                    requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
            event.setLikes(eventRepository.countLikesByEventId(event.getId()));
            double rate = analyzerClient.getInteractionsCount(List.of(event.getId()))
                    .findFirst()
                    .map(RecommendationsMessages.RecommendedEventProto::getScore)
                    .orElse(0.0);
            event.setRating(rate);
        }
        return eventTopList;
    }



    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(EventSearchParams searchParams) {
        Pageable page = PageRequest.of(
                searchParams.getFrom(), searchParams.getSize());

        BooleanExpression booleanExpression = event.isNotNull();

        if (searchParams.getAdminSearchParams().getUsers() != null) {
            booleanExpression = booleanExpression.and(
                    event.initiatorId.in(searchParams.getAdminSearchParams().getUsers()));
        }

        if (searchParams.getAdminSearchParams().getCategories() != null) {
            booleanExpression = booleanExpression.and(
                    event.category.id.in(searchParams.getAdminSearchParams().getCategories()));
        }

        if (searchParams.getAdminSearchParams().getStates() != null) {
            booleanExpression = booleanExpression.and(
                    event.state.in(searchParams.getAdminSearchParams().getStates()));
        }

        LocalDateTime rangeStart = searchParams.getAdminSearchParams().getRangeStart();
        LocalDateTime rangeEnd = searchParams.getAdminSearchParams().getRangeEnd();

        if (rangeStart != null && rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.between(rangeStart, rangeEnd));
        } else if (rangeStart != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.after(rangeStart));
        } else if (rangeEnd != null) {
            booleanExpression = booleanExpression.and(
                    event.eventDate.before(rangeEnd));
        }

        List<Event> receivedEventList = eventRepository.findAll(booleanExpression, page).stream().toList();
        for (Event event : receivedEventList) {
            event.setConfirmedRequests(requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, event.getId()));
            event.setLikes(eventRepository.countLikesByEventId(event.getId()));
        }

        return receivedEventList
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getTopViewEvent(Integer count) {
        List<Event> eventTopList = getTopEvents(count);
        return eventTopList.stream()
                .map(eventMapper::eventToEventShortDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(EventGetByIdParams params) {
        Event receivedEvent;
        if (params.initiatorId() != null) {
            userServiceClient.checkExistence(params.initiatorId());
            receivedEvent = eventRepository.findByInitiatorIdAndId(params.initiatorId(), params.eventId())
                    .orElseThrow(() -> new NotFoundException(
                            "Event with id " + params.eventId() +
                                    " created by user with id " + params.initiatorId() + " not found"));
        } else {
            receivedEvent = eventRepository.findById(params.eventId())
                    .orElseThrow(() -> new NotFoundException("Event with id " + params.eventId() + " not found"));


            receivedEvent.setConfirmedRequests(
                    requestServiceClient.countByStatusAndEventId(RequestStatus.CONFIRMED, receivedEvent.getId()));
            receivedEvent.setLikes(eventRepository.countLikesByEventId(receivedEvent.getId()));
        }
        UserDto initiator = userServiceClient.getById(receivedEvent.getInitiatorId());
        receivedEvent.setInitiator(initiator);
        return eventMapper.eventToEventFullDto(receivedEvent);
    }

    @Override
    public EventFullDto update(long eventId, EventUpdateParams updateParams) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        Event updatedEvent;

        if (updateParams.updateEventUserRequest() != null) { // private section
            userServiceClient.checkExistence(updateParams.userId());
            if (updateParams.updateEventUserRequest().category() != null) {
                Category category = categoryRepository.findById(updateParams.updateEventUserRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventUserRequest().category() + " not found"));
                event.setCategory(category);
            }
            if (!updateParams.userId().equals(event.getInitiatorId())) {
                throw new AccessException("User with id = " + updateParams.userId() + " do not initiate this event");
            }

            if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
                throw new ConflictException(
                        "User. Cannot update event: only pending or canceled events can be changed");
            }

            LocalDateTime eventDate = updateParams.updateEventUserRequest().eventDate();

            if (eventDate != null &&
                    eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ConflictException(
                        "User. Cannot update event: event date must be not earlier then after 2 hours ");
            }

            StateAction stateAction = updateParams.updateEventUserRequest().stateAction();
            log.debug("State action received from params: {}", stateAction);

            if (stateAction != null) {
                switch (stateAction) {
                    case CANCEL_REVIEW -> event.setState(EventState.CANCELED);

                    case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                }
            }

            if (updateParams.updateEventUserRequest().location() != null) {
                event.setLocation(locationMapper.locationDtoToLocation(
                        updateParams.updateEventUserRequest().location())
                );
            }


            log.debug("Private. Событие до мапинга: {}", event);
            eventMapper.updateEventUserRequestToEvent(event, updateParams.updateEventUserRequest());
            log.debug("Private. Событие после мапинга для сохранения: {}", event);

        }

        if (updateParams.updateEventAdminRequest() != null) { // admin section

            if (updateParams.updateEventAdminRequest().category() != null) {
                Category category  = categoryRepository.findById(updateParams.updateEventAdminRequest().category())
                        .orElseThrow(() -> new NotFoundException(
                                "Category with id " + updateParams.updateEventAdminRequest().category() + " not found"));
                event.setCategory(category);
            }

            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Admin. Cannot update event: only pending events can be changed");
            }

            if (updateParams.updateEventAdminRequest().eventDate() != null &&
                    updateParams.updateEventAdminRequest().eventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new IncorrectValueException(
                        "Admin. Cannot update event: event date must be not earlier then after 2 hours ");
            }
            log.debug("Admin. Событие до мапинга: {}; {}", event.getId(), event.getState());
            eventMapper.updateEventAdminRequestToEvent(event, updateParams.updateEventAdminRequest());
            log.debug("Admin. Событие после мапинга для сохранения: {}, {}", event.getId(), event.getState());

        }
        event.setId(eventId);

        updatedEvent = eventRepository.save(event);

        updatedEvent.setLikes(eventRepository.countLikesByEventId(updatedEvent.getId()));

        updatedEvent.setConfirmedRequests(requestServiceClient.countByStatusAndEventId(
                RequestStatus.CONFIRMED, updatedEvent.getId()));

        updatedEvent.setInitiator(userServiceClient.getById(updatedEvent.getInitiatorId()));

        log.debug("Событие возвращенное из базы: {} ; {}", event.getId(), event.getState());

        return eventMapper.eventToEventFullDto(updatedEvent);
    }

    @Override
    public EventShortDto addLike(long userId, long eventId) {
         Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event with id " + eventId + " is not published");
        }
        eventRepository.addLike(userId, eventId);
        event.setLikes(eventRepository.countLikesByEventId(eventId));
        return eventMapper.eventToEventShortDto(event);
    }

    @Override
    public void deleteLike(long userId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        boolean isLikeExist = eventRepository.checkLikeExisting(userId, eventId);
        if (isLikeExist) {
            eventRepository.deleteLike(userId, eventId);
        } else {
            throw new NotFoundException("Like for event: " + eventId + " by user: " + userId + " not exist");
        }
    }

    @Override
    public EventFullDto getByIdInternal(long eventId) {
        Event savedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));
        savedEvent.setInitiator(userServiceClient.getById(savedEvent.getInitiatorId()));
        return eventMapper.eventToEventFullDto(savedEvent);
    }

}
