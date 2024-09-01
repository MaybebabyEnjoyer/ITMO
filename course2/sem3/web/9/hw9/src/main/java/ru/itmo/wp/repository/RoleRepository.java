package ru.itmo.wp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.wp.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(Role.Name name);
}
