package ru.practicum.ewm.main.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequestDto {
    @NotNull(message = "У пользователя должна быть электронная почта")
    @Email(message = "Неправильный формат электронной почты")
    @Size(min = 6, message = "Мин длина 6 символов")
    @Size(max = 254, message = "Макс длина 254 символов")
    private String email;
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 2, message = "Мин длина 2 символа")
    @Size(max = 250, message = "Макс дли на 250 символов")
    private String name;
}
