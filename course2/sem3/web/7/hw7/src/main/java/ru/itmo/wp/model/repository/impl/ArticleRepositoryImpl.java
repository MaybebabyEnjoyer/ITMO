package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.ArticleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleRepositoryImpl extends BaseRepositoryImpl<Article> implements ArticleRepository {
    @Override
    public void save(Article article) {
        saveImpl(article,
                new Pair("userId", article.getUserId()),
                new Pair("title", article.getTitle()),
                new Pair("text", article.getText()),
                new Pair("hidden", article.getHidden())
        );
    }

    @Override
    public Article find(long id) {
        return findImpl(id);
    }

    @Override
    public List<Article> findAll() {
        return findAllImpl();
    }

    @Override
    public List<Article> findAllByUserId(long id) {
        String sql = "SELECT * FROM " + getName() + " WHERE userId=" + id + " ORDER BY id DESC";
        return findAllByImpl(sql);
    }

    @Override
    public List<Article> findAllByHidden(boolean hidden) {
        String sql = "SELECT * FROM " + getName() + " WHERE hidden=" + hidden + " ORDER BY id DESC";
        return findAllByImpl(sql);
    }

    @Override
    public void updateHidden(long id, boolean updateHidden) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE Article SET hidden=? WHERE id=?"))
            {
                statement.setBoolean(1, updateHidden);
                statement.setLong(2, id);
                statement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't update Article.", e);
        }
    }

    @Override
    protected Article toModel(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Article article = new Article();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> article.setId(resultSet.getLong(i));
                case "userId" -> article.setUserId(resultSet.getLong(i));
                case "title" -> article.setTitle(resultSet.getString(i));
                case "text" -> article.setText(resultSet.getString(i));
                case "hidden" -> article.setHidden(resultSet.getBoolean(i));
                case "creationTime" -> article.setCreationTime(resultSet.getTimestamp(i));
                default -> {
                    // No operation.
                }
            }
        }

        return article;
    }

    @Override
    protected String getName() {
        return "Article";
    }
}
