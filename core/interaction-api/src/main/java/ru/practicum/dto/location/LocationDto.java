package ru.practicum.dto.location;

public record LocationDto(
        Long id,
        Float lat,
        Float lon,
        Long likes
) {
}


