package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TalksPage extends Page {
    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        User user = getUser();
        if (user == null) {
            setMessage("You are not authorized");
            throw new RedirectException("/index");
        }
        List<User> users = userService.findAll();
        view.put("users", users);
        view.put("talks", talkService.findAllByUserId(user.getId()));
        Map<String, String> idToLogin = new HashMap<>();
        for (User u : users) {
            idToLogin.put(Long.toString(u.getId()), u.getLogin());
        }
        view.put("idToLogin", idToLogin);
    }

    private void send(HttpServletRequest request, Map<String, Object> view) {
        Talk talk = new Talk();
        talk.setSourceUserId(getUser().getId());
        talk.setTargetUserId(Integer.parseInt(request.getParameter("select")));
        talk.setText(request.getParameter("text"));

        talkService.send(talk);
        setMessage("Your message was send!");
        throw new RedirectException("/talks");
    }
}
