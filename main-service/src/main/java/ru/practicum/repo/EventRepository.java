package ru.practicum.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT e.* FROM events e " +
            "WHERE e.initiator_id = :userId " +
            "AND e.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<Event> findByParamForUser(@Param("userId") Long userId,
                            @Param("from") int from,
                            @Param("size") int size);

    List<Event> findAllById(Long userId, Pageable pageable);

    Optional<Event> findByCategory_Id(Long catId);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findByIdIn(List<Long> eventsId);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE (:userIds IS NULL OR e.initiator_id IN :userIds) " +
            "AND e.id > :from " +
            "AND (e.event_date >= :rangeStart) " +
            "AND (e.event_date <= :rangeEnd) " +
            "AND (:categoryIds IS NULL OR e.category_id IN :categoryIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "LIMIT :size",
            nativeQuery = true)
    List<Event> findByParam(@Param("userIds") List<Long> userIds,
                            @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd,
                            @Param("categoryIds") List<Long> categoryIds,
                            @Param("states") List<String> states,
                            @Param("from") int from,
                            @Param("size") int size);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE (e.event_date BETWEEN :start AND :end) " +
            "AND e.id > :from " +
            "AND (:categoryIds IS NULL OR e.category_id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "ORDER BY e.event_date " +
            "LIMIT :size", nativeQuery = true)
    List<Event> findByParamWhenDatePresAndSortEventDate(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("start")  LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,  @Param("from") int from,
            @Param("size") int size);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE e.event_date > :now " +
            "AND e.id > :from " +
            "AND (:categoryIds IS NULL OR e.category_id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "ORDER BY e.event_date " +
            "LIMIT :size", nativeQuery = true)
    List<Event> findByAfterNowEventDateSort(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("now")  LocalDateTime now, @Param("from") int from,
            @Param("size") int size);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE (e.event_date BETWEEN :start AND :end) " +
            "AND e.id > :from " +
            "AND (:categoryIds IS NULL OR e.category_id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "ORDER BY e.views " +
            "LIMIT :size", nativeQuery = true)
    List<Event> findByParamWhenDatePresAndSortViews(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("start")  LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd,  @Param("from") int from,
            @Param("size") int size);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE e.event_date > :now " +
            "AND e.id > :from " +
            "AND (:categoryIds IS NULL OR e.category_id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')))) " +
            "ORDER BY e.views " +
            "LIMIT :size", nativeQuery = true)
    List<Event> findByAfterNowViewsSort(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("now")  LocalDateTime now, @Param("from") int from,
            @Param("size") int size);
}