package ru.practicum.ewm.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location_admin", schema = "public")
public class LocationAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "la_id")
    private Long laId;
    private String name;
    @Column(name = "location_id")
    private Long locationId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", insertable = false, updatable = false)
    private Location location;

    public String getName() {
        return name != null ? name.trim() : null;
    }
}
