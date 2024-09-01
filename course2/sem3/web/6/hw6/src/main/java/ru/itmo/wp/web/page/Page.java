package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.EventService;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public abstract class Page {
    protected final UserService userService = new UserService();
    protected final EventService eventService = new EventService();
    protected final TalkService talkService = new TalkService();

    private static HttpServletRequest pageRequest;

    protected void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    protected void before(HttpServletRequest request, Map<String, Object> view) {
        pageRequest = request;
        putUser(view);
        putMessage(view);
    }

    protected void after(HttpServletRequest request, Map<String, Object> view) {
        view.put("userCount", userService.findCount());
    }

    protected void setMessage(String message) {
        getSession().setAttribute("message", message);
    }

    protected User getUser() {
        return (User) getSession().getAttribute("user");
    }

    protected void setUser(User user) {
        getSession().setAttribute("user", user);
    }

    protected void removeUser() {
        getSession().removeAttribute("user");
    }

    private HttpSession getSession() {
        return pageRequest.getSession();
    }

    private void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }
    }

    private void putMessage(Map<String, Object> view) {
        String message = (String) getSession().getAttribute("message");
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            getSession().removeAttribute("message");
        }
    }
}
