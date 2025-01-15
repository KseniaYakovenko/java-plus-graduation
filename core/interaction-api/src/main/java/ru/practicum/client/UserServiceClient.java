package ru.practicum.client;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserCreateDto;
import ru.practicum.dto.user.UserDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/{userId}")
    UserDto getById(@PathVariable long userId);

    @GetMapping("/internal/users/{userId}/check")
    void checkExistence(@PathVariable("userId") long userId);

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") long userId);

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto add(@RequestBody @Valid UserCreateDto userCreateDto);

    @GetMapping("/admin/users")
    List<UserDto> getAll(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    );

    @GetMapping("/internal/users/all")
    Map<Long, UserDto> getAll(@RequestParam List<Long> userIds);

}