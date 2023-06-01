package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.compilation.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("select cp from Compilation cp where cp.pinned = :pinned")
    Page<Compilation> findAllPinnedUnPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}
