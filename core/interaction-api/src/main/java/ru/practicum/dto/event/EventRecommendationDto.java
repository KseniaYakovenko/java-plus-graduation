package ru.practicum.dto.event;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
    public class EventRecommendationDto {
    private long eventId;
    private double score;
}