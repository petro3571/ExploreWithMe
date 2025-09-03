package ru.practicum.services.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.enums.State;
import ru.practicum.entity.Comment;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.repo.CommentRepository;
import ru.practicum.repo.EventRepository;
import ru.practicum.repo.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public CommentDto postComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = findUserMethod(userId).get();
        Event event = findEventMethod(eventId).get();
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundUserException("События с id = " + eventId + " нет");
        }
        Comment comment = CommentMapper.mapToCommentFromNewRequest(newCommentDto);
        comment.setUser(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto patchComment(Long userId, Long eventId, Long commentId, NewCommentDto updateCommentDto) {
        Comment oldComment = findCommentMethod(commentId).get();
        if (userId != oldComment.getUser().getId()) {
            throw new BadRequestException("Только автор комментария может обновлять комментарий.");
        }
        if (eventId != oldComment.getEvent().getId()) {
            throw new NotFoundUserException("Комментария с id = " + commentId + " для события с id = " + eventId + " нет.");
        }
        oldComment.setText(updateCommentDto.getText());
        return CommentMapper.mapToCommentDto(commentRepository.save(oldComment));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getComment(Long userId, Long eventId, Long commentId) {
        Comment comment = findCommentMethod(commentId).get();
        if (userId != comment.getUser().getId()) {
            throw new BadRequestException("Только автор комментария может обновлять комментарий.");
        }
        if (eventId != comment.getEvent().getId()) {
            throw new NotFoundUserException("Комментария с id = " + commentId + " для события с id = " + eventId + " нет.");
        }
        return CommentMapper.mapToCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        Comment comment = findCommentMethod(commentId).get();
        if (userId != comment.getUser().getId()) {
            throw new BadRequestException("Only author can delete comment.");
        }
        if (eventId != comment.getEvent().getId()) {
            throw new NotFoundUserException("No comment with id = " + commentId + " for event with id = " + eventId);
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsForEvent(Long eventId) {
        Event event = findEventMethod(eventId).get();
        List<Comment> comments = commentRepository.findByEventId(eventId);
        return comments.stream().map(comment -> CommentMapper.mapToCommentDto(comment)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments_1(Long userId) {
        User user = findUserMethod(userId).get();
        List<Comment> comments = commentRepository.findByUserId(userId);
        return comments.stream().map(comment -> CommentMapper.mapToCommentDto(comment)).toList();
    }

    @Override
    public void deleteComment_1(Long userId, Long commentId) {
        Comment comment = findCommentMethod(commentId).get();
        if (userId != comment.getUser().getId()) {
            throw new NotFoundUserException("Комментария с id = " + commentId + " и автором с id = " + userId + " нет.");
        }
        commentRepository.deleteById(commentId);
    }

    private Optional<User> findUserMethod(Long userId) {
        Optional<User> findUser = userRepository.findById(userId);
        if (!findUser.isPresent()) {
            throw new NotFoundUserException("Пользователь с id " + userId + "не зарегистрирован.");
        } else {
            return findUser;
        }
    }

    private Optional<Event> findEventMethod(Long eventId) {
        Optional<Event> findEvent = eventRepository.findById(eventId);
        if (!findEvent.isPresent()) {
            throw new NotFoundUserException("Событие с id " + eventId + " нет.");
        } else {
            return findEvent;
        }
    }

    private Optional<Comment> findCommentMethod(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            throw new NotFoundUserException("Комментария с id " + commentId + " нет.");
        } else {
            return comment;
        }
    }
}