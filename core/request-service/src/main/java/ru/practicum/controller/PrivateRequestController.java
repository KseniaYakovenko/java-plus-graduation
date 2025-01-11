package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(
            @PathVariable long userId,
            @RequestParam long eventId) {
        log.info("==> POST. /users/{userId}/requests " +
                "Creating new Request with id: {} by user with id: {}", eventId, userId);
        ParticipationRequestDto receivedRequestDto = requestService.create(userId, eventId);
        log.info("<== POST. /users/{userId}/requests " +
                "Returning new Request {}: {}", receivedRequestDto.id(), receivedRequestDto);
        return receivedRequestDto;
    }

    @GetMapping
    public List<ParticipationRequestDto> getOwnRequests(
            @PathVariable long userId) {
        log.info("==> GET. /users/{userId}/requests " +
                "Getting all requests of user with id: {} ", userId);
        List<ParticipationRequestDto> requestDtoList = requestService.getAllOwnRequests(userId);
        log.info("<== GET. /users/{userId}/requests " +
                "Returning all requests of user with id: {} ", userId);
        return requestDtoList;
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancel(
            @PathVariable long userId,
            @PathVariable long requestId) {
        log.info("==> PATCH. /users/{userId}/requests/{requestId}/cancel" +
                "Cancelling request with id {} by user with id: {} ", requestId, userId);
        ParticipationRequestDto receivedDto = requestService.cancel(userId, requestId);
        log.info("<== PATCH. /users/{userId}/requests/{requestId}/cancel" +
                "Request with id {} CANCELED by user with id: {} ", requestId, userId);
        return receivedDto;
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForOwnEvent(
            @PathVariable long userId,
            @PathVariable long eventId) {
        log.info("==> GET. /users/{userId}/events/{eventId}/requests " +
                "Getting requests for own event with id: {}, of user with id: {}", eventId, userId);

        List<ParticipationRequestDto> receivedRequestsDtoList
                = requestService.getAllForOwnEvent(userId, eventId);

        log.info("<== GET. /users/{userId}/events/{eventId}/requests " +
                "Returning requests for own event with id: {} of user with id: {}", eventId, userId);

        return receivedRequestsDtoList;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest updateRequestStatusDto) {

        log.info("==> PATCH. /users/{userId}/events/{eventId}/requests " +
                "Changing request status for own event with id: {} of user with id: {}", eventId, userId);
        log.info("EventRequestStatusUpdateRequest. Deserialized body: {}", updateRequestStatusDto);
        EventRequestStatusUpdateResult eventUpdateResult =
                requestService.updateStatus(new PrivateUpdateRequestParams(userId, eventId, updateRequestStatusDto));
        log.info("<== PATCH. /users/{userId}/events/{eventId}/requests " +
                "Changed request status for own event with id: {} of user with id: {}", eventId, userId);
        return eventUpdateResult;
    }


}