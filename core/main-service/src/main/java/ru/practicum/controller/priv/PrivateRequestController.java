package ru.practicum.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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

}
