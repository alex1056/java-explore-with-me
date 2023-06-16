package ru.practicum.ewm.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "public")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String name;

    public String getName() {
        return name != null ? name.trim() : null;
    }

    public String getEmail() {
        if (email != null) return email.trim();
        return null;
    }
}
