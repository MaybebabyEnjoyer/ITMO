package ru.itmo.wp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.itmo.wp.domain.Notice;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.service.NoticeService;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

public class Page {
    private static final String USER_ID_SESSION_KEY = "userId";
    private static final String MESSAGE_SESSION_KEY = "message";

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @ModelAttribute("user")
    public User getUser(HttpSession httpSession) {
        return userService.findById((Long) httpSession.getAttribute(USER_ID_SESSION_KEY));
    }

    protected void setUser(HttpSession httpSession, User user) {
        if (user != null) {
            httpSession.setAttribute(USER_ID_SESSION_KEY, user.getId());
        } else {
            unsetUser(httpSession);
        }
    }

    protected void unsetUser(HttpSession httpSession) {
        httpSession.removeAttribute(USER_ID_SESSION_KEY);
    }

    protected boolean checkUser(HttpSession httpSession) {
        User user = getUser(httpSession);
        if (user != null && user.isDisabled()) {
            unsetUser(httpSession);
            setMessage(httpSession, "You was disabled");
            return true;
        }
        return false;
    }

    @ModelAttribute("message")
    public String getMessage(HttpSession httpSession) {
        String message = (String) httpSession.getAttribute(MESSAGE_SESSION_KEY);
        httpSession.removeAttribute(MESSAGE_SESSION_KEY);
        return message;
    }

    protected void setMessage(HttpSession httpSession, String message) {
        httpSession.setAttribute(MESSAGE_SESSION_KEY, message);
    }

    @ModelAttribute("notices")
    public List<Notice> getNotices() {
        return noticeService.findAll();
    }
}
