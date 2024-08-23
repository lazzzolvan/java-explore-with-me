package ru.practicum.service.event;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;


public interface EventService {
    List<EventShortDto> getEventsByFilter(EventFilterDto eventFilterDto);

    EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getUserEventById(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getEventsByAdmin(EventAdminDto eventAdminDto);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateDto);

}
