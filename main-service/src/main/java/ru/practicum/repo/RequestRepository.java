package ru.practicum.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.entity.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findByEvent_IdAndRequester_Id(Long eventId, Long requesterId);

    Optional<ParticipationRequest> findById(Long id);

    List<ParticipationRequest> findByRequester_Id(Long userId);
}