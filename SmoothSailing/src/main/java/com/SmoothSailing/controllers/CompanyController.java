package com.SmoothSailing.controllers;

import com.SmoothSailing.dto.ChangeUserPassDto;
import com.SmoothSailing.dto.CompanyRegisterDto;
import com.SmoothSailing.dto.UserLoginDto;
import com.SmoothSailing.dto.UserRegisterDto;
import com.SmoothSailing.models.BoatModel;
import com.SmoothSailing.models.CompanyModel;
import com.SmoothSailing.models.UserModel;
import com.SmoothSailing.repositories.CompanyRepo;
import com.SmoothSailing.services.CompanyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/register")
    public String getRegisterCompanyPage(Model model){
        model.addAttribute("registerCompanyRequest", new CompanyModel());
        return "company/register_company_page";
    }

    @GetMapping("/login")
    public String getLoginCompanyPage(Model model){
        model.addAttribute("loginCompanyRequest", new CompanyModel());
        return "company/login_company_page";
    }

    @PostMapping("/register")
    public String registerCompany(@ModelAttribute CompanyRegisterDto companyRegisterDto){
        System.out.println("register request: " + companyRegisterDto);
        CompanyModel registeredCompany = companyService.registerCompany(companyRegisterDto);
        return registeredCompany == null ? "error_page" : "redirect:/company/login";
    }

    @PostMapping("/login")
    public String loginCompany(@ModelAttribute UserLoginDto companyLoginDto, Model model, HttpServletResponse response){
        System.out.println("login request: " + companyLoginDto);
        CompanyModel authenticated = companyService.authenticateCompany(companyLoginDto.getEmail(), companyLoginDto.getPassword());
        if (authenticated!=null){
            model.addAttribute("companyEmail", authenticated.getEmail());

            Cookie cookie = new Cookie("company_id", authenticated.getId());
            cookie.setMaxAge(3600);
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            Cookie cookieName = new Cookie("name", "company");
            cookieName.setMaxAge(3600);
            cookieName.setSecure(true);
            cookieName.setHttpOnly(true);
            cookieName.setPath("/");
            response.addCookie(cookieName);

            return "redirect:/";
        }
        else{
            throw new NullPointerException("Id value cannot be null!");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("company_id", "");
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

    @GetMapping("/list")
    public String list(@CookieValue(name = "name", required = false) String role, @RequestParam Map<String, String> allParams, Model model){

        if(!Objects.equals(role, "admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        model.addAttribute("companyListRequest", companyService.getAll(Integer.parseInt(allParams.get("page"))));
        model.addAttribute("admin", true);
        model.addAttribute("prev", Integer.parseInt(allParams.get("page")) - 1);
        model.addAttribute("next", Integer.parseInt(allParams.get("page")) + 1);
        return "company/company_list";
    }

    @GetMapping("/change-password/{id}")
    public String changePassword(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id, Model model){

        if(!Objects.equals(role, "company")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        model.addAttribute("changePasswordRequest", id);
        return "company/company_change_pass";
    }

    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable("id") String id, @ModelAttribute ChangeUserPassDto changeUserPassDto){
        companyService.changePass(id, changeUserPassDto.getPassword());
        return "redirect:/company/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id, Model model){

        if(!Objects.equals(role, "company") && !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        Optional<CompanyModel> user = companyService.getById(id);
        user.ifPresent(userModel -> model.addAttribute("editCompanyRequest", userModel));
        return "company/edit_company";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, @ModelAttribute CompanyRegisterDto companyDto){
        companyService.edit(id, companyDto);
        return "redirect:/company/list?page=0";
    }

    @GetMapping("/delete/{id}")
    public String delete(@CookieValue(name = "name", required = false) String role, @PathVariable("id") String id){

        if(!Objects.equals(role, "company") && !role.equals("admin")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");
        }

        companyService.deleteById(id);
        return "redirect:/company/list?page=0";
    }

    @GetMapping("/profile")
    public String profilePage(@CookieValue(name = "name", required = false) String role,
                              @CookieValue(name = "company_id", required = false) String id, Model model){

        if(!Objects.equals(role, "company")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Values are not equal");;
        if(id == null || id.isEmpty()){
            return "company/login_page";
        }

        Optional<CompanyModel> company = companyService.getById(id);
        company.ifPresent(companyModel -> model.addAttribute("profile", companyModel));

        return "company/company_page";
    }

    @GetMapping("/boat-list")
    public String companyBoatList(@CookieValue(name = "company_id", required = false) String id, Model model, @RequestParam Map<String, String> allParams){

        if(id == null || id.isEmpty()){
            return "company/login_company_page";
        }

        List<BoatModel> boatPage = companyService.getBoatsByCompanyId(id, Integer.parseInt(allParams.get("page")));
        model.addAttribute("boats", boatPage);
        model.addAttribute("prev", Integer.parseInt(allParams.get("page")) - 1);
        model.addAttribute("next", Integer.parseInt(allParams.get("page")) + 1);

        return "company/boat_list";
    }
}