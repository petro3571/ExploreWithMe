package ru.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllById(Long userId, Pageable pageable);

    Optional<Event> findByCategory_Id(Long catId);

    Optional<Event> findByIdAndInitiator_Id(Long eventId, Long userId);

    List<Event> findByIdIn(List<Long> eventsId);

//    @Query(value = "SELECT e" +
//            "FROM Event as e " +
//            "WHERE (e.event_date BETWEEN :start AND :end) AND e.category_id IN :categoryIds AND e.state IN :states"
//            )
    Page<Event> findByInitiator_IdInAndEventDateBetweenAndCategory_IdInAndStateIn(@Param("userIds") List<Long> userIds,
                                        @Param("start") LocalDateTime rangeStart,
                                        @Param("end") LocalDateTime rangeEnd,@Param("categoryIds") List<Long> categoryIds, @Param("states") List<String> states, Pageable pageable);

    @Query(value = "SELECT e FROM Event e " +
            "WHERE (e.eventDate BETWEEN :start AND :end) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))))")
    Page<Event> findByDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategory_IdInAndPaidAndEventDateBetweenAndConfirmedRequestsLessThan(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("start")  LocalDateTime rangeStart,
            @Param("end") LocalDateTime rangeEnd, Pageable pageable);

    @Query(value = "SELECT e FROM Event e " +
            "WHERE e.eventDate > :now " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (:text IS NULL OR (LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%'))))")
    Page<Event> findByAfterNowEvent(
            @Param("text") String str,@Param("categoryIds") List<Long> categoryIds,@Param("paid") Boolean paid,@Param("now")  LocalDateTime now, Pageable pageable);
}
