package ru.practicum.ewm.main.dto.locationAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.dto.location.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewLocationAdminDto {
    @NotNull
    @NotEmpty
    @NotBlank
    @Size(min = 1, message = "Мин длина 1 символ")
    @Size(max = 50, message = "Макс длина 50 символов")
    private String name;
    @NotNull(message = "Должна быть локация")
    private LocationDto location;
}
