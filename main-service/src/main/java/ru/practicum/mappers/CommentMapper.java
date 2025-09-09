package ru.practicum.mappers;

import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.entity.Comment;

public class CommentMapper {
    public static Comment mapToCommentFromNewRequest(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreated(comment.getCreated());
        dto.setAuthorName(comment.getUser().getName());
        return dto;
    }
}