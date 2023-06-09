package ru.practicum.ewm.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location", schema = "public")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double lat;
    private Double lon;
    private String name;
    private String description;
    private Double radius;

    public String getName() {
        return name != null ? name.trim() : null;
    }

    public String getDescription() {
        return description != null ? description.trim() : null;
    }
}
