package ru.itmo.wp.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.form.TagNames;

@Component
public class TagsValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return TagNames.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            TagNames tagsString = (TagNames) target;
            String[] tags = tagsString.getNames().split("\\s+");
            for (String tag : tags) {
                if (!tag.matches("[a-zA-Z+]")) {
                    errors.rejectValue("tags", "latin-letters-only",
                            "Tags can contain latin letter only.");
                }
            }
        }
    }
}
