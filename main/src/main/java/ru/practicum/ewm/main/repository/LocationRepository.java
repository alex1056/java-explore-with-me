package ru.practicum.ewm.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.main.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    @Query("select l from Location l " +
            "where l.name != null " +
            "order by l.id DESC "
    )
    List<Location> findAdminLocationAll();

    @Query("select l from Location l " +
            "where l.id = :id AND l.name != null")
    Optional<Location> findAdminLocationById(Long id);

}
