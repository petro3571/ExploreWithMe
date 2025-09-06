package ru.practicum.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.entity.User;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundUserException;
import ru.practicum.mappers.UserMapper;
import ru.practicum.repo.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto registerUser(NewUserRequest request) {
        Optional<User> findUserSameEmail = userRepository.findByEmail(request.getEmail());
        if (findUserSameEmail.isPresent()) {
            throw new ConflictException("Пользователь с почтой " + request.getEmail() + " уже  зарегистрирован.");
        }

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
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids != null) {
            List<User> findUsers = userRepository.findByParam(ids, from, size);
            if (findUsers.isEmpty()) {
                return Collections.emptyList();
            } else {
                return findUsers.stream().map(u -> UserMapper.mapToUserDto(u)).toList();
            }
        } else {
            List<User> findUsers = userRepository.findByParamFromAndSize(from, size);
            if (findUsers.isEmpty()) {
                return Collections.emptyList();
            } else {
                return findUsers.stream().map(u -> UserMapper.mapToUserDto(u)).toList();
            }
        }
    }
}