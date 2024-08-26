package ru.practicum.service.comment;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.comment.Comment;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;
import ru.practicum.repository.Comment.CommentRepository;
import ru.practicum.repository.event.EventRepository;
import ru.practicum.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    public ResponseEntity<CommentDto> create(NewCommentDto newCommentDto) {

        Optional<User> user = userRepository.findById(newCommentDto.getAutorId());
        Optional<Event> event = eventRepository.findById(newCommentDto.getEventId());

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь не найден по: id:" + newCommentDto.getAutorId());
        }

        if (event.isEmpty()) {
            throw new NotFoundException("Ивент не найден по: id:" + newCommentDto.getEventId());
        }

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEventId(event.get());
        comment.setAuthorId(user.get());

        commentRepository.save(comment);
        return null;
    }

    @Override
    public List<CommentDto> getComments(Long eventId, Pageable pageRequest) {
        return commentRepository.findCommentByEventId(eventId, pageRequest).stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с id:" + commentId));
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с id:" + commentId));

        return CommentMapper.toDto(comment);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new NotFoundException("Комментарий не найден с id:" + commentDto.getId()));

        comment.setText(commentDto.getText());
        commentRepository.save(comment);

        return CommentMapper.toDto(comment);
    }

    private User findUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден по: id:" + userId));
    }

    public Event findEventId(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено с id: " + eventId));
    }

}
