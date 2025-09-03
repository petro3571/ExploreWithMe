package ru.practicum.controllers.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.services.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping(path = "/events/{eventId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllCommentsForEvent(@PathVariable Long eventId) {
        return commentService.getAllCommentsForEvent(eventId);
    }
}