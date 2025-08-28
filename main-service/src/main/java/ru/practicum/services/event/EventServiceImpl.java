package ru.practicum.services.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.enums.RequestStatus;
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
import ru.practicum.mappers.RequestMapper;
import ru.practicum.repo.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService{
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;

    @Override
    public EventFullDto addEvent(NewEventRequest request, Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            throw new NotFoundUserException("Категории с id " + request.getCategoryId() + "нет.");
        }

        Event event = EventMapper.mapToEventFromNewRequest(request);
        event.setCategory(category.get());
        event.setInitiator(findUser.get());
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestConditionsException("For the requested operation the conditions are not met.");
        }

        Location location = new Location();
        location.setLon(request.getLocation().getLon());
        location.setLat(request.getLocation().getLat());
        location = locationRepository.save(location);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());

        return EventMapper.mapToFullEventDtoFormEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventShortDto> getEvents(Long userId, int from, int size) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Pageable pageable = PageRequest.of(from, size, Sort.by("eventDate"));

        Page<Event> requestsPage = eventRepository.findAllById(userId, pageable);

        if (!requestsPage.getContent().isEmpty()) {
            List<EventShortDto> content = requestsPage.getContent().stream()
                    .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                    .sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        } else {
            return new PageImpl<>(
                    Collections.emptyList(),
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        if (eventId == null) {
            throw new RuntimeException("Id события нет.");
        }

        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<Event> findEvent = eventRepository.findByIdAndInitiator_Id(eventId, userId);

        if (findEvent.isPresent()) {
            return EventMapper.mapToFullEventDtoFormEvent(findEvent.get());
        } else {
            throw new NotFoundUserException("События которое Вы ищите нет.");
        }
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        if (eventId == null) {
            throw new RuntimeException("Id события нет.");
        }

        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            throw new NotFoundUserException("Категории с id " + request.getCategoryId() + "нет.");
        }

        Optional<Event> event = eventRepository.findByIdAndInitiator_Id(eventId, userId);

        if (event.isPresent() && !event.get().getState().equals(State.PUBLISHED) && event.get().getEventDate()
                .minusHours(2).isAfter(LocalDateTime.now())) {
            Event findEventAndUpdate = EventMapper.mapToEventFromUpdateEvent(event.get(), request);
            findEventAndUpdate.setCategory(category.get());
            return EventMapper.mapToFullEventDtoFormEvent(eventRepository.save(findEventAndUpdate));
        } else {
            throw new ConflictException("Событие не удовлетворяет правилам редактирования");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        if (eventId == null) {
            throw new RuntimeException("Id события нет.");
        }

        if (userId == null) {
            throw new RuntimeException("Id пользователя нет.");
        }

        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        List<ParticipationRequest> requests = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (requests.isEmpty()) {
            return Collections.emptyList();
        } else {
            return requests.stream().map(request -> RequestMapper.mapToRequestDtoFromRequest(request))
                    .collect(Collectors.toList());
        }
//        Optional<Event> findEvent = eventRepository.findByIdAndInitiator_Id(eventId, userId);
//        if (findEvent.isEmpty()) {
//            throw new NotFoundUserException("Событие с id " + eventId + "нет.");
//        }
//
//        ParticipationRequest request = new ParticipationRequest();
//        request.setCreated(LocalDateTime.now());
//        request.setEvent(findEvent.get());
//        request.setRequester(findUser.get());
    }

    @Override
    public EventRequestStatusUpdateResul changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventrequest) {
        if (eventId == null) {
            throw new RuntimeException("Id события нет.");
        }

        if (userId == null) {
            throw new RuntimeException("Id пользователя нет.");
        }

        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<Event> findEvent = eventRepository.findByIdAndInitiator_Id(eventId, userId);
        if (findEvent.isEmpty()) {
            throw new NotFoundUserException("Событие с id " + eventId + "нет.");
        }

        if (findEvent.get().getParticipantLimit() == 0 || findEvent.get().isRequestModeration() == false) {
            throw new RuntimeException("Подтверждение заявок не требуется");
        }

        List<ParticipationRequest> listRequests = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (listRequests.size() >= findEvent.get().getParticipantLimit()) {
            throw new ConflictException("The participant limit has been reached");
        }

        RequestStatus status = eventrequest.getStatus();

        List<ParticipationRequestDto> list = eventrequest.getRequestIds().stream()
                .map(id -> requestRepository.findById(id)).map(request -> {
            if (request.isEmpty()) {
                throw new NotFoundUserException("Такой заявки нет");
            } else {
                return request.get();
            }
        }).filter( r -> r.getStatus().equals(RequestStatus.PENDING)).peek(r -> {
            r.setStatus(status);
            requestRepository.save(r);
        }).map(request1 -> RequestMapper.mapToRequestDtoFromRequest(request1)).toList();

        if (status.equals(RequestStatus.CONFIRMED)) {
            return new EventRequestStatusUpdateResul(new ArrayList<>(list), Collections.emptyList());
        } else if (status.equals(RequestStatus.REJECTED)) {
            return new EventRequestStatusUpdateResul(Collections.emptyList(), new ArrayList<>(list));
        } else {
            throw new BadRequestException("Cannot update request status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventFullDto> getEvents_2(List<Long> userIds, List<String> states, List<Long> categoryIds,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("eventDate"));

        Page<Event> requestsPage = eventRepository.findByInitiator_IdInAndEventDateBetweenAndCategory_IdInAndStateIn(userIds, rangeStart, rangeEnd, categoryIds, states, pageable);

        if (!requestsPage.getContent().isEmpty()) {
            List<EventFullDto> content = requestsPage.getContent().stream()
                    .map(event -> EventMapper.mapToFullEventDtoFormEvent(event))
                    .sorted(Comparator.comparing(EventFullDto::getEventDate))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        } else {
            return new PageImpl<>(
                    Collections.emptyList(),
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        }
    }

    @Override
    public EventFullDto updateEvent_1(Long eventId, UpdateEventAdminRequest request) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new NotFoundUserException("События с id " + eventId + " нет");
        }

        if (request.getStateAction().equals(StateAction.CANCEL_REVIEW) && findEvent.get().getState().equals(State.PUBLISHED)) {
            throw new RequestConditionsException("Событие не удовлетворяет правилам редактирования");
        }

        Event event = EventMapper.mapToEventFromUpdateEventAdmin(findEvent.get(), request);
        if (event.getEventDate().isAfter(event.getPublishedOn().plusHours(1))) {
            return EventMapper.mapToFullEventDtoFormEvent(eventRepository.save(event));
        } else {
            throw new RequestConditionsException("Событие не удовлетворяет правилам редактирования");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventShortDto> getEvents_1(String text, List<Long> categoryIds, boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        switch (sort.toLowerCase()) {
            case "event_date":
                Pageable pageable = PageRequest.of(from, size, Sort.by("eventDate"));
                if (rangeStart == null && rangeEnd == null) {
                    Page<Event> requestsPage = eventRepository.findByAfterNowEvent(text, categoryIds, paid, LocalDateTime.now(), pageable);

                    if (!requestsPage.getContent().isEmpty()) {
                        List<EventShortDto> content = requestsPage.getContent().stream()
                                .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                                .collect(Collectors.toList());

                        return new PageImpl<>(
                                content,
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    } else {
                        return new PageImpl<>(
                                Collections.emptyList(),
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    }
                } else {
                    Page<Event> requestsPage = eventRepository.findByDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategory_IdInAndPaidAndEventDateBetweenAndConfirmedRequestsLessThan(text, categoryIds, paid, rangeStart, rangeEnd, pageable);

                    if (!requestsPage.getContent().isEmpty()) {
                        List<EventShortDto> content = requestsPage.getContent().stream()
                                .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                                .collect(Collectors.toList());

                        return new PageImpl<>(
                                content,
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    } else {
                        return new PageImpl<>(
                                Collections.emptyList(),
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    }
                }

            case "views":
                Pageable pageable1 = PageRequest.of(from, size, Sort.by("views"));
                if (rangeStart == null && rangeEnd == null) {
                    Page<Event> requestsPage = eventRepository.findByAfterNowEvent(text, categoryIds, paid, LocalDateTime.now(), pageable1);

                    if (!requestsPage.getContent().isEmpty()) {
                        List<EventShortDto> content = requestsPage.getContent().stream()
                                .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());

                        return new PageImpl<>(
                                content,
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    } else {
                        return new PageImpl<>(
                                Collections.emptyList(),
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    }
                } else {
                    Page<Event> requestsPage = eventRepository.findByDescriptionContainingIgnoreCaseOrAnnotationContainingIgnoreCaseAndCategory_IdInAndPaidAndEventDateBetweenAndConfirmedRequestsLessThan(text, categoryIds, paid, rangeStart, rangeEnd, pageable1);

                    if (!requestsPage.getContent().isEmpty()) {
                        List<EventShortDto> content = requestsPage.getContent().stream()
                                .map(event -> EventMapper.mapToEventShortDtoFromEvent(event))
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());

                        return new PageImpl<>(
                                content,
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    } else {
                        return new PageImpl<>(
                                Collections.emptyList(),
                                requestsPage.getPageable(),
                                requestsPage.getTotalElements()
                        );
                    }
                }
            default: throw new BadRequestException("Incorrectly made request.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent_1(Long eventId) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new NotFoundUserException("События с id " + eventId + " нет");
        }

        if (findEvent.get().getState().equals(State.PUBLISHED)) {
            return EventMapper.mapToFullEventDtoFormEvent(findEvent.get());
        } else {
            throw new BadRequestException("Событие не опубликовано.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

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
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (findEvent.isEmpty()) {
            throw new NotFoundUserException("Событие с id " + userId + "нет.");
        }

        List<ParticipationRequest> findRequests = requestRepository.findByEvent_IdAndRequester_Id(eventId, userId);

        if (findRequests.isEmpty() || findEvent.get().getInitiator().equals(findUser.get())
                || findEvent.get().getState().equals(State.PENDING) || findEvent.get().getState().equals(State.CANCELED)
                || findEvent.get().getConfirmedRequests() >= findEvent.get().getParticipantLimit()) {
            throw new ConflictException("Integrity constraint has been violated.");
        }

        if (findEvent.get().isRequestModeration() == false) {
            ParticipationRequest request = new ParticipationRequest();
            request.setEvent(findEvent.get());
            request.setRequester(findUser.get());
            request.setStatus(RequestStatus.CONFIRMED);
            request.setCreated(LocalDateTime.now());

            return RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
        } else {
            ParticipationRequest request = new ParticipationRequest();
            request.setEvent(findEvent.get());
            request.setRequester(findUser.get());
            request.setStatus(RequestStatus.PENDING);
            request.setCreated(LocalDateTime.now());
            return RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(request));
        }
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (findUser.isEmpty()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        }

        Optional<ParticipationRequest> findRequest = requestRepository.findById(requestId);
        if (findRequest.isEmpty()) {
            throw new NotFoundUserException("Заявки с id " + userId + "нет.");
        }

        if (findRequest.get().getRequester().equals(findUser)) {
            findRequest.get().setStatus(RequestStatus.REJECTED);
            return RequestMapper.mapToRequestDtoFromRequest(requestRepository.save(findRequest.get()));
        } else {
            throw new ConflictException("Пользователь не является автором заявки.");
        }
    }
}