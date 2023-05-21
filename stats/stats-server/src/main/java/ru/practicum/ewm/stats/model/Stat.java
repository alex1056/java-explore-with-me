package ru.practicum.ewm.stats.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Stat {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime created;

    public String getApp() {
        return app != null ? app.trim() : app;
    }

    public String getUri() {
        return uri != null ? uri.trim() : uri;
    }

    public String getIp() {
        return ip != null ? ip.trim() : ip;
    }
}
