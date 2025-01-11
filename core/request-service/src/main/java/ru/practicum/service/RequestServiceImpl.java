package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.EventServiceClient;
import ru.practicum.client.UserServiceClient;
import ru.practicum.controller.PrivateUpdateRequestParams;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.Request;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.AccessException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.RequestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserServiceClient userServiceClient;
    private final EventServiceClient eventServiceClient;
    private final RequestMapper requestMapper;

    @Override
    public ParticipationRequestDto create(long userId, long eventId) {
        UserDto user = userServiceClient.getById(userId);
        EventFullDto event = eventServiceClient.getById(eventId);

        long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (userId == event.initiator().id()) {
            throw new ConflictException("Initiator can't request in own event");
        }
        if (!event.state().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (event.participantLimit() != 0 && event.participantLimit() == confirmedRequests) {
            throw new ConflictException("There is no empty place for this event");
        }

        Request creatingRequest = requestMapper.toRequest(user, event);

        if (!event.requestModeration() || event.participantLimit() == 0) {
            creatingRequest.setStatus(RequestStatus.CONFIRMED);
        }

        Request receivedRequest = requestRepository.save(creatingRequest);

        return requestMapper.toParticipationRequestDto(receivedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllOwnRequests(long userId) {
        log.debug("method getAllOwnRequests with user {}", userId);
        userServiceClient.checkExistence(userId);
        log.debug("User with id {} exist", userId);
        List<Request> receivedRequests = requestRepository.getAllByRequesterId(userId);
        log.debug("receivedRequests if user with id {}: {}", userId, receivedRequests);
        return receivedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        userServiceClient.checkExistence(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(canceledRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId) {

        UserDto user = userServiceClient.getById(userId);
        EventFullDto event = eventServiceClient.getById(eventId);

        log.info("getAllForOwnEvent. Пользователь полученный из сервиса user: {} ", user);
        log.info("getAllForOwnEvent. Событие полученное из сервиса event: {} ", event);
        log.info("getAllForOwnEvent. Входящие параметры метода getAllForOwnEvent. UserId: {}, EventId: {}", userId, eventId);


        if (!Objects.equals(event.initiator().id(), user.id())) {
            throw new AccessException("User with id " + userId + " is not own event");
        }

        List<Request> receivedEventsList = requestRepository.getAllByEventId(eventId);

        return receivedEventsList.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateStatus(PrivateUpdateRequestParams params) {
        UserDto user = userServiceClient.getById(params.userId());
        EventFullDto event = eventServiceClient.getById(params.eventId());

        log.info("updateStatus. Пользователь полученный из сервиса user: {} ", user);
        log.info("updateStatus. Событие полученное из сервиса event: {} ", event);
        log.info("updateStatus. Входящие параметры метода updateStatus. PrivateUpdateRequestParams: {}", params);

        if (!Objects.equals(event.initiator().id(), user.id())) {
            throw new AccessException("User with id " + params.userId() + " is not own event");
        }

        List<Request> requestListOfEvent = //Получение всех изменяемых реквестов события
                requestRepository.findAllByIdInAndEventId(
                        params.eventRequestStatusUpdateRequest().requestIds(), params.eventId());

        long confirmedRequestsCount = //Получение количества подвержденных запросов события.
                requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, params.eventId());


        for (Request request : requestListOfEvent) {
            if (request.getStatus() != RequestStatus.PENDING) { // Проверка что все реквесты для изменения - в режиме подтверждения
                throw new ConflictException("Request status is not PENDING");
            }

            if (confirmedRequestsCount >= event.participantLimit()) { // Проверка что количество подтвержденных реквестов не больше лимита
                throw new ConflictException("Participant limit exceeded");
            }

            if (event.requestModeration()) { // Проверка необходимости модерации
                String status = params.eventRequestStatusUpdateRequest().status().toString();
                log.debug("State for update: {}", status);
                requestRepository.updateStatus(
                        status, request.getId());
                Request modifiedRequest = requestRepository.findById(request.getId())
                        .orElseThrow(() -> new NotFoundException("Request with id " + request.getId() + " not found"));
                log.debug("Updated {} {}", modifiedRequest.getId(), modifiedRequest.getStatus());
                if (params.eventRequestStatusUpdateRequest().status() == RequestStatus.CONFIRMED) { //увеличение счетчика подтвержденных событий, в случае потверждения
                    confirmedRequestsCount++;
                }
                if (confirmedRequestsCount >= event.participantLimit()) { //проверка счетчика на превышение, отмена остальных реквестов
                    requestRepository.cancelNewRequestsStatus(event.id());
                }
            }
        }

        List<ParticipationRequestDto> confirmedRequestsDtoList =
                requestRepository.findAllByStatus(RequestStatus.CONFIRMED)
                        .stream()
                        .filter(request -> request.getEventId().equals(event.id()))
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();
        List<Request> rejectedRequests = requestRepository.findAllByStatus(RequestStatus.REJECTED);
        for (Request request : rejectedRequests) {
            log.debug("{} id, status: {}", request.getId(), request.getStatus());
        }

        List<ParticipationRequestDto> rejectedRequestsDtoList =
                rejectedRequests
                        .stream()
                        .filter(request -> request.getEventId().equals(event.id()))
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();

        return new EventRequestStatusUpdateResult(confirmedRequestsDtoList, rejectedRequestsDtoList);
    }

    @Override
    public long countByStatusAndEventId(RequestStatus status, long eventId) {
        return requestRepository.countByStatusAndEventId(status, eventId);
    }

    @Override
    public Map<Long, Long> countByStatusAndEventsIds(RequestStatus status, List<Long> eventsIds) {

        List<Map<String,Long>> list = requestRepository.countByStatusAndEventsIds(RequestStatus.CONFIRMED.toString(), eventsIds);
        Map<Long, Long> eventRequestsWithStatus = new HashMap<>();
        for (Map<String, Long> row : list) {
            Long eventId = row.get("EVENT_ID");
            Long statusCount = row.get("EVENT_COUNT");
            eventRequestsWithStatus.put(eventId, statusCount);
        }
        return eventRequestsWithStatus;
    }
}