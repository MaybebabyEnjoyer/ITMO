package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmo.wp.domain.Comment;
import ru.itmo.wp.domain.Post;
import ru.itmo.wp.security.Guest;
import ru.itmo.wp.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class PostPage extends Page {
    private final PostService postService;

    public PostPage(PostService postService) {
        this.postService = postService;
    }

    @Guest
    @GetMapping({"/post/{id}", "/post/"})
    public String post(@PathVariable(value = "id", required = false) String postId,
                       Model model) {
        long id = checkId(postId);
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        if (!model.containsAttribute("comment")) {
            model.addAttribute("comment", new Comment());
        }
        return "PostPage";
    }

    @PostMapping({"/post/{id}", "/post/"})
    public String createComment(@PathVariable(value = "id", required = false) String postId,
                                @Valid @ModelAttribute("comment") Comment comment,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                HttpSession httpSession)
    {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.comment",
                    bindingResult);
            redirectAttributes.addFlashAttribute("comment", comment);
            return "redirect:/post/" + postId;
        }

        long id = checkId(postId);
        Post post = postService.findById(id);
        postService.writeComment(getUser(httpSession), post, comment);
        putMessage(httpSession, "Your comment was published");

        return "redirect:/post/" + id;
    }

    private long checkId(String id) {
        long res;
        try {
            res = Long.parseLong(id);
        } catch (NumberFormatException ignored) {
            res = -1;
        }
        return res;
    }
}
