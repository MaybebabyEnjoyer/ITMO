package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.Status;
import ru.itmo.wp.service.UserService;

@Component
public class StatusValidator implements Validator {
    private final UserService userService;

    public StatusValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Status.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            Status status = (Status) target;
            User user = userService.findById(status.getUserId());
            if (user == null) {
                errors.rejectValue(
                        "id", "no-such-user", "No such user"
                );
            }
        }
    }
}
