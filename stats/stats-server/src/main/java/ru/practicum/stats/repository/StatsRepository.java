package ru.practicum.stats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.entity.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select h from Hit h " +
            "where h.timestamp > :start and h.timestamp < :end and h.uri in :uris ")
    List<Hit> getStatByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select h from Hit h " +
            "where h.timestamp > :start and h.timestamp < :end")
    List<Hit> getStat(LocalDateTime start, LocalDateTime end);

    List<Hit> findAllByUriIn(List<String> uris);
}

