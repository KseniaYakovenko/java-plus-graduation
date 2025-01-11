package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.entity.Location;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.repository.LocationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    public LocationDto addLike(long userId, long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        locationRepository.addLike(userId, locationId);
        return locationMapper.locationToLocationDto(locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found")));
    }

    @Override
    public void deleteLike(long userId, long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id " + locationId + " not found"));
        if (!locationRepository.checkLikeExisting(userId, locationId)) {
            throw new NotFoundException("Like for Location: " + locationId + " by user: " + userId + " not exist");
        }
        locationRepository.deleteLike(userId, locationId);
    }

    @Override
    public List<LocationDto> getTop(long userId, Integer count) {

        List<Location> locationTopList = locationRepository.findTop(count);

        for (Location location : locationTopList) {
            location.setLikes(locationRepository.countLikesByLocationId(location.getId()));
        }

        return locationTopList.stream()
                .map(locationMapper::locationToLocationDto)
                .toList();
    }

}
