package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPostsPage extends Page {
    @GetMapping("/myPosts")
    public String posts() {
        return "MyPostsPage";
    }
}
