package ru.itmo.wp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.wp.domain.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    int countByLogin(String login);

    @Transactional
    @Modifying
    @Query(
            value = "UPDATE user SET passwordSha=SHA1(CONCAT('abfb33e9f6ccc', ?2, ?3)) WHERE id=?1",
            nativeQuery = true
    )
    void updatePassword(long id, String login, String password);

    @Query(
            value = "UPDATE user SET disabled=?2 WHERE id=?1",
            nativeQuery = true
    )
    void updateDisabled(long id, boolean status);

    @Query(
            value = "SELECT * FROM user WHERE login=?1 AND passwordSha=SHA1(CONCAT('abfb33e9f6ccc', ?1, ?2))",
            nativeQuery = true
    )
    User findByLoginAndPassword(String login, String password);

    List<User> findAllByOrderByIdDesc();
}
