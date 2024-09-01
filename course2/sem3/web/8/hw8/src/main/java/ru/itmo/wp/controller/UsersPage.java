package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itmo.wp.form.Status;
import ru.itmo.wp.form.validator.StatusValidator;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UsersPage extends Page {
    private final UserService userService;
    private final StatusValidator statusValidator;

    public UsersPage(UserService userService, StatusValidator statusValidator) {
        this.userService = userService;
        this.statusValidator = statusValidator;
    }

    @InitBinder("updateStatus")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(statusValidator);
    }

    @GetMapping("/users/all")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "UsersPage";
    }

    @PostMapping("/users/all")
    public String updateStatus(@Valid Status status,
                               BindingResult bindingResult,
                               HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "redirect:/users/all";
        }

        if (checkUser(httpSession)) {
            return "redirect:/";
        }

        userService.updateStatus(status.getUserId(), status.isStatus());
        return "redirect:/users/all";
    }

}
