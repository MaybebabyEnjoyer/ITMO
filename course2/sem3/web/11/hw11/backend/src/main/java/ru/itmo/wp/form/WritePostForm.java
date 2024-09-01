package ru.itmo.wp.form;

import ru.itmo.wp.domain.User;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WritePostForm {
    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotEmpty
    @NotBlank
    @Lob
    @Size(min = 1, max = 10000)
    private String text;

    @NotNull
    private User user;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
