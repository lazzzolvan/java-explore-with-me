package ru.practicum.dto.category;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Название категорий не может быть пустым")
    @Size(min = 1, max = 50, message = "Название категорий должен быть от 1 до 50 символов")
    private String name;
}
