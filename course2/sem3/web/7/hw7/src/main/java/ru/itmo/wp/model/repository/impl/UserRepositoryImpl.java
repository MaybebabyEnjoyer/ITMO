package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.UserRepository;

import java.sql.*;
import java.util.List;

public class UserRepositoryImpl extends BaseRepositoryImpl<User> implements UserRepository {
    @Override
    public User find(long id) {
        return findImpl(id);
    }

    @Override
    public User findByLogin(String login) {
        return findByImpl(new Pair("login", login));
    }

    @Override
    public User findByEmail(String email) {
        return findByImpl(new Pair("email", email));
    }

    @Override
    public User findByLoginAndPasswordSha(String login, String passwordSha) {
        return findByImpl(
                new Pair("login", login),
                new Pair("passwordSha", passwordSha));
    }

    @Override
    public User findByEmailAndPasswordSha(String email, String passwordSha) {
        return findByImpl(
                new Pair("email", email),
                new Pair("passwordSha", passwordSha));
    }

    @Override
    public List<User> findAll() {
        return findAllImpl();
    }

    @Override
    public void save(User user, String passwordSha) {
        saveImpl(user,
                new Pair("login", user.getLogin()),
                new Pair("email", user.getEmail()),
                new Pair("passwordSha", passwordSha));
    }

    @Override
    public void updateAdmin(long id, boolean admin) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE User SET admin=? WHERE id=?"))
            {
                statement.setBoolean(1, admin);
                statement.setLong(2, id);
                statement.executeQuery();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't update User.", e);
        }
    }

    @Override
    protected User toModel(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        User user = new User();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id" -> user.setId(resultSet.getLong(i));
                case "login" -> user.setLogin(resultSet.getString(i));
                case "creationTime" -> user.setCreationTime(resultSet.getTimestamp(i));
                case "email" -> user.setEmail(resultSet.getString(i));
                case "admin" -> user.setAdmin(resultSet.getBoolean(i));
                default -> {
                    // No operations.
                }
            }
        }

        return user;
    }

    @Override
    protected String getName() {
        return "User";
    }
}
