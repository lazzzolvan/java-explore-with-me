package ru.practicum.controller.publics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/comments")
public class PublicCommentController {

    private final CommentService commentService;


    @GetMapping("/{eventId}")
    public List<CommentDto> getCommentByCommentId(@PathVariable Long eventId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Private: Получение комментариев к ивенту с ID = {}.", eventId);
        Pageable pageRequest = PageRequest.of(page, size);
        return commentService.getComments(eventId, pageRequest);
    }
}
