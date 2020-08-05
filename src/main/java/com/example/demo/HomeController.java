package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CloudinaryConfig cloudinaryConfig;

    @RequestMapping("/")
    public String index(Model model, Principal principal) {
        model.addAttribute("post", new Post());
        model.addAttribute("posts", postRepository.findAll());


        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
        }

        return "index";
    }

    @GetMapping("/signup")
    public String loadRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registrationform";
    }

    @PostMapping("/processuser")
    public String processUser(@ModelAttribute User user) {
        user.setEnabled(true);

        Role userRole = new Role(user.getUsername(), "ROLE_USER");


        userRepository.save(user);
        roleRepository.save(userRole);
        return "redirect:/";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }

    @RequestMapping("/secure")
    public String secure() {
        return "secure";
    }

    @GetMapping("/newpost")
    public String loadPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "postform";
    }

    @PostMapping("/processpost")
    public String processPost(@ModelAttribute Post post, Principal principal, @RequestParam("file") MultipartFile file) {

        User author;
        author = userRepository.findUserByUsername(principal.getName());
        post.setAuthor(author);


        if (!file.isEmpty()) {
            try {
                Map uploadResult = cloudinaryConfig.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                post.setMedia(uploadResult.get("url").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        postRepository.save(post);

        return "redirect:/";
    }
}
