package ru.practicum.dto.user;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class UserShortDto {

    private Long id;
    private String name;
}
