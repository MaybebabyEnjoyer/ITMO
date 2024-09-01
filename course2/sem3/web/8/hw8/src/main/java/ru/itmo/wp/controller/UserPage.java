package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.service.UserService;

@Controller
public class UserPage extends Page {
    private final UserService userService;

    public UserPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/user/{id}", "/user/"})
    public String user(@PathVariable(value = "id", required = false) String userId, Model model) {
        long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException ignored) {
            id = -1;
        }
        model.addAttribute("user", userService.findById(id));
        return "UserPage";
    }
    
}
