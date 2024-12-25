package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiatorId(long initiatorId, Pageable pageable);

    Optional<Event> findByInitiatorIdAndId(long initiatorId, long eventId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO LIKES_EVENTS (USER_ID, EVENT_ID) values (:userId, :eventId)", nativeQuery = true)
    void addLike(Long userId, Long eventId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM LIKES_EVENTS WHERE USER_ID = :userId AND EVENT_ID = :eventId", nativeQuery = true)
    void deleteLike(Long userId, Long eventId);


    @Query(value = "SELECT EXISTS (" +
            "SELECT * FROM LIKES_EVENTS WHERE USER_ID = :userId AND EVENT_ID = :eventId)", nativeQuery = true)
    boolean checkLikeExisting(Long userId, Long eventId);

    @Query(value = "SELECT COUNT(*) FROM LIKES_EVENTS WHERE EVENT_ID = :eventId", nativeQuery = true)
    long countLikesByEventId(Long eventId);

    @Query(value = "SELECT E.*, RATE.LIKES FROM EVENTS E LEFT JOIN (\n" +
                        "SELECT EVENT_ID, COUNT(*) AS LIKES FROM LIKES_EVENTS\n" +
                        "GROUP BY EVENT_ID) AS RATE ON E.EVENT_ID = RATE.EVENT_ID\n" +
                   "ORDER BY RATE.LIKES DESC NULLS LAST\n" +
                   "LIMIT :count", nativeQuery = true)
    List<Event> findTop(Integer count);
}
