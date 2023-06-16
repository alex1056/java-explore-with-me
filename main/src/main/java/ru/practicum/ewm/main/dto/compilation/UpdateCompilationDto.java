package ru.practicum.ewm.main.dto.compilation;

import io.micrometer.core.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @Nullable
    @Size(min = 1, message = "Мин длина 1 символ")
    @Size(max = 50, message = "Макс длина 50 символов")
    private String title;
}
