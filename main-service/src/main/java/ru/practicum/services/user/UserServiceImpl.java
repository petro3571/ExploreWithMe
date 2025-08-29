package ru.practicum.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repo.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto registerUser(NewUserRequest request) {
        User user = userRepository.save(UserMapper.mapToUserFromNewRequest(request));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundUserException("User with id = " + userId + " was not found");
        } else {
            userRepository.delete(user.get());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("id"));

        if (ids != null) {

            Page<User> requestsPage = userRepository.findAllByIdIn(ids, pageable);

            List<UserDto> content = requestsPage.getContent().stream()
                    .map(user -> UserMapper.mapToUserDto(user))
                    .sorted(Comparator.comparing(UserDto::getId))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        } else {
            Page<User> requestsPage = userRepository.findAll(pageable);

            List<UserDto> content = requestsPage.getContent().stream()
                    .map(user -> UserMapper.mapToUserDto(user))
                    .sorted(Comparator.comparing(UserDto::getId))
                    .collect(Collectors.toList());

            return new PageImpl<>(
                    content,
                    requestsPage.getPageable(),
                    requestsPage.getTotalElements()
            );
        }
    }
}