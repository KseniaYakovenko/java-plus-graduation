package ru.practicum.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateCompilationRequestDto {
    List<Long> events;
    Boolean pinned;
    @Size(min = 1, max = 50, message = "Имя не более 50 символов")
    String title;
}