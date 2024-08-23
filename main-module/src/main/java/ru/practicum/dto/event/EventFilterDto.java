package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.model.event.SortType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFilterDto {

    private HttpServletRequest httpServletRequest;
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private String rangeStart;
    private String rangeEnd;
    private boolean onlyAvailable;
    private SortType sort;
    @PositiveOrZero
    @Builder.Default
    private int from  = 0;
    @PositiveOrZero
    @Builder.Default
    private int size = 10;
}
