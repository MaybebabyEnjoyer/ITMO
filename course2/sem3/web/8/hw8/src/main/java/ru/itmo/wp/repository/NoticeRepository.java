package ru.itmo.wp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itmo.wp.domain.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
