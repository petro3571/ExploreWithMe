package ru.practicum.services.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.enums.RequestStatus;
import ru.practicum.dto.enums.RuleSort;
import ru.practicum.dto.enums.State;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.dto.event.*;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateRequest;
import ru.practicum.dto.participationRequest.EventRequestStatusUpdateResul;
import ru.practicum.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.entity.*;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.exceptions.RequestConditionsException;
import ru.practicum.mappers.EventMapper;
import ru.practicum.mappers.LocationMapper;
import ru.practicum.mappers.RequestMapper;
import ru.practicum.repo.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;

    @Override
    public EventFullDto addEvent(NewEventRequest request, Long userId) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<Category> category = categoryRepository.findById(request.getCategory());
        if (category.isEmpty()) {
            throw new NotFoundUserException("Категории с id " + request.getCategory() + "нет.");
        }

        Event event = EventMapper.mapToEventFromNewRequest(request);
        event.setCategory(category.get());
        event.setInitiator(findUser.get());
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("For the requested operation the conditions are not met.");
        }

        Location location = LocationMapper.mapToLoc(request.getLocation());
        event.setLocation(locationRepository.save(location));
        event.setCreatedOn(LocalDateTime.now());

        return EventMapper.mapToFullEventDtoFormEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(Long userId, int from, int size) {
        Optional<User> findUser = findUserMethod(userId);


        List<Event> result = eventRepository.findByParamForUser(userId, from, size);

        if (result.isEmpty()) {
            return Collections.emptyList();
        } else {
            return result.stream().map(event -> EventMapper.mapToEventShortDtoFromEvent(event)).collect(Collectors.toList());
        }

//        Pageable pageable = PageRequest.of(from, size, Sort.by("eventDate"));

//        Page<Event> requestsPage = eventRepository.findAllById(userId, pageable);
//
//        if (!requestsPage.getContent().isEmpty()) {
//            List<EventShortDto> content = requestsPage.getContent().stream()
//                    .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
//                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
//                    .collect(Collectors.toList());
//
//            return new PageImpl<>(
//                    content,
//                    requestsPage.getPageable(),
//                    requestsPage.getTotalElements()
//            );
//        } else {
//            return new PageImpl<>(
//                    Collections.emptyList(),
//                    requestsPage.getPageable(),
//                    requestsPage.getTotalElements()
//            );
//        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<Event> findEvent = eventRepository.findByIdAndInitiator_Id(eventId, userId);

        if (findEvent.isPresent()) {
            return EventMapper.mapToFullEventDtoFormEvent(findEvent.get());
        } else {
            throw new NotFoundUserException("Событие которое Вы ищите нет.");
        }
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<Event> event = eventRepository.findByIdAndInitiator_Id(eventId, userId);

        if (event.isPresent() && !event.get().getState().equals(State.PUBLISHED) && event.get().getEventDate()
                .minusHours(2).isAfter(LocalDateTime.now())) {
            Event findEventAndUpdate = EventMapper.mapToEventFromUpdateEvent(event.get(), request);

            Optional<Category> category = categoryRepository.findById(findEventAndUpdate.getCategory().getId());
            if (category.isEmpty()) {
                throw new NotFoundUserException("Категории с id " + request.getCategory() + "нет.");
            }
            findEventAndUpdate.setCategory(category.get());
            return EventMapper.mapToFullEventDtoFormEvent(eventRepository.save(findEventAndUpdate));
        } else {
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<Event> findEvent = findEventMethod(eventId);

        if (!findEvent.get().getInitiator().getId().equals(userId)) {
            throw new NotFoundUserException("Событие с id = " + eventId + " не принадлежит пользователю с id = " + userId);
        }

        // 3. Найти все запросы на участие в этом событии
        List<ParticipationRequest> requests = requestRepository.findByEvent_Id(eventId);

        if (requests.isEmpty()) {
            return Collections.emptyList();
        } else {
            return requests.stream().map(request -> RequestMapper.mapToRequestDtoFromRequest(request))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventRequestStatusUpdateResul changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Пользователь с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundUserException("Событие с id " + eventId + " не найдено"));

        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            throw new ConflictException("Подтверждение заявок не требуется");
        }

        long confirmedCount = requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED);

        RequestStatus status = eventRequest.getStatus();
        if (status == RequestStatus.CONFIRMED && confirmedCount >= event.getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<Long> requestIds = eventRequest.getRequestIds();
        List<ParticipationRequest> requestsToProcess = requestIds.stream()
                .map(id -> requestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundUserException("Заявка с id " + id + " не найдена")))
                .collect(Collectors.toList());

        requestsToProcess.forEach(request -> {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Можно изменять только заявки в состоянии ожидания");
            }
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Заявка не принадлежит данному событию");
            }
        });

        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
        List<ParticipationRequestDto> rejectedList = new ArrayList<>();

        for (ParticipationRequest request : requestsToProcess) {
            if (status == RequestStatus.CONFIRMED) {
                if (confirmedCount < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    confirmedList.add(RequestMapper.mapToRequestDtoFromRequest(request));
                    confirmedCount++;
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(request);
                    rejectedList.add(RequestMapper.mapToRequestDtoFromRequest(request));
                }
            } else if (status == RequestStatus.REJECTED) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedList.add(RequestMapper.mapToRequestDtoFromRequest(request));
            }
        }

        if (status == RequestStatus.CONFIRMED && confirmedCount >= event.getParticipantLimit()) {
            List<ParticipationRequest> pendingRequests = requestRepository
                    .findByEvent_IdAndStatus(eventId, RequestStatus.PENDING);

            for (ParticipationRequest pendingRequest : pendingRequests) {
                if (!requestIds.contains(pendingRequest.getId())) {
                    pendingRequest.setStatus(RequestStatus.REJECTED);
                    requestRepository.save(pendingRequest);
                    rejectedList.add(RequestMapper.mapToRequestDtoFromRequest(pendingRequest));
                }
            }
        }

        return new EventRequestStatusUpdateResul(confirmedList, rejectedList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents_2(List<Long> userIds, List<String> states, List<Long> categoryIds,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        LocalDateTime start = LocalDateTime.now();
        if (rangeStart != null) {
            try {
                start = rangeStart;
            } catch (RuntimeException e) {
                throw new BadRequestException("Cannot parse RangeStart");
            }
        }
        LocalDateTime end = LocalDateTime.now().plusYears(10);
        if (rangeEnd != null) {
            try {
                end = rangeEnd;
            } catch (RuntimeException e) {
                throw new BadRequestException("Cannot parse RangeEnd");
            }
        }
        if (!start.isBefore(end)) {
            throw new ConflictException("RangeStart must be after RangeEnd");
        }
        List<String> listStates = List.of(State.PENDING.toString(),
                State.PUBLISHED.toString(),
                State.CANCELED.toString());
        if (states != null) {
            listStates = states;
        }

        List<Event> events = eventRepository.findByParam(userIds, start, end, categoryIds, listStates, from, size);
        Map<Long, Long> views = findViewsForEvents(events);
        return events.stream()
                .map(event -> EventMapper.mapToFullEventDtoFormEvent(event))
                .peek(event -> {
                    event.setConfirmedRequests(requestRepository.countConfirmedRequestsForEvent(event.getId()));
                    event.setViews(views.get(event.getId()));
                }).toList();
    }

    @Override
    public EventFullDto updateEvent_1(Long eventId, UpdateEventAdminRequest request) {
        Optional<Event> findEvent = findEventMethod(eventId);
        Event existingEvent = findEvent.orElseThrow(() ->
                new NotFoundUserException("Событие с id=" + eventId + " не найдено"));

        if (request.getStateAction().equals(StateAction.REJECT_EVENT) && existingEvent.getState().equals(State.PUBLISHED)
                || (request.getStateAction().equals(StateAction.PUBLISH_EVENT) && existingEvent.getState().equals(State.PUBLISHED))
                || (request.getStateAction().equals(StateAction.PUBLISH_EVENT) && existingEvent.getState().equals(State.CANCELED))
                || (request.getStateAction().equals(StateAction.REJECT_EVENT) && existingEvent.getState().equals(State.CANCELED))) {
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }

        Event event = EventMapper.mapToEventFromUpdateEventAdmin(existingEvent, request);

        if (event.getLocation() != null && event.getLocation().getId() == null) {
            Location savedLocation = locationRepository.save(event.getLocation());
            event.setLocation(savedLocation);
        }

        if (request.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            event.setPublishedOn(LocalDateTime.now());
            event.setState(State.PUBLISHED);
        } else if (request.getStateAction().equals(StateAction.REJECT_EVENT)) {
            event.setState(State.CANCELED);
        }

        if (event.getPublishedOn() != null &&
                event.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
            Event savedEvent = eventRepository.save(event);
            return EventMapper.mapToFullEventDtoFormEvent(savedEvent);
        } else {
            throw new RequestConditionsException("Событие не удовлетворяет правилам редактирования");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents_1(String text, List<Long> categoryIds, boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        if (sort.equals(RuleSort.EVENT_DATE.toString())) {
            if (rangeStart == null && rangeEnd == null) {
                List<Event> result = eventRepository.findByParamWhenDatePresAndSortEventDate(text, categoryIds, paid,
                        rangeStart, rangeEnd, from, size);

                if (result.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    return result.stream().map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                            .collect(Collectors.toList());
                }
            } else {
                List<Event> result = eventRepository.findByAfterNowEventDateSort(text, categoryIds, paid,
                        LocalDateTime.now(), from, size);

                if (result.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    return result.stream().map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                            .collect(Collectors.toList());
                }
            }
        } else if (sort.equals(RuleSort.VIEWS.toString())) {
            if (rangeStart == null && rangeEnd == null) {
                List<Event> result = eventRepository.findByParamWhenDatePresAndSortViews(text, categoryIds, paid,
                        rangeStart, rangeEnd, from, size);

                if (result.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    return result.stream().map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                            .collect(Collectors.toList());
                }
            } else {
                List<Event> result = eventRepository.findByAfterNowViewsSort(text, categoryIds, paid,
                        LocalDateTime.now(), from, size);

                if (result.isEmpty()) {
                    return Collections.emptyList();
                } else {
                    return result.stream().map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                            .collect(Collectors.toList());
                }
            }
        } else {
            throw new BadRequestException("Incorrectly made request.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent_1(Long eventId) {
        Optional<Event> findEvent = findEventMethod(eventId);

        if (findEvent.get().getState().equals(State.PUBLISHED)) {
            EventFullDto event = EventMapper.mapToFullEventDtoFormEvent(findEvent.get());
            event.setViews(findViewsForEvents(List.of(findEvent.get())).get(eventId));
            return event;

        } else {
            throw new NotFoundUserException("Событие не опубликовано.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        Optional<User> findUser = findUserMethod(userId);

        List<ParticipationRequest> listRequest = requestRepository.findByRequester_Id(userId);

        if (listRequest.isEmpty()) {
            return Collections.emptyList();
        } else {
            return listRequest.stream().map(request -> RequestMapper.mapToRequestDtoFromRequest(request))
                    .toList();
        }
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<Event> findEvent = findEventMethod(eventId);

        List<ParticipationRequest> findRequests = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (!findRequests.isEmpty() || findEvent.get().getInitiator().equals(findUser.get())
                || findEvent.get().getState().equals(State.PENDING) || findEvent.get().getState().equals(State.CANCELED)) {
            throw new ConflictException("Integrity constraint has been violated.");
        }

        if (findEvent.get().getConfirmedRequests() != null) {
            if (findEvent.get().getConfirmedRequests().equals(findEvent.get().getParticipantLimit())) {
                throw new ConflictException("Integrity constraint has been violated.");
            }
        }

        if (!findEvent.get().isRequestModeration()) {
            if (findEvent.get().getConfirmedRequests() != null) {
                ParticipationRequest request = new ParticipationRequest();
                request.setEvent(findEvent.get());
                request.setRequester(findUser.get());
                request.setStatus(RequestStatus.CONFIRMED);
                request.setCreated(LocalDateTime.now());
                findEvent.get().setConfirmedRequests(findEvent.get().getConfirmedRequests() + 1);//
                ParticipationRequestDto dto = RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
                eventRepository.save(findEvent.get());

                return dto;
            } else {
                ParticipationRequest request = new ParticipationRequest();
                request.setEvent(findEvent.get());
                request.setRequester(findUser.get());
                request.setStatus(RequestStatus.CONFIRMED);
                request.setCreated(LocalDateTime.now());
                findEvent.get().setConfirmedRequests(1L);
                ParticipationRequestDto dto = RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
                eventRepository.save(findEvent.get());

                return dto;
            }
        } else if (findEvent.get().isRequestModeration() || findEvent.get().getParticipantLimit() == 0 /*|| findEvent.get().getConfirmedRequests() < findEvent.get().getParticipantLimit()*/) {
            if (findEvent.get().getConfirmedRequests() != null) {
                ParticipationRequest request = new ParticipationRequest();
                request.setEvent(findEvent.get());
                request.setRequester(findUser.get());
                request.setStatus(RequestStatus.PENDING);
                request.setCreated(LocalDateTime.now());
                findEvent.get().setConfirmedRequests(findEvent.get().getConfirmedRequests() + 1);
                ParticipationRequestDto dto = RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
                eventRepository.save(findEvent.get());

                return dto;
            } else {
                ParticipationRequest request = new ParticipationRequest();
                request.setEvent(findEvent.get());
                request.setRequester(findUser.get());
                request.setStatus(RequestStatus.PENDING);
                request.setCreated(LocalDateTime.now());
                findEvent.get().setConfirmedRequests(1L);
                ParticipationRequestDto dto = RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
                eventRepository.save(findEvent.get());

                return dto;
            }
        } else {
            throw new ConflictException("Integrity constraint has been violated.");
        }
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Optional<User> findUser = findUserMethod(userId);

        Optional<ParticipationRequest> findRequest = requestRepository.findById(requestId);
        if (findRequest.isEmpty()) {
            throw new NotFoundUserException("Заявки с id " + userId + "нет.");
        }
        ParticipationRequest request = findRequest.get();
        if (request.getRequester().equals(findUser.get())) {
            request.setStatus(RequestStatus.CANCELED);
            return RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
        } else {
            throw new ConflictException("Пользователь не является автором заявки.");
        }
    }

    private Optional<User> findUserMethod(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }
        return findUser;
    }

    private Optional<Event> findEventMethod(Long eventId) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new NotFoundUserException("Событие с id " + eventId + " нет.");
        }
        return findEvent;
    }

    private Map<Long, Long> findViewsForEvents(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            String uriTemplate = "/events/";
            LocalDateTime start = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now().minusYears(1));

            LocalDateTime end = LocalDateTime.now();

            List<String> uris = events.stream()
                    .map(event -> uriTemplate + event.getId())
                    .collect(Collectors.toList());

            ResponseEntity<Object> response = statsClient.getStats(start.format(FORMATTER), end.format(FORMATTER), uris, true);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() instanceof List) {
                List<?> responseList = (List<?>) response.getBody();
                List<ViewStatsDto> stats = responseList.stream()
                        .filter(item -> item instanceof Map)
                        .map(item -> objectMapper.convertValue(item, ViewStatsDto.class))
                        .collect(Collectors.toList());

                Map<Long, Long> result = new HashMap<>();
                for (ViewStatsDto stat : stats) {
                    try {
                        String uri = stat.getUri();
                        if (uri.startsWith(uriTemplate)) {
                            Long id = Long.parseLong(uri.substring(uriTemplate.length()));
                            result.put(id, stat.getHits());
                        }
                    } catch (NumberFormatException e) {
                        throw new Exception("sadadkslmsda");
                    }
                }

                for (Event event : events) {
                    result.putIfAbsent(event.getId(), 0L);
                }

                return result;
            } else {
                throw new InternalError("Failed to get stats: " + response.getStatusCode());
            }

        } catch (Exception e) {
            return createDefaultResult(events);
        }
    }

    private List<ViewStatsDto> convertResponseToViewStats(Object responseObject) {
        if (responseObject instanceof List) {
            List<?> responseList = (List<?>) responseObject;
            return responseList.stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> objectMapper.convertValue(item, ViewStatsDto.class))
                    .collect(Collectors.toList());
        } else if (responseObject instanceof byte[]) {
            // Если приходят raw bytes, пытаемся распарсить как JSON
            try {
                String json = new String((byte[]) responseObject, StandardCharsets.UTF_8);
                return objectMapper.readValue(json,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ViewStatsDto.class));
            } catch (Exception e) {
                throw new InternalError("Failed to parse stats response: " + e.getMessage());
            }
        } else {
            throw new InternalError("Unknown response type: " + responseObject.getClass());
        }
    }

    private Map<Long, Long> createDefaultResult(List<Event> events) {
        Map<Long, Long> result = new HashMap<>();
        for (Event event : events) {
            result.put(event.getId(), 0L);
        }
        return result;
    }
}