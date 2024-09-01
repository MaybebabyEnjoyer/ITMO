package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/** @noinspection unused*/
public abstract class Page {
    protected final ArticleService articleService = new ArticleService();
    protected final UserService userService = new UserService();
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
        // No operations.
    }

    protected void setMessage(String message) {
        getSession().setAttribute("message", message);
    }

    protected void setNotAuthorizedMessage() {
        setMessage("You are not authorized");
        throw new RedirectException("/index");
    }

    protected User getUser() {
        return (User) getSession().getAttribute("user");
    }

    protected void removeUser() {
        getSession().removeAttribute("user");
    }

    protected void setUser(User user) {
        getSession().setAttribute("user", user);
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
