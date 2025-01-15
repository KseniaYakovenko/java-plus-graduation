package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.event.EventFullDto;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/internal/events/{eventId}")
    EventFullDto getById(@PathVariable long eventId);

}