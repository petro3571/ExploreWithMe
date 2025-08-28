package ru.practicum.services.user;

import org.springframework.data.domain.Page;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(NewUserRequest request);

    void deleteUser(Long userId);

    Page<UserDto> getUsers(List<Long> ids, int from, int size);
}
