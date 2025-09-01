package ru.practicum.mappers;

import ru.practicum.dto.enums.State;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.dto.event.*;
import ru.practicum.entity.Event;
import ru.practicum.entity.Location;

public class EventMapper {
    public static EventFullDto mapToFullEventDtoFormEvent(Event event) {
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setInitiator(UserMapper.mapToShortDtoFromUser(event.getInitiator()));//
        dto.setPaid(event.isPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setTitle(event.getTitle());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.isRequestModeration());
        dto.setState(event.getState());

        return dto;
    }

    public static Event mapToEventFromNewRequest(NewEventRequest newEventRequest) {
        Event event = new Event();
        event.setAnnotation(newEventRequest.getAnnotation());
        event.setDescription(newEventRequest.getDescription());
        event.setEventDate(newEventRequest.getEventDate());
        event.setPaid(newEventRequest.isPaid());
        event.setParticipantLimit(newEventRequest.getParticipantLimit());
        event.setRequestModeration(newEventRequest.isRequestModeration());
        event.setTitle(newEventRequest.getTitle());
        event.setState(newEventRequest.getState());
        return event;
    }

    public static EventShortDto mapToEventShortDtoFromEvent(Event event) {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(event.getId());
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setEventDate(event.getEventDate());
        shortDto.setInitiator(UserMapper.mapToShortDtoFromUser(event.getInitiator()));
        shortDto.setPaid(event.isPaid());
        shortDto.setParticipantLimit(event.getParticipantLimit());
        shortDto.setTitle(event.getTitle());
        shortDto.setViews(event.getViews());
        return shortDto;
    }

    public static Event mapToEventFromUpdateEvent(Event event, UpdateEventUserRequest request) {
        if (!(request.getAnnotation() == null || request.getAnnotation().isBlank())) {
            event.setAnnotation(request.getAnnotation());
        }

        if (!(request.getDescription() == null || request.getDescription().isBlank())) {
            event.setDescription(request.getDescription());
        }

        if (!(request.getEventDate() == null)) {
            event.setEventDate(request.getEventDate());
        }

        if (event.isPaid() == false && request.isPaid() == true) {
            event.setPaid(request.isPaid());
        }

        if (!(request.getParticipantLimit() == null)) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (event.isRequestModeration() == false && request.isRequestModeration() == true) {
            event.setRequestModeration(request.isRequestModeration());
        }

        if (!(request.getTitle() == null || request.getTitle().isBlank())) {
            event.setTitle(request.getTitle());
        }

        if (request.getStateAction() != null) {

            if (request.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }

            if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }

            if (request.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
            }

            if (request.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }

        if (request.getLocation() != null) {
            if (request.getLocation().getLat() != 0 && request.getLocation().getLon() != 0) {
                Location location = new Location();
                location.setLat(request.getLocation().getLat());
                location.setLon(request.getLocation().getLon());
                event.setLocation(location);
            }
        }

        return event;
    }

    public static Event mapToEventFromUpdateEventAdmin(Event event, UpdateEventAdminRequest request) {
        if (!(request.getAnnotation() == null || request.getAnnotation().isBlank())) {
            event.setAnnotation(request.getAnnotation());
        }

        if (!(request.getDescription() == null || request.getDescription().isBlank())) {
            event.setDescription(request.getDescription());
        }

        if (!(request.getEventDate() == null)) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getPaid() != null) {
            if (request.getPaid()) {
                event.setPaid(true);
            } else {
                event.setPaid(false);
            }
        }

        if (!(request.getParticipantLimit() == null)) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (event.isRequestModeration() == false && request.isRequestModeration() == true) {
            event.setRequestModeration(request.isRequestModeration());
        }

        if (!(request.getTitle() == null || request.getTitle().isBlank())) {
            event.setTitle(request.getTitle());
        }

        if (request.getStateAction() != null) {

            if (request.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                event.setState(State.CANCELED);
            }

            if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                event.setState(State.PENDING);
            }

            if (request.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
            }

            if (request.getStateAction().equals(StateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }

        if (request.getLocation() != null) {
            if (request.getLocation().getLat() != 0 && request.getLocation().getLon() != 0) {
                Location location = new Location();
                location.setLat(request.getLocation().getLat());
                location.setLon(request.getLocation().getLon());
                event.setLocation(location);
            }
        }

        return event;
    }
}