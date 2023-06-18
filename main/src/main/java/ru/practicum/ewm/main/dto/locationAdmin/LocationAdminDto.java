package ru.practicum.ewm.main.dto.locationAdmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.dto.location.LocationDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationAdminDto {
    private Long id;
    private String name;
    private LocationDto location;
}
