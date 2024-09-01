package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.Text;
import ru.itmo.wp.service.NoticeService;

@Component
public class TextNoticeValidator implements Validator {
    private final NoticeService noticeService;

    public TextNoticeValidator(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Text.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }
}
