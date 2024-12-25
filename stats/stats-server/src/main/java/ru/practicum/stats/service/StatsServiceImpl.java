package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;
import ru.practicum.stats.entity.Hit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.stats.mapper.HitDtoMapper.dtoToHit;
import static ru.practicum.stats.mapper.HitDtoMapper.toHitDto;
import static ru.practicum.stats.utils.Constants.formatter;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public HitDto saveHit(HitDto hitDto) {
        return toHitDto(statsRepository.save(dtoToHit(hitDto)));
    }

    @Override
    public List<HitStatDto> getHits(String start, String end, List<String> uris, Boolean unique) {

        List<Hit> data;
        List<HitStatDto> result = new ArrayList<>();
        if ((start.isBlank() || end.isBlank())) {
            data = statsRepository.findAllByUriIn(uris);
        } else {
            LocalDateTime localDateTimeStart = LocalDateTime.parse(start, formatter);
            LocalDateTime localDateTimeEnd = LocalDateTime.parse(end, formatter);
            if (!localDateTimeStart.isBefore(localDateTimeEnd)) {
                throw new IllegalArgumentException("start must be before end");
            }
            data = (uris == null || uris.isEmpty()) ? statsRepository.getStat(localDateTimeStart, localDateTimeEnd) :
                    statsRepository.getStatByUris(localDateTimeStart, localDateTimeEnd, uris);
        }
        Map<String, Map<String, List<Hit>>> mapByAppAndUri = data.stream()
                .collect(Collectors.groupingBy(Hit::getApp,
                        Collectors.groupingBy(Hit::getUri)));
        mapByAppAndUri.forEach((appKey, mapUriValue) -> mapUriValue.forEach((uriKey, hitsValue) -> {
            HitStatDto hitStat = new HitStatDto();
            hitStat.setApp(appKey);
            hitStat.setUri(uriKey);
            List<String> ips = hitsValue.stream().map(Hit::getIp).toList();
            Integer hits = unique ? ips.stream().distinct().toList().size() : ips.size();
            hitStat.setHits(hits);
            result.add(hitStat);
        }));
        return result.stream().sorted(Comparator.comparingInt(HitStatDto::getHits).reversed()).toList();
    }
}
