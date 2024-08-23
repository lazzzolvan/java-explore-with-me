package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.user.User;
import ru.practicum.repository.User.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findById(List<Long> ids, int from, int size) {
        if (ids != null && !ids.isEmpty()) {
            return userRepository.findByIdIn(ids, PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(PageRequest.of(from, size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("User with this email already exists");
        }

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(findById(userId));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id=" + id + " was not found"));
    }

    private User findUserId(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id:" + userId));
    }
}
