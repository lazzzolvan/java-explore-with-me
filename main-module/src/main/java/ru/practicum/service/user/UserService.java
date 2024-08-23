package ru.practicum.service.user;

import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findById(List<Long> ids, int from, int size);

    UserDto create(UserDto userDto);

    void delete(Long userId);
}
