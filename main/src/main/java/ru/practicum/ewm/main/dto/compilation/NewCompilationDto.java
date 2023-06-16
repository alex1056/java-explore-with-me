package ru.practicum.ewm.main.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {

    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    @NotNull(message = "title должно быть заполнено")
    @NotEmpty(message = "title должно иметь значение")
    @Size(min = 1, message = "Мин длина 1 символ")
    @Size(max = 50, message = "Макс длина 50 символов")
    private String title;
}
