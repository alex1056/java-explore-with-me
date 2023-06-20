package ru.practicum.ewm.main.dto.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAdminDto {
    Long id;
    private Double lat;
    private Double lon;
    private String name;
    private String description;
    private Double radius;
}
