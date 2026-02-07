package com.healthtech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Welcome Controller - Redirects root to frontend.
 */
@Controller
public class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "redirect:/index.html";
    }
}
