package ru.practicum.controllers.priv;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.services.comment.CommentService;

@RestController
@RequestMapping(path = "/users/{userId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable Long userId,
                                  @PathVariable Long eventId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.postComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/{commentId}")
    public CommentDto patchComment(@PathVariable Long userId,
                                   @PathVariable Long eventId,
                                   @PathVariable Long commentId,
                                   @RequestBody @Valid NewCommentDto updateCommentDto) {
        return commentService.patchComment(userId, eventId, commentId, updateCommentDto);
    }

    @GetMapping("/{eventId}/{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long commentId) {
        return commentService.getComment(userId, eventId, commentId);
    }

    @DeleteMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }
}