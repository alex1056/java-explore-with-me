package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micrometer.core.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.annotations.TwoHoursSafe;
import ru.practicum.ewm.main.dto.location.LocationDto;
import ru.practicum.ewm.main.model.event.StateActionUser;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {
    @Nullable
    @Size(min = 20, message = "Мин длина 20 символов")
    @Size(max = 2000, message = "Макс длина 2000 символов")
    private String annotation;
    @Nullable
    private Long category;
    @Nullable
    @Size(min = 20, message = "Мин длина 20 символов")
    @Size(max = 7000, message = "Макс длина 7000 символов")
    private String description;
    @Nullable
    @Size(min = 3, message = "Мин длина 3 символ")
    @Size(max = 120, message = "Макс длина 120 символ")
    private String title;
    @Nullable
    @TwoHoursSafe
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionUser stateAction;
}
