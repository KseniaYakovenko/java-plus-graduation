package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.entity.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query(value = "MERGE INTO LIKES_LOCATIONS (LOCATION_ID, USER_ID) values (:locationId, :userId)", nativeQuery = true)
    void addLike(Long userId, Long locationId);

    @Query(value = "DELETE FROM LIKES_LOCATIONS WHERE LOCATION_ID = :locationId AND USER_ID = :userId ", nativeQuery = true)
    void deleteLike(Long userId, Long locationId);

    @Query(value = "SELECT EXISTS (" +
            "SELECT * FROM LIKES_LOCATIONS WHERE LOCATION_ID = :locationId AND USER_ID = :userId)", nativeQuery = true)
    boolean checkLikeExisting(Long userId, Long locationId);

    @Query(value = "SELECT L.*, RATE.LIKES FROM LOCATIONS L LEFT JOIN (\n" +
            "SELECT LOCATION_ID, COUNT(*) AS LIKES FROM LIKES_LOCATIONS\n" +
            "GROUP BY LOCATION_ID) AS RATE ON L.LOCATION_ID = RATE.LOCATION_ID\n" +
            "ORDER BY RATE.LIKES DESC NULLS LAST\n" +
            "LIMIT :count", nativeQuery = true)
    List<Location> findTop(Integer count);

    @Query(value = "SELECT COUNT(*) FROM LIKES_LOCATIONS WHERE LOCATION_ID = :locationId", nativeQuery = true)
    long countLikesByLocationId(Long locationId);


}
