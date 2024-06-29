package moe.dozy.demo.sample1.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import moe.dozy.demo.sample1.Sample1Properties;
import moe.dozy.demo.sample1.services.UserService;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private Sample1Properties properties;
    @Autowired
    private UserService userService;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @GetMapping
    public String getIndex(Model model, Authentication auth,
            HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("pageDescription", "user dashboard");
        model.addAttribute("requestUrl",
                request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        var companyUsers = userService.findAllWithAuth(userDetails);
        companyUsers.forEach(x -> {
            beanFactory.autowireBean(x);
            x.setCreatedAt(x.getCreatedAt()
                    .withZoneSameInstant(properties.getZoneId()));
        });
        model.addAttribute("objects", companyUsers);
        return "dashboard";
    }
}
