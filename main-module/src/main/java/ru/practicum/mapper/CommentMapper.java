package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.comment.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserShortDto(comment.getAuthorId()))
                .edited(comment.getUpdatedAt())
                .created(comment.getCreatedAt())
                .text(comment.getText())
                .eventId(comment.getId())
                .build();
    }

}
