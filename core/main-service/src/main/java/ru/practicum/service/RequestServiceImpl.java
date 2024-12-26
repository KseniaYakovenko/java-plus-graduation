package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.controller.priv.PrivateUpdateRequestParams;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.entity.*;
import ru.practicum.exception.AccessException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        Long confirmedRequests = requestRepository.countByStatusAndEventId(RequestStatus.CONFIRMED, eventId);

        if (userId == event.getInitiator().getId()) {
            throw new ConflictException("Initiator can't request in own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event is not published");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirmedRequests) {
            throw new ConflictException("There is no empty place for this event");
        }

        Request creatingRequest = requestMapper.toRequest(user, event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            creatingRequest.setStatus(RequestStatus.CONFIRMED);
        }

        Request receivedRequest = requestRepository.save(creatingRequest);

        return requestMapper.toParticipationRequestDto(receivedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllOwnRequests(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        List<Request> receivedRequests = requestRepository.getAllByRequesterId(userId);

        return receivedRequests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto cancel(long userId, long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(canceledRequest);
    }

    @Override
    public List<ParticipationRequestDto> getAllForOwnEvent(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " not found"));

        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
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
        User user = userRepository.findById(params.userId()) //Проверка наличия пользователя
                .orElseThrow(() -> new NotFoundException("User with id " + params.userId() + " not found"));
        Event event = eventRepository.findById(params.eventId()) //Проверка наличия события
                .orElseThrow(() -> new NotFoundException("Event with id " + params.eventId() + " not found"));

        if (!Objects.equals(event.getInitiator().getId(), user.getId())) {
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

            if (confirmedRequestsCount >= event.getParticipantLimit()) { // Проверка что количество подтвержденных реквестов не больше лимита
                throw new ConflictException("Participant limit exceeded");
            }

            if (event.isRequestModeration()) { // Проверка необходимости модерации
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
                if (confirmedRequestsCount >= event.getParticipantLimit()) { //проверка счетчика на превышение, отмена остальных реквестов
                    requestRepository.cancelNewRequestsStatus(event.getId());
                }
            }
        }

        List<ParticipationRequestDto> confirmedRequestsDtoList =
                requestRepository.findAllByStatus(RequestStatus.CONFIRMED)
                        .stream()
                        .filter(request -> request.getEvent().getId() == event.getId())
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();
        List<Request> rejectedRequests = requestRepository.findAllByStatus(RequestStatus.REJECTED);
        for (Request request : rejectedRequests) {
            log.debug("{} id, status: {}", request.getId(), request.getStatus());
        }

        List<ParticipationRequestDto> rejectedRequestsDtoList =
                rejectedRequests
                        .stream()
                        .filter(request -> request.getEvent().getId() == event.getId())
                        .map(requestMapper::toParticipationRequestDto)
                        .toList();

        return new EventRequestStatusUpdateResult(confirmedRequestsDtoList, rejectedRequestsDtoList);
    }


}
