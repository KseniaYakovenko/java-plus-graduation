package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.entity.Location;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(

        String annotation,

        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime createOn,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime eventDate,

        Long id,

        UserShortDto initiator,

        Location location,

        boolean paid,

        String title,

        Long views,

        Long likesCount

) {
}
