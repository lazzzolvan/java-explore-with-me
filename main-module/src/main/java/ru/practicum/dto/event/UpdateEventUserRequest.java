package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.model.event.StateAction;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Размер анотации должен быть от 3-х до 120 символов.")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Размер описания должен быть от 3-х до 120 символов.")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Событие не может быть в прошлом")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    @Builder.Default
    private Boolean requestModeration = true;

    private StateAction stateAction;

    @Size(min = 3, max = 120, message = "Размер заголовка должен быть от 3-х до 120 символов.")
    private String title;
}
