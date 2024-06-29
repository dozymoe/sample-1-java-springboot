package moe.dozy.demo.sample1.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import moe.dozy.demo.sample1.controllers.BaseController;
import moe.dozy.demo.sample1.requests.UserRequest;
import moe.dozy.demo.sample1.services.AuthRoleService;
import moe.dozy.demo.sample1.services.UserService;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController extends BaseController {

    @Autowired
    private AuthRoleService authRoleService;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasPermission(#id, 'User', 'update')")
    public String getEditForm(Model model, @PathVariable Long id,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var user = userService.findById(id);
        beanFactory.autowireBean(user);

        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("pageDescription", "modify user information");
        model.addAttribute("forwardUrl", forwardUrl);
        model.addAttribute("object", UserRequest.of(user));
        model.addAttribute("roles", authRoleService.findAll());

        return "user/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasPermission(#id, 'User', 'update')")
    public String postEditForm(Model model, RedirectAttributes redirects,
            @PathVariable Long id,
            @Valid @ModelAttribute("object") UserRequest submission,
            BindingResult bindingResult,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit User");
            model.addAttribute("pageDescription", "modify user information");
            model.addAttribute("forwardUrl", forwardUrl);
            model.addAttribute("roles", authRoleService.findAll());
            return "user/edit";
        }
        beanFactory.autowireBean(submission);

        var object = userService.findById(id);
        beanFactory.autowireBean(object);
        submission.mergeTo(object);
        userService.update(object);
        submission.mergeRelationsTo(object);

        redirects.addFlashAttribute("message", "The user has been updated");
        if (forwardUrl != null) {
            return "redirect:" + forwardUrl;
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("hasPermission(#id, 'User', 'delete')")
    public String getDeleteForm(Model model, @PathVariable Long id,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var object = userService.findById(id);
        beanFactory.autowireBean(object);

        model.addAttribute("pageTitle", "Delete User");
        model.addAttribute("pageDescription", "delete user information");
        model.addAttribute("forwardUrl", forwardUrl);
        model.addAttribute("object", object);

        return "user/delete";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasPermission(#id, 'User', 'delete')")
    public String postDeleteForm(Model model, @PathVariable Long id,
            RedirectAttributes redirects,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var object = userService.findById(id);
        beanFactory.autowireBean(object);
        userService.delete(object);

        redirects.addFlashAttribute("message", "The user has been deleted");
        if (forwardUrl != null) {
            return "redirect:" + forwardUrl;
        }
        return "redirect:/dashboard";
    }
}
