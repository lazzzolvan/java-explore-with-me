package ru.practicum.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EventSpecification {

    public static Specification<Event> hasStateIn(List<EventState> states) {
        return ((root, query, criteriaBuilder) -> {
            if (Objects.isNull(states) || states.isEmpty()) return null;
            return criteriaBuilder.in(root.get("state")).value(states);
        });
    }

    public static Specification<Event> hasCategoryIn(List<Long> categories) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(categories) || categories.isEmpty()) return null;
            return root.get("category").get("id").in(categories);
        }));
    }

    public static Specification<Event> hasStartDate(LocalDateTime start) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(start)) return null;
            return criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), start);
        }));
    }

    public static Specification<Event> hasEndDate(LocalDateTime end) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(end)) return null;
            return criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), end);
        }));
    }

    public static Specification<Event> hasPaidIn(Boolean paid) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(paid)) return null;
            return criteriaBuilder.equal(root.get("paid"), paid);
        }));
    }

    public static Specification<Event> hasTextIn(String text) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(text) || !text.isBlank()) return null;
            String searchWord = "%" + text.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), searchWord),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchWord));
        }));
    }

    public static Specification<Event> hasAvailableIn(boolean available) {
        return (((root, query, criteriaBuilder) -> {
            if (available) {
                return criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("participantLimit"), 0),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), root.get("confirmedRequests")));
            }
            ;
            return criteriaBuilder.and(
                    criteriaBuilder.greaterThan(root.get("participantLimit"), 0),
                    criteriaBuilder.greaterThan(root.get("confirmedRequests"), root.get("participantLimit")));

        }));
    }

    public static Specification<Event> hasIdIn(Long id) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(id)) return null;
            return criteriaBuilder.equal(root.get("id"), id);
        }));
    }

    public static Specification<Event> hasUserIn(List<Long> userIds) {
        return (((root, query, criteriaBuilder) -> {
            if (Objects.isNull(userIds) || userIds.isEmpty()) return null;
            return root.get("initiator").get("id").in(userIds);
        }));
    }
}
