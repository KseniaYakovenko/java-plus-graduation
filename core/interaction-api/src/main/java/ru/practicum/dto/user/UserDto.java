package ru.practicum.dto.user;

public record UserDto(
        String email,
        Long id,
        String name
) {
}
