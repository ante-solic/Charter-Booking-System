package com.SmoothSailing.controllers;

import com.SmoothSailing.dto.ChangeUserPassDto;
import com.SmoothSailing.dto.UserLoginDto;
import com.SmoothSailing.dto.UserRegisterDto;
import com.SmoothSailing.models.UserModel;
import com.SmoothSailing.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String list(@CookieValue(name = "name", required = false) String role, @RequestParam Map<String, String> allParams, Model model){

        if(!Objects.equals(role, "admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        model.addAttribute("userListRequest", userService.getAll(Integer.parseInt(allParams.get("page"))));
        model.addAttribute("admin", true);
        model.addAttribute("prev", Integer.parseInt(allParams.get("page")) - 1);
        model.addAttribute("next", Integer.parseInt(allParams.get("page")) + 1);
        return "user/user_list";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model){
        model.addAttribute("registerRequest", new UserModel());
        return "user/register_page";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model){
        model.addAttribute("loginRequest", new UserModel());
        return "user/login_page";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserRegisterDto userDto){
        System.out.println("register request: " + userDto);
        UserModel registeredUser = userService.registerUser(userDto);
        return registeredUser == null ? "error_page" : "redirect:/user/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserLoginDto userDto, Model model, HttpServletResponse response){
        System.out.println("login request: " + userDto);
        UserModel authenticated = userService.authenticate(userDto.getEmail(), userDto.getPassword());
        if(authenticated!=null){
            model.addAttribute("userEmail", authenticated.getEmail());
            model.addAttribute("userId", authenticated.getId());

            Cookie cookie = new Cookie("id", authenticated.getId());
            cookie.setMaxAge(3600);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            String status = authenticated.getSuperuser() ? "admin" : "user";
            Cookie cookieName = new Cookie("name", status);
            cookieName.setMaxAge(3600);
            cookieName.setSecure(true);
            cookieName.setHttpOnly(true);
            cookieName.setPath("/");
            response.addCookie(cookieName);
            return "redirect:/user/profile";
        }
        else{
            throw new NullPointerException("Value cannot be null!");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("id", "");
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        Cookie name = new Cookie("name", null);
        name.setMaxAge(0);
        name.setSecure(true);
        name.setHttpOnly(true);
        name.setPath("/");
        response.addCookie(name);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@CookieValue(name = "name", required = false) String role, @CookieValue("id") String id, Model model){

        if(!Objects.equals(role, "user") && !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        Optional<UserModel> user = userService.getUserById(id);
        user.ifPresent(userModel -> model.addAttribute("editUserRequest", userModel));
        return "user/edit_user";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, @ModelAttribute UserRegisterDto userDto){
        userService.editUser(id, userDto);
        return "redirect:/user/list?page=0";
    }

    @GetMapping("/delete/{id}")
    public String delete(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id){

        if(!Objects.equals(role, "user") && !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        userService.deleteUserById(id);
        return "redirect:/user/list?page=0";
    }

    @GetMapping("/change-password/{id}")
    public String changePassword(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id, Model model){

        if(!Objects.equals(role, "user")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        model.addAttribute("changePasswordRequest", id);
        return "user/change_password";
    }

    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable("id") String id, @ModelAttribute ChangeUserPassDto changeUserPassDto){
        userService.changePass(id, changeUserPassDto.getPassword());
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profilePage(@CookieValue(name = "name", required = false) String role,
                              @CookieValue(name = "id", required = false) String id, Model model){
        if(!Objects.equals(role, "user") && !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }
        if(id == null || id.isEmpty()){
            throw new NullPointerException("Value cannot be null!");
        }

        Optional<UserModel> user = userService.getUserById(id);
        user.ifPresent(userModel -> model.addAttribute("profile", userModel));

        return "user/personal_page";
    }
}
