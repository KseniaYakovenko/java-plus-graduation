package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.HitDto;
import ru.practicum.entity.Hit;

import java.time.LocalDateTime;

import static ru.practicum.utils.Constants.FORMATTER;

@UtilityClass
public class HitDtoMapper {

    public static HitDto toHitDto(Hit hit) {
        String dateTime = hit.getTimestamp().format(FORMATTER);

        return new HitDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                dateTime
        );
    }

    public static Hit dtoToHit(HitDto hitDto) {

        LocalDateTime localDateTime = LocalDateTime.parse(hitDto.getTimestamp(), FORMATTER);
        Hit hit = new Hit();
        hit.setId(hitDto.getId());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(localDateTime);
        return hit;
    }
}

