package moe.dozy.demo.sample1.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import moe.dozy.demo.sample1.controllers.BaseController;

@Controller
@RequestMapping("/auth")
public class AuthController extends BaseController {

    @GetMapping("/login")
    public String getLoginForm(Model model) {
        model.addAttribute("pageTitle", "Login User");
        model.addAttribute("pageDescription", "user login page");
        return "auth/login";
    }

    @GetMapping("/logout")
    public String getLogoutForm(Model model) {
        model.addAttribute("pageTitle", "Logout User");
        model.addAttribute("pageDescription", "user logout page");
        return "auth/logout";
    }
}
