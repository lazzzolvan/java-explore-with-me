package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {

    private Long id;
    private String annotation;
    private CategoryDto category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserShortDto initiator;
    private boolean paid;
    private String title;
    private Integer confirmedRequests;
    private Integer views;
}
