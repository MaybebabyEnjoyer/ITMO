package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage extends Page {
    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        if (getUser() != null) {
            User user = userService.find(getUser().getId());
            getUser().setAdmin(user.getAdmin());
//            
        }
    }

    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user",
                userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    private void change(HttpServletRequest request, Map<String, Object> view) {
        long id = Long.parseLong(request.getParameter("id"));
        boolean admin = Boolean.parseBoolean(request.getParameter("boolValue"));
        User user = userService.find(getUser().getId());

        if (!user.getAdmin()) {
            setMessage("You are not allow to change the status");
            getUser().setAdmin(false);
            throw new RedirectException("/index");
        }

        userService.updateAdmin(id, !admin);
    }
}
