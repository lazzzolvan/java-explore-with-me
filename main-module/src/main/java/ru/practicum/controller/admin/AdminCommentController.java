package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.CommentService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;


    @DeleteMapping("/{commentId}")
    public void deleteCommentByCommentId(@PathVariable Long commentId) {
        log.info("Private: Удаление комментария с ID = {}.", commentId);
        commentService.deleteComment(commentId);
    }


}