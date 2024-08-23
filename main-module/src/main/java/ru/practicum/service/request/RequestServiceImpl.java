package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ViolationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.Event.EventRepository;
import ru.practicum.repository.Request.RequestRepository;
import ru.practicum.repository.User.UserRepository;
import ru.practicum.specification.EventSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(long userId) {
        User user = findUserId(userId);
        return requestRepository.findAllByRequester(user).stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        User user = findUserId(userId);
        Specification<Event> specification = Specification.where(
                EventSpecification.hasIdIn(eventId)
                        .and(EventSpecification.hasUserIn(new ArrayList<>(Math.toIntExact(user.getId())))));

        Event event = eventRepository.findOne(specification)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ViolationException("Инициатор не может делать запрос на принятие участия в своём событии.");
        }

        System.out.println("limit = " + event.getParticipantLimit());
        System.out.println("Confirmed = " + event.getConfirmedRequests());
        List<Request> requests = requestRepository.findAllByRequester(user);

        if (!requests.isEmpty()) {
            throw new ConflictException("Запросы не могут повторяться");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        int requestCount = requestRepository.findAllByEventId(eventId).size();
        if (requestCount >= event.getParticipantLimit() && event.getParticipantLimit() > 0) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setParticipantLimit(event.getParticipantLimit() + 1);
        } else {
            if (event.getParticipantLimit() == 0) {
                request.setStatus(RequestStatus.CONFIRMED);
            } else {
                request.setStatus(RequestStatus.PENDING);
            }
        }

        requestRepository.save(request);
        eventRepository.save(event);

        return RequestMapper.toDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, long requestId) {
        User user = findUserId(userId);
        Request request = findRequestId(requestId);
        if (!user.getId().equals(request.getRequester().getId())) {
            throw new NotFoundException("Доступ запрещен.");
        }

        if (!request.getStatus().equals(RequestStatus.CANCELED)) {
            request.setStatus(RequestStatus.CANCELED);
            requestRepository.save(request);
        }
        return RequestMapper.toDto(request);
    }

    private Event findById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + eventId));
    }

    private User findUserId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id:" + userId));
    }

    private Request findRequestId(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found with id:" + requestId));
    }
}

