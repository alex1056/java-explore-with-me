package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.LocationAdmin;

import java.util.Optional;

public interface LocationAdminRepository extends JpaRepository<LocationAdmin, Long> {
    @Query("select l from LocationAdmin l " +
            "where l.id = :id")
    Optional<LocationAdmin> findLocationAdminById(Long id);
}
