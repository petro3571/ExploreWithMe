package ru.practicum.services.comment;

import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto postComment(Long userId,
                           Long eventId,
                           NewCommentDto newCommentDto);

    CommentDto patchComment(Long userId,
                            Long eventId,
                            Long commentId,
                            NewCommentDto updateCommentDto);

    CommentDto getComment(Long userId,
                                 Long eventId,
                                 Long commentId);

    void deleteComment(Long userId,
                              Long eventId,
                              Long commentId);

    List<CommentDto> getAllCommentsForEvent(Long eventId);

    List<CommentDto> getAllComments_1(Long userId);

    void deleteComment_1(Long userId,
                         Long commentId);
}