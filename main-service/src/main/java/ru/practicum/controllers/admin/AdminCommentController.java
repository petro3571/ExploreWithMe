package ru.practicum.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.services.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users/{userId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentsByAdmin(@PathVariable Long userId) {
        return commentService.getAllCommentsByAdmin(userId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long userId,
                                @PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(userId, commentId);
    }
}