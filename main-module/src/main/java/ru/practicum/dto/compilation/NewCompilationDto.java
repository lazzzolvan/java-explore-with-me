package ru.practicum.dto.compilation;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCompilationDto {

    private Long id;
    private List<Long> events;
    @Builder.Default
    private Boolean pinned = false;

    @NotBlank
    @Size(max = 50)
    private String title;
}
