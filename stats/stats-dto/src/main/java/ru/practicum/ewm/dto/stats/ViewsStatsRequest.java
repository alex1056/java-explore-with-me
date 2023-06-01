package ru.practicum.ewm.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewsStatsRequest {
    @NotNull
    @NotEmpty
    private LocalDateTime start;
    @NotNull
    @NotEmpty
    private LocalDateTime end;
    private Boolean unique;
    private List<String> uris;
}

