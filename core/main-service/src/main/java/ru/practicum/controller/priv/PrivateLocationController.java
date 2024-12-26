package ru.practicum.controller.priv;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.service.LocationService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/locations")
public class PrivateLocationController {

    private final LocationService locationService;

    @PutMapping("/{locationId}/likes")
    public LocationDto addLike(//Добавление лайка на локацию
                                @PathVariable long userId,
                                @PathVariable long locationId) {

        log.info("==> PUT. /users/{userId}/locations/{locationId}/likes" +
                "Adding like for location with id: {} by user with id: {}", locationId, userId);
        LocationDto locationDto = locationService.addLike(userId, locationId);
        log.info("<== PUT. /users/{userId}/events/{eventId}/likes" +
                "Like for location with id: {} by user with id: {} added.", locationId, userId);
        return locationDto;
    }

    @DeleteMapping("/{locationId}/likes")
    public void deleteLike(//удаление лайка на локацию
                            @PathVariable long userId,
                            @PathVariable long locationId
    ) {
        log.info("==> DELETE. /users/{userId}/events/{eventId}/likes" +
                "Deleting like for location with id: {} by user with id: {}", locationId, userId);
        locationService.deleteLike(userId, locationId);
        log.info("<== DELETE. /users/{userId}/events/{eventId}/likes" +
                "Like for location with id: {} by user with id: {} deleted.", locationId, userId);
    }

    @GetMapping("/top")
    public List<LocationDto> getTop(
            @PathVariable long userId,
            @RequestParam(required = false, defaultValue = "10") Integer count,
            HttpServletRequest httpRequest) {
        log.info("==> GET /users/{userId}/locations/top");

        List<LocationDto> locationDtoList = locationService.getTop(userId, count);
        log.info("<== GET /users/{userId}/locations/top Returning top {} locations.", count);
        return locationDtoList;
    }



}



