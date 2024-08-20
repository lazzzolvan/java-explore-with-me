package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class HitDto {

    private Long id;

    @NotBlank(message = "Идентификатор сервиса не может быть пустым")
    private String app;

    @NotBlank(message = "URI запроса не может быть пустым")
    private String uri;

    @NotEmpty(message = "IP-адрес пользователя не может быть пустым")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Дата и время запроса не может быть пустыми")
    private LocalDateTime timestamp;
}