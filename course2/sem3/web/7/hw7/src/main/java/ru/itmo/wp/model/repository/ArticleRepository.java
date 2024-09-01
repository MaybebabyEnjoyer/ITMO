package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;

import java.util.List;

public interface ArticleRepository {
    void save(Article article);
    Article find(long id);
    List<Article> findAll();
    List<Article> findAllByUserId(long id);
    List<Article> findAllByHidden(boolean hidden);
    void updateHidden(long id, boolean updateHidden);
}
