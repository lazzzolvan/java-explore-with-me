package ru.practicum.controller.privates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.comment.CommentService;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentDto> createNewComment(@RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("Private: Добавление нового комментария newComment = {}.", newCommentDto);
        return commentService.create(newCommentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getCommentByCommentId(@PathVariable Long commentId) {
        log.info("Private: Получение комментария с ID = {}.", commentId);
        return commentService.getComment(commentId);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        log.info("Private: Обновление комментария с ID = {}.", commentDto.getId());
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByCommentId(@PathVariable Long commentId) {
        log.info("Private: Удаление комментария с ID = {}.", commentId);
        commentService.deleteComment(commentId);
    }
}
