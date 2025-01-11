package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.enums.RequestStatus;
import ru.practicum.service.RequestService;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/requests")
public class InternalRequestController {

    private final RequestService requestService;

    @GetMapping("/count/{eventId}")
    public long countByStatusAndEventId(@RequestParam RequestStatus status, @PathVariable long eventId) {
        log.info("|| ==> GET /internal/requests/count/{eventId} Counting by status {} of eventId {}", status, eventId);
        long count = requestService.countByStatusAndEventId(status, eventId);
        log.info("|| <== GET /internal/requests/count/{eventId} Returning count by status {} of eventId {}", status, eventId);
        return count;
    }

    @GetMapping("/count")
    public Map<Long, Long> countByStatusAndEventsIds(@RequestParam RequestStatus status, @RequestParam List<Long> eventsIds) {
        log.info("|| ==> GET /internal/requests/count Counting by status {} of eventIds {}", status, eventsIds);
        Map<Long, Long> counts = requestService.countByStatusAndEventsIds(status, eventsIds);
        log.info("|| <== GET /internal/requests/count Returning count by status {} of eventId {}", status, eventsIds);
        return counts;
    }


}