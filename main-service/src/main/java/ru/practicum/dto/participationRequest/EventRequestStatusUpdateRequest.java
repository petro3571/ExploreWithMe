package ru.practicum.dto.participationRequest;

import lombok.Data;
import ru.practicum.dto.enums.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}