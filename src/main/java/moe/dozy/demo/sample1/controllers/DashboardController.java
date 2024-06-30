package moe.dozy.demo.sample1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import moe.dozy.demo.sample1.services.UserService;

import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ApplicationContext appContext;

    @GetMapping
    public String getIndex(Model model, Authentication auth,
            HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UserService userService = new UserService(appContext);

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("pageDescription", "user dashboard");
        model.addAttribute("requestUrl",
                request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        model.addAttribute("objects", userService.findAllWithAuth(userDetails));

        return "dashboard";
    }
}
