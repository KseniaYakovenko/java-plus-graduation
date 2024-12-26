package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @NotBlank @Size (min = 6, max = 254) @Email String email,
        @NotBlank @Size(min = 2, max = 250) String name
) {
}
