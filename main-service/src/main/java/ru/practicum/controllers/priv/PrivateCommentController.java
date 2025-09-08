package ru.practicum.controllers.priv;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.services.comment.CommentService;

@RestController
@RequestMapping(path = "/users/{userId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable @Min(1) Long userId,
                                  @PathVariable @Min(1) Long eventId,
                                  @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.postComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/{commentId}")
    public CommentDto patchComment(@PathVariable @Min(1) Long userId,
                                   @PathVariable @Min(1) Long eventId,
                                   @PathVariable @Min(1) Long commentId,
                                   @RequestBody @Valid NewCommentDto updateCommentDto) {
        return commentService.patchComment(userId, eventId, commentId, updateCommentDto);
    }

    @GetMapping("/{eventId}/{commentId}")
    public CommentDto getComment(@PathVariable @Min(1) Long userId,
                                 @PathVariable @Min(1) Long eventId,
                                 @PathVariable @Min(1) Long commentId) {
        return commentService.getComment(userId, eventId, commentId);
    }

    @DeleteMapping("/{eventId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Min(1) Long userId,
                              @PathVariable @Min(1) Long eventId,
                              @PathVariable @Min(1) Long commentId) {
        commentService.deleteComment(userId, eventId, commentId);
    }
}