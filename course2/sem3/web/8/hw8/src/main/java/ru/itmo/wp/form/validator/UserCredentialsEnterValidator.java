package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.service.UserService;

@Component
public class UserCredentialsEnterValidator implements Validator {
    private final UserService userService;

    public UserCredentialsEnterValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(Class<?> clazz) {
        return UserCredentials.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserCredentials enterForm = (UserCredentials) target;
            User user = userService.findByLoginAndPassword(enterForm.getLogin(), enterForm.getPassword());
            if (user == null) {
                errors.rejectValue(
                        "password", "password.invalid-login-or-password", "Invalid login or password");
            } else if (user.isDisabled()) {
                errors.rejectValue(
                        "login", "login.user-is-disabled", "User is disabled");
            }
        }
    }
}
