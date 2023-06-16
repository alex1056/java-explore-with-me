package ru.practicum.ewm.main.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.dto.location.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotNull(message = "Должна быть аннотация")
    @Size(min = 20, message = "Мин длина 20 символов")
    @Size(max = 2000, message = "Макс длина 2000 символов")
    private String annotation;
    @NotNull(message = "Должна быть категория")
    private Long category;
    @NotNull(message = "Должна быть аннотация")
    @Size(min = 20, message = "Мин длина 20 символов")
    @Size(max = 7000, message = "Макс длина 7000 символов")
    private String description;

    @NotNull(message = "Должна быть аннотация")
    @Size(min = 3, message = "Мин длина 3 символ")
    @Size(max = 120, message = "Макс длина 120 символ")
    private String title;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Должна быть локация")
    private LocationDto location;
    private Boolean paid; // default false
    private Integer participantLimit; // default 0
    private Boolean requestModeration; // default false
}
