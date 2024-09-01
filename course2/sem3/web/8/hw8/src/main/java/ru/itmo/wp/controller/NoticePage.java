package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.form.Text;
import ru.itmo.wp.form.validator.TextNoticeValidator;
import ru.itmo.wp.service.NoticeService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class NoticePage extends Page {
    private final NoticeService noticeService;
    private final TextNoticeValidator textNoticeValidator;

    public NoticePage(NoticeService noticeService, TextNoticeValidator textNoticeValidator) {
        this.noticeService = noticeService;
        this.textNoticeValidator = textNoticeValidator;
    }

    @InitBinder("noticeForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(textNoticeValidator);
    }

    @GetMapping("/notice")
    public String noticeGet(Model model, HttpSession httpSession) {
        if (checkUser(httpSession)) {
            return "redirect:/";
        }
        model.addAttribute("noticeForm", new Text());
        return "NoticePage";
    }

    @PostMapping("/notice")
    public String noticePost(@Valid @ModelAttribute("noticeForm") Text noticeForm,
                             BindingResult bindingResult,
                             HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "NoticePage";
        }

        if (checkUser(httpSession)) {
            return "redirect:/";
        }

        noticeService.add(noticeForm);
        setMessage(httpSession, "Your notice has been created");
        return "redirect:";
    }
}
