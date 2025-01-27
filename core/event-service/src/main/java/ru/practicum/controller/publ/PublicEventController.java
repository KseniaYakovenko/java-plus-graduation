package ru.practicum.controller.publ;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.AnalyzerClient;
import ru.practicum.CollectorClient;
import ru.practicum.client.RequestServiceClient;
import ru.practicum.controller.params.EventGetByIdParams;
import ru.practicum.controller.params.search.EventSearchParams;
import ru.practicum.controller.params.search.PublicSearchParams;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventRecommendationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.enums.EventState;
import ru.practicum.ewm.stats.proto.ActionTypeProto;
import ru.practicum.ewm.stats.proto.RecommendationsMessages;
import ru.practicum.exception.IncorrectValueException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String X_EWM_USER_ID_HEADER = "X-EWM-USER-ID";
    private final EventService eventService;
    private final AnalyzerClient analyzerClient;
    private final RequestServiceClient requestServiceClient;
    private final CollectorClient collectorClient;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @GetMapping("/recommendations")
    public List<EventRecommendationDto> getRecommendations(@RequestHeader(X_EWM_USER_ID_HEADER) long userId,
                                                           @RequestParam(defaultValue = "10") int maxResults) {

        var recStream = analyzerClient.getRecommendationsForUser(userId, maxResults);
        var recList = recStream.toList();

        List<EventRecommendationDto> result = new ArrayList<>();
        for (RecommendationsMessages.RecommendedEventProto rp : recList) {
            result.add(new EventRecommendationDto(rp.getEventId(), rp.getScore()));
        }
        return result;
    }

    @PutMapping("/events/{eventId}/like")
    public void likeEvent(@PathVariable Long eventId,
                          @RequestHeader(X_EWM_USER_ID_HEADER) long userId) {
        eventService.addLike(userId, eventId);

        collectorClient.sendUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpRequest) {
        log.info("==> GET /events Public searching events with params: " +
                        "text {}, categories: {}, paid {}, rangeStart: {}, rangeEnd: {}, available {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectValueException("rangeStart of event can't be after rangeEnd");
        }

        EventSearchParams eventSearchParams = new EventSearchParams();
        PublicSearchParams publicSearchParams = new PublicSearchParams();
        publicSearchParams.setText(text);
        publicSearchParams.setCategories(categories);
        publicSearchParams.setPaid(paid);

        publicSearchParams.setRangeStart(rangeStart);
        publicSearchParams.setRangeEnd(rangeEnd);

        eventSearchParams.setPublicSearchParams(publicSearchParams);
        eventSearchParams.setFrom(from);
        eventSearchParams.setSize(size);

        List<EventShortDto> eventShortDtoList = eventService.getAllByPublic(eventSearchParams);
        log.info("<== GET /events Returning public searching events. List size: {}",
                eventShortDtoList.size());
        return eventShortDtoList;
    }

    @GetMapping("/top")
    public List<EventShortDto> getTop(
            @RequestParam(required = false, defaultValue = "10") Integer count,
            HttpServletRequest httpRequest) {
        log.info("==> GET /events/top");

        List<EventShortDto> eventShortDtoList = eventService.getTopEvent(count);
        log.info("<== GET /events Returning top {} events.", count);
        return eventShortDtoList;
    }

    @GetMapping("/top-view")
    public List<EventShortDto> getTopView(
            @RequestParam(required = false, defaultValue = "10") Integer count,
            HttpServletRequest httpRequest) {
        log.info("==> GET /events/top-view");

        List<EventShortDto> eventShortDtoList = eventService.getTopViewEvent(count);
        log.info("<== GET /events Returning top view {} events.", count);
        return eventShortDtoList;
    }

    @GetMapping("/{id}")
    @Transactional
    public EventFullDto getById(
            @PathVariable Long id, HttpServletRequest httpRequest
    ) {
        log.info("==> GET /events/{}  Public getById", id);

        EventFullDto eventFullDto = eventService.getById(new EventGetByIdParams(null, id));
        if (eventFullDto.state() != EventState.PUBLISHED) {
            throw new NotFoundException("Нет опубликованных событий с id " + id);
        }
        log.info("<== GET /events/{}  Public getById", id);
        return eventFullDto;
    }
}
