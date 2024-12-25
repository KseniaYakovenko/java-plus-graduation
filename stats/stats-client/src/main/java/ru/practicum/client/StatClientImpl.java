package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.practicum.HitDto;
import ru.practicum.HitStatDto;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class StatClientImpl implements StatClient {

    private static final String STATS_PATH = "/stats";
    private static final String HIT_PATH = "/hit";

    RestClient restClient;

    public StatClientImpl(@Value("${stats-service.url}") String statsServiceUri) {
        restClient = RestClient.builder()
                .baseUrl(statsServiceUri)
                .build();
    }

    public void saveHit(final HitDto hitDto) {
        try {
            restClient
                    .post()
                    .uri(HIT_PATH)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(hitDto)
                    .retrieve()
                    .body(HitDto.class);
        } catch (Exception e) {
            log.error("Ошибка клиента {}", e.getMessage(), e);
        }
    }

    public List<HitStatDto> getStats(final String start,
                                     final String end,
                                     final List<String> uris,
                                     final Boolean unique) {
        try {
            return restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(STATS_PATH)
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.error("Ошибка клиета {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

}
