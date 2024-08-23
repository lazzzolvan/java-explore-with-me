package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .category((CategoryMapper.toDto(event.getCategory())))
                .confirmedRequests(Objects.isNull(event.getConfirmedRequests()) ? 0 : event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationDto.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .confirmedRequests(0)
                .annotation(newEventDto.getAnnotation())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto shortDto = new EventShortDto();
        shortDto.setId(event.getId());
        shortDto.setAnnotation(event.getAnnotation());
        shortDto.setCategory(CategoryMapper.toDto(event.getCategory()));
        shortDto.setConfirmedRequests(event.getConfirmedRequests());
        shortDto.setEventDate(event.getEventDate());
        shortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        shortDto.setPaid(event.getPaid());
        shortDto.setTitle(event.getTitle());
        shortDto.setViews(event.getViews());

        return shortDto;
    }

    public static List<EventFullDto> toDtoList(List<Event> eventList) {
        return eventList.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    public static List<String> toUrls(List<Event> events) {
        List<String> urls = new ArrayList<>();
        for (Event event : events) {
            urls.add("/events/" + event.getId());
        }
        return urls;
    }

    public static void updateEventFromRequest(Event event, UpdateEventAdminRequest request, Category category) {
        Optional.ofNullable(request.getAnnotation()).ifPresent(event::setAnnotation);
        event.setCategory(category);
        Optional.ofNullable(request.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(request.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(request.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(request.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(request.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(request.getTitle()).ifPresent(event::setTitle);

    }

}
