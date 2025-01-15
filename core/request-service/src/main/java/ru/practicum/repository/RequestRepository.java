package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Request;
import ru.practicum.enums.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getAllByRequesterId(long requesterId);

    List<Request> getAllByEventId(long eventId);

    List<Request> findAllByIdInAndEventId(List<Long> ids, long eventId);

    long countByStatusAndEventId(RequestStatus status, long eventId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE REQUESTS SET STATUS = ?1 WHERE REQUEST_ID = ?2", nativeQuery = true)
    void updateStatus(String status, long requestId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE REQUESTS SET STATUS = 'CANCELED' WHERE EVENT_ID = ?1 AND STATUS = 'PENDING'", nativeQuery = true)
    void cancelNewRequestsStatus(long eventId);

    @Query(value = "SELECT r from Request r where r.status = ?1")
    List<Request> findAllByStatus(RequestStatus status);

    @Query(value = """
            select EVENT_ID, COUNT(*) as EVENT_COUNT
            from REQUESTS where event_id in (:eventsIds) AND status = :status
            GROUP BY EVENT_ID
            """, nativeQuery = true)
    List<Map<String, Long>> countByStatusAndEventsIds(
            @Param("status") String status, @Param("eventsIds") List<Long> eventsIds);

}