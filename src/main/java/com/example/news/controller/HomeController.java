package com.example.news.controller;

import com.example.news.dto.ArticleDTO;
import com.example.news.dto.UserDTO;
import com.example.news.entity.Article;
import com.example.news.exception.ArticleNotFoundException;
import com.example.news.exception.UserNotFoundException;
import com.example.news.service.ArticleService;
import com.example.news.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import java.util.List;

@Controller
public class HomeController {

    private final ArticleService articleService;
    private final UserService userService;

    @Autowired
    public HomeController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<ArticleDTO> latestArticles = articleService.getLatestArticles();
        model.addAttribute("articles", latestArticles);
        return "index"; // The name of the HTML template for the home page
    }

    @GetMapping("/news")
    public String news(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,desc") String[] sort) {
        Sort.Order order = Sort.Order.by(sort[0]).with(Sort.Direction.fromString(sort[1]));
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(order));
        List<Article> articlesPage = articleService.getAllArticles(pageRequest);
        model.addAttribute("articlesPage", articlesPage);
        return "news"; // The name of the HTML template for the news page
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        try {
            Article article = articleService.getArticleById(id);
            model.addAttribute("article", article);
            return "news-detail"; // The name of the HTML template for the news detail page
        } catch (ArticleNotFoundException ex) {
            return "error/404"; // The name of the HTML template for the error page
        }
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        try {
            UserDTO user = userService.getCurrentUser();
            model.addAttribute("user", user);
            return "settings"; // The name of the HTML template for the settings page
        } catch (UserNotFoundException ex) {
            return "error/404"; // The name of the HTML template for the error page
        }
    }

    @PostMapping("/settings")
    public String saveSettings(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDTO);
            return "settings";
        }
        userService.updateUserSettings(userDTO);
        return "redirect:/settings";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new UserDTO());
        return "login"; // The name of the HTML template for the login page
    }

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginRequest") UserDTO loginRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "login";
        }
        try {
            userService.authenticateUser(loginRequest);
            return "redirect:/";
        } catch (UserNotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new UserDTO());
        return "register"; // The name of the HTML template for the register page
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") UserDTO registerRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        userService.registerUser(registerRequest);
        return "redirect:/login";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ModelAndView handleArticleNotFoundException(ArticleNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }
}
