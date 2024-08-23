package ru.practicum.dto.user;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым.")
    @Size(min = 2, max = 250, message = "Имя пользователя должено быть от 2 до 250 символов.")
    private String name;

    @Email(message = "email должен быть адресом эл. почты.")
    @NotEmpty(message = "email не должен быть пустым")
    @Size(min = 6, max = 254, message = "email пользователя должено быть от 6 до 254 символов.")
    private String email;
}
