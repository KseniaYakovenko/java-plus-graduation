package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.location.LocationDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "todo-service")
public interface LocationServiceClient {

    @GetMapping("/users/{userId}/locations/top")
    List<LocationDto> getTop(@PathVariable long userId,
                             @RequestParam(required = false, defaultValue = "10") Integer count);

    @PostMapping("/users/{userId}/locations")
    LocationDto create(@PathVariable long userId,
                       @RequestBody LocationDto location);

    @GetMapping("/internal/locations/{locationId}")
    LocationDto getById(@PathVariable long locationId);

    @GetMapping("/internal/locations/all")
    Map<Long, LocationDto> getAllById(@RequestParam List<Long> locationIds);

}