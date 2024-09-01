package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.UserRepository;
import ru.itmo.wp.model.repository.impl.UserRepositoryImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

/** @noinspection UnstableApiUsage*/
public class UserService {
    private final UserRepository userRepository = new UserRepositoryImpl();
    private static final String PASSWORD_SALT = "1174f9d7bc21e00e9a5fd0a783a44c9a9f73413c";

    public void validateRegistration(User user, String password) throws ValidationException {
        if (Strings.isNullOrEmpty(user.getLogin())) {
            throw new ValidationException("Login is required");
        }
        if (!user.getLogin().matches("[a-z]+")) {
            throw new ValidationException("Login can contain only lowercase Latin letters");
        }
        if (user.getLogin().length() > 20) {
            throw new ValidationException("Login can't be longer than 20 letters");
        }
        if (userRepository.findByLogin(user.getLogin()) != null) {
            throw new ValidationException("Login is already in use");
        }

        if (Strings.isNullOrEmpty(user.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!user.getEmail().matches("[a-zA-Z0-9]*@[a-zA-Z0-9]*")) {
            throw new ValidationException("Email address can contain only one '@' character");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("Email is already in use");
        }

        if (Strings.isNullOrEmpty(password)) {
            throw new ValidationException("Password is required");
        }
        if (password.length() < 4) {
            throw new ValidationException("Password can't be shorter than 4 characters");
        }
        if (password.length() > 25) {
            throw new ValidationException("Password can't be longer than 25 characters");
        }
    }

    public User validateAndFindByLoginAndPassword(String loginOrEmail, String password) throws ValidationException {
        User user;
        String passwordSha = getPasswordSha(password);
        if (loginOrEmail.matches("[a-zA-Z0-9]*@[a-zA-Z0-9]*")) {
            user = userRepository.findByEmailAndPasswordSha(loginOrEmail, passwordSha);
        } else {
            user = userRepository.findByLoginAndPasswordSha(loginOrEmail, passwordSha);
        }
        if (user == null) {
            throw new ValidationException("Invalid login or password");
        }
        return user;
    }

    public void register(User user, String password) {
        userRepository.save(user, getPasswordSha(password));
    }

    private String getPasswordSha(String password) {
        return Hashing.sha256().hashBytes((PASSWORD_SALT + password).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User find(long id) {
        return userRepository.find(id);
    }

    public void updateAdmin(long id, boolean admin) {
        userRepository.updateAdmin(id, admin);
    }
}
