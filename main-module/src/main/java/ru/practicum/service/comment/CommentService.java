package ru.practicum.service.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto getComment(Long commentId);

    CommentDto updateComment(CommentDto commentDto);

    void deleteComment(Long commentId);

    ResponseEntity<CommentDto> create(NewCommentDto newCommentDto);

    List<CommentDto> getComments(Long eventId, Pageable pageRequest);
}
