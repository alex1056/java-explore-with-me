package ru.practicum.ewm.main.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {
    @NotNull(message = "Должна быть lat")
    private Double lat;
    @NotNull(message = "Должна быть lon")
    private Double lon;
}
