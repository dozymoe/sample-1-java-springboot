package moe.dozy.demo.sample1.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "Hallo");
        model.addAttribute("pageDescription", "welcome page");
        return "home";
    }

}
