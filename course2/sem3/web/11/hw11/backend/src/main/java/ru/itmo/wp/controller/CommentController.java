package ru.itmo.wp.controller;

import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/1")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("comments")
    public List<Comment> findComments() {
        return commentService.findAll();
    }
}
