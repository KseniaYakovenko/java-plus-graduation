package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.dto.event.annotation.FutureAfterTwoHours;
import ru.practicum.dto.location.LocationDto;

import java.time.LocalDateTime;
import java.util.Objects;

public record NewEventDto(

        @NotBlank @Size(min = 20, max = 2000)
        String annotation,

        Long category,

        @NotBlank @Size(min = 20, max = 7000)
        String description,

        @NotNull @FutureAfterTwoHours @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        @NotNull
        LocationDto location,

        Boolean paid,

        @PositiveOrZero
        Integer participantLimit,

        Boolean requestModeration,

        @NotNull @Size(min = 3, max = 120)
        String title
) {
        public NewEventDto(
                String annotation,
                Long category,
                String description,
                LocalDateTime eventDate,
                LocationDto location,
                Boolean paid,
                Integer participantLimit,
                Boolean requestModeration,
                String title) {

                this.annotation = annotation;
                this.category = category;
                this.description = description;
                this.eventDate = eventDate;
                this.location = location;
                this.paid = Objects.requireNonNullElse(paid, false);
                this.participantLimit = Objects.requireNonNullElse(participantLimit, 0);
                this.requestModeration = Objects.requireNonNullElse(requestModeration, true);
                this.title = title;

        }


}
