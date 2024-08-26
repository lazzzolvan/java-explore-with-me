package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.dto.location.LocationDto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000, message = "Размер аннотации должен быть от 20 до 2000 символов.")
    private String annotation;

    @NotNull
    @PositiveOrZero
    private Long category;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000, message = "Размер описания должен быть я от 20 до 7000 символов.")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "Дата не может быть в прошлом")
    private LocalDateTime eventDate;

    private LocationDto location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit;

    private boolean requestModeration = true;

    @NotBlank(message = "Заголовок должен быть пустым")
    @Size(min = 3, max = 120, message = "Размер заголовка должен быть от 3-х до 120 символов.")
    private String title;

}
