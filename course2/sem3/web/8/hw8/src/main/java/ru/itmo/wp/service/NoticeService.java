package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Notice;
import ru.itmo.wp.form.Text;
import ru.itmo.wp.repository.NoticeRepository;

import java.util.List;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public void add(Text text) {
        Notice notice = new Notice();
        notice.setContent(text.getText());
        noticeRepository.save(notice);
    }

    public List<Notice> findAll() {
        return noticeRepository.findAll();
    }
}
