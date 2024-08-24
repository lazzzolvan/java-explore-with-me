package ru.practicum.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.model.category.Category;
import ru.practicum.model.user.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false, length = 7000)
    @Size(min = 20, max = 7000)
    private String description;

    @Column(name = "event_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    private Float lat;

    private Float lon;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state", nullable = false)
    private EventState state;

    @Column(name = "title", nullable = false)
    private String title;

    @Builder.Default
    private Integer confirmedRequests = 0;

    @Builder.Default
    private Integer views = 0;

}
