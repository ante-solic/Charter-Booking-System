package com.SmoothSailing.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void checkCookie(@CookieValue(name = "name", required = false) String name, Model model) {
        if (name == null || name.isEmpty()) {
            name = "logout";
        }
        System.out.println(name);
        model.addAttribute("authorize", name);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404Error(NoHandlerFoundException ex) {
        return "error"; // Return the error page view name
    }
}