package ru.practicum.dto.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @Pattern(regexp = "\\S+")
    @Size(max = 50, message = "Имя не более 50 символов")
    @NotNull
    String name;
}
