package ru.practicum.ewm.main.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.main.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where id in :ids")
    Page<User> findUserByIds(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("select u from User u where upper(u.name) = upper(:newName)")
    Optional<User> checkUserName(@Param("newName") String newName);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    List<User> findAllByOrderByIdAsc();
}
