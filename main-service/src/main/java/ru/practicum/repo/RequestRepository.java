package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.enums.RequestStatus;
import ru.practicum.entity.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findById(Long id);

    List<ParticipationRequest> findByRequester_Id(Long userId);

    long countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByEvent_Id(Long eventId);

    @Query(value = "SELECT COUNT(r.id) FROM requests r WHERE r.event_id = ?1 AND r.status LIKE 'CONFIRMED'", nativeQuery = true)
    Long countConfirmedRequestsForEvent(Long eventId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);
}