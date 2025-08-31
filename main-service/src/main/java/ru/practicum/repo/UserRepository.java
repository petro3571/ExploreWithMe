package ru.practicum.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long userId);

    Page<User> findAll(Pageable pageable);

    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u.* FROM users u " +
            "WHERE (:userIds IS NULL OR u.id IN :userIds) " +
            "AND u.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<User> findByParam(@Param("userIds") List<Long> userIds,
                            @Param("from") int from,
                            @Param("size") int size);

    @Query(value = "SELECT u.* FROM users u " +
            "WHERE u.id > :from " +
            "LIMIT :size",
            nativeQuery = true)
    List<User> findByParamFromAndSize(@Param("from") int from,
                                      @Param("size") int size);
}