package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto locationToLocationDto(Location location);
}
