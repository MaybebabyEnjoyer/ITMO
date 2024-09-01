package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class MyArticlesPage extends Page {
    @Override
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        if (getUser() == null) {
            setNotAuthorizedMessage();
        }
        view.put("articles", articleService.findAllByUserId(getUser().getId()));
    }

    private void change(HttpServletRequest request, Map<String, Object> view) {
        long id = Long.parseLong(request.getParameter("id"));
        boolean hidden = Boolean.parseBoolean(request.getParameter("boolValue"));
        Article article = articleService.find(id);

        if (article.getUserId() != getUser().getId()) {
            setMessage("You are not the author of this article");
            throw new RedirectException("/index");
        }

        articleService.updateHidden(article.getId(), !hidden);
        view.put("bool", !hidden);
    }
}
