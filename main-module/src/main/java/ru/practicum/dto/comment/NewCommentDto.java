package ru.practicum.dto.comment;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCommentDto {

    @NotNull
    private Long autorId;
    @NotNull
    private Long eventId;

    @NotBlank
    @Size(max = 1000)
    private String text;

}
