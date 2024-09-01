package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class IndexPage extends Page {
    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("articles", articleService.findAllByHidden());
    }

    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        long id = Long.parseLong(request.getParameter("userId"));
        User user = userService.find(id);
        view.put("user", userService.find(id));
    }
}
