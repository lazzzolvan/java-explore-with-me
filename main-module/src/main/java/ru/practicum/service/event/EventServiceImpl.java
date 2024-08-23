package ru.practicum.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestCountDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.InvalidException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.event.StateAction;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.Categories.CategoriesRepository;
import ru.practicum.repository.Event.EventRepository;
import ru.practicum.repository.Request.RequestRepository;
import ru.practicum.repository.User.UserRepository;
import ru.practicum.specification.EventSpecification;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.model.event.EventState.CANCELED;
import static ru.practicum.model.event.EventState.PENDING;
import static ru.practicum.model.event.SortType.VIEWS;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final CategoriesRepository categoryRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<EventShortDto> getEventsByFilter(EventFilterDto filter) {

        LocalDateTime start = LocalDateTime.now().minusYears(10);
        LocalDateTime end = LocalDateTime.now().plusYears(100);

        if (Objects.nonNull(filter.getRangeStart()) && Objects.nonNull(filter.getRangeEnd())) {
            start = LocalDateTime.parse(filter.getRangeStart(), formatter);
            end = LocalDateTime.parse(filter.getRangeEnd(), formatter);
        }

        if (end.isBefore(start) || start.isEqual(end)) {
            throw new InvalidException("Не правильное время начала и конца");
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "eventDate");

        if (filter.getSort() == VIEWS) {
            sort = Sort.by(Sort.Direction.DESC, "views");
        }

        Pageable pageable = PageRequest.of(filter.getFrom(), filter.getSize(), sort);
        Specification<Event> specification = Specification.where(
                EventSpecification.hasStateIn(new ArrayList<>(EventState.PUBLISHED.ordinal()))
                        .and(EventSpecification.hasCategoryIn(filter.getCategories()))
                        .and(EventSpecification.hasStartDate(start))
                        .and(EventSpecification.hasEndDate(end))
                        .and(EventSpecification.hasPaidIn(filter.getPaid()))
                        .and(EventSpecification.hasTextIn(filter.getText()))
        );

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();

        List<StatsDto> statistics = statsClient.getStatistic(
                start,
                end,
                EventMapper.toUrls(events),
                Boolean.TRUE
        );

        Map<String, Integer> views = statistics.stream()
                .collect(Collectors.toMap(StatsDto::getUri, StatsDto::getHits));

        return events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
                    String url = "/events/" + event.getId();
                    eventShortDto.setViews(views.getOrDefault(url, 0));
                    return eventShortDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = findById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event not found with id: " + eventId);
        }

        EventFullDto eventDto = EventMapper.toEventFullDto(event);

        List<StatsDto> statistics = (List<StatsDto>) statsClient.getStatistic(
                LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100),
                List.of("/events/" + eventId),
                Boolean.TRUE
        );

        if (!statistics.isEmpty()) {
            eventDto.setViews(statistics.get(0).getHits());
        } else {
            eventDto.setViews(1);
        }

        return eventDto;

    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        User user =  findUserId(userId);
        List<Event> userEvents = eventRepository.findAllByInitiator(user, PageRequest.of(from, size));
        return userEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        if (newEventDto.getEventDate() != null && newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidException("Не правильное время начала и конца");
        }

        User user = findUserId(userId);
        Category category = findCategoryId(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setState(PENDING);
        event.setCreatedOn(LocalDateTime.now());

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = getEvent(userId, eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = getEvent(userId, eventId);

        if (event.getState() != EventState.CANCELED && event.getState() != PENDING) {
            throw new ConflictException("Событие с таким статусом не может быть измененно");
        }

        if (request.getEventDate() != null &&
                request.getEventDate().isBefore((LocalDateTime.now().plusHours(2)))) {
            throw new InvalidException("Не правильное время начала и конца");
        }

        if (request.getStateAction() != null) {
            if (request.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(PENDING);
            }
            if (request.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(CANCELED);
            }
        }
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsEvent(Long userId, Long eventId) {
        Event event = getEvent(userId, eventId);

        List<Request> eventRequests = requestRepository.findAllByEventId(event.getId());

        return eventRequests.stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {

        Event event = getEvent(userId, eventId);

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("");
        }

        List<Request> requests = requestRepository.findByIdIn(updateRequest.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request request : requests) {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
            switch (updateRequest.getStatus()) {
                case CANCELED:
                    request.setStatus(RequestStatus.CANCELED);
                    rejectedRequests.add(RequestMapper.toDto(request));
                    break;
                case REJECTED:
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toDto(request));
                    break;
                case CONFIRMED:
                    if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmedRequests.add(RequestMapper.toDto(request));
                    } else {
                        throw new ConflictException("Достигнут лимит заявок.");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный статус.");
            }
            requestRepository.save(request);
            eventRepository.save(event);
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private Event getEvent(Long userId, Long eventId) {
        User user = findUserId(userId);
        Specification<Event> specification = Specification.where(
                EventSpecification.hasIdIn(eventId)
                        .and(EventSpecification.hasUserIn(new ArrayList<>(Math.toIntExact(user.getId())))));

        return eventRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(EventAdminDto eventAdminDto) {

        LocalDateTime start;
        LocalDateTime end;
        start = (eventAdminDto.getRangeStart() == null) ? null : parseToLocalDateTime(eventAdminDto.getRangeStart());
        end = (eventAdminDto.getRangeEnd() == null) ? null : parseToLocalDateTime(eventAdminDto.getRangeEnd());

        if (Objects.nonNull(end) && Objects.nonNull(start)) {
            if (end.isBefore(start) || start.isEqual(end)) {
                throw new InvalidException("Не правильное время начала и конца");
            }
        }

        Pageable pageable = PageRequest.of(eventAdminDto.getFrom(), eventAdminDto.getSize());
        Specification<Event> specification = Specification.where(
                EventSpecification.hasStartDate(start)
                        .and(EventSpecification.hasEndDate(end))
                        .and(EventSpecification.hasStateIn(eventAdminDto.getStates()))
                        .and(EventSpecification.hasUserIn(eventAdminDto.getUsers()))
                        .and(EventSpecification.hasCategoryIn(eventAdminDto.getCategories()))
        );

        List<Event> events = eventRepository.findAll(specification, pageable).getContent();
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<RequestCountDto> requests = requestRepository.findRequestCountsByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED);

        Map<Long, Long> requestCountDtoMap = requests.stream()
                .collect(Collectors.toMap(RequestCountDto::getEventId, RequestCountDto::getRequestCount));

        List<StatsDto> stat = (List<StatsDto>) statsClient.getStatistic(LocalDateTime.now().minusYears(20), LocalDateTime.now().plusYears(100),
                EventMapper.toUrls(events), Boolean.TRUE);
        Map<String, StatsDto> statsMap = stat.stream()
                .collect(Collectors.toMap(StatsDto::getUri, statsHitDto -> statsHitDto, (l, r) -> l));

        for (Event event : events) {
            if (!requestCountDtoMap.isEmpty()) {
                if (requestCountDtoMap.containsKey(event.getId())) {
                    event.setConfirmedRequests(Math.toIntExact(requestCountDtoMap.get(event.getId())));
                }
            } else {
                event.setConfirmedRequests(0);
            }
        }

        for (Event event : events) {
            if (!statsMap.isEmpty()) {
                String uri = "/events/" + event.getId();
                if (statsMap.containsKey(uri)) {
                    event.setViews(statsMap.get(uri).getHits());
                }
            } else {
                event.setViews(0);
            }
        }
        System.out.println("Events = " + events);
        return events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = findById(eventId);
            Category category = getCategory(updateRequest, event);

            if (updateRequest.getEventDate() != null && updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Дата начала мероприятия не должна быть раньше, чем через час после даты публикации");
            }
            if ((event.getState() != EventState.PENDING) && (updateRequest.getStateAction() == StateAction.PUBLISH_EVENT)) {
                throw new ConflictException("Невозможно опубликовать мероприятие, так как оно не находится в состоянии: ОЖИДАЕТ ПУБЛИКАЦИИ");
            }

            if ((event.getState() == EventState.PUBLISHED) && (updateRequest.getStateAction() == StateAction.REJECT_EVENT)) {
                throw new ConflictException("Невозможно отклонить мероприятие, так как оно уже опубликовано");
            }

            handleStateAction(updateRequest, event);

            EventMapper.updateEventFromRequest(event, updateRequest, category);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private Category getCategory(UpdateEventAdminRequest updateRequest, Event event) {
        if (updateRequest.getCategory() != null) {
            return categoryRepository.findById(updateRequest.getCategory()).orElse(event.getCategory());
        }
        return event.getCategory();
    }

    private void handleStateAction(UpdateEventAdminRequest updateRequest, Event event) {
        if (updateRequest.getStateAction() != null) {

            switch (updateRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new IllegalArgumentException("Недопустимое действие с состоянием");
            }
        }
    }

    private Event findById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private User findUserId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id:" + userId));
    }

    private Category findCategoryId(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id:" + categoryId));
    }

    private LocalDateTime parseToLocalDateTime(String date) {

        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            throw new RuntimeException("Неверный формат даты: " + date);
        }
    }
}