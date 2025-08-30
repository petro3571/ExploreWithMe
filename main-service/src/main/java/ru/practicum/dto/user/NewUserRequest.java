package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank
    @Size(min = 2, max = 250, message = "Annotation must be between 20 and 2000 characters")
    private String name;
    @NotBlank
    @Email
    @Size(min = 6, max = 254, message = "Annotation must be between 20 and 2000 characters")
    private String email;
}
