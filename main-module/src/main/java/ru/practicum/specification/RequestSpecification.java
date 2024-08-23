package ru.practicum.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.List;

public class RequestSpecification {

    public static Specification<Request> findByEventIdsAndStatus(List<Long> ids, RequestStatus status) {
        return ((root, query, criteriaBuilder) -> {
            Predicate eventsIdsPredicate = root.get("event").get("id").in(ids);
            Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), status);

            query.groupBy(root.get("event").get("id"));
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(root.get("id"))));

            Expression<Long> eventId = root.get("event").get("id");
            Expression<Long> count = criteriaBuilder.count(root.get("id"));

            query.multiselect(eventId, count).distinct(true);

            return criteriaBuilder.and(eventsIdsPredicate, statusPredicate);
        });
    }
}

