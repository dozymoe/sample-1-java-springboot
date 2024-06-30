package moe.dozy.demo.sample1.auth;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
import moe.dozy.demo.sample1.services.UserService;
import moe.dozy.demo.sample1.services.AuthRoleService;

@Controller
@RequestMapping("/admin/user")
public class UserAdminController extends BaseController {

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasPermission(#id, 'User', 'update')")
    public String getEditForm(Model model, @PathVariable Long id,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var sqlSession = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSession.startManagedSession(ExecutorType.BATCH,
                TransactionIsolationLevel.READ_COMMITTED);
        try {
            var userService = new UserService(sqlSession, appContext);
            var roleService = new AuthRoleService(sqlSession, appContext);

            var user = userService.findById(id);

            model.addAttribute("pageTitle", "Edit User");
            model.addAttribute("pageDescription", "modify user information");
            model.addAttribute("forwardUrl", forwardUrl);
            model.addAttribute("object", UserRequest.of(user));
            model.addAttribute("roles", roleService.findAll());
        } finally {
            sqlSession.close();
        }

        return "user/edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasPermission(#id, 'User', 'update')")
    public String postEditForm(Model model, RedirectAttributes redirects,
            @PathVariable Long id,
            @Valid @ModelAttribute("object") UserRequest submission,
            BindingResult bindingResult,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var sqlSession = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSession.startManagedSession(ExecutorType.BATCH,
                TransactionIsolationLevel.READ_COMMITTED);
        try {
            var userService = new UserService(sqlSession, appContext);
            var roleService = new AuthRoleService(sqlSession, appContext);

            if (bindingResult.hasErrors()) {
                model.addAttribute("pageTitle", "Edit User");
                model.addAttribute("pageDescription", "modify user information");
                model.addAttribute("forwardUrl", forwardUrl);
                model.addAttribute("roles", roleService.findAll());
                return "user/edit";
            }

            var object = userService.findById(id);
            submission.mergeTo(object);
            userService.update(object);
            submission.mergeRelationsTo(object, sqlSession);

            sqlSession.commit();
        } finally {
            sqlSession.close();
        }

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
        var sqlSession = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSession.startManagedSession(ExecutorType.BATCH,
                TransactionIsolationLevel.READ_COMMITTED);
        try {
            var userService = new UserService(sqlSession, appContext);

            var object = userService.findById(id);

            model.addAttribute("pageTitle", "Delete User");
            model.addAttribute("pageDescription", "delete user information");
            model.addAttribute("forwardUrl", forwardUrl);
            model.addAttribute("object", object);
        } finally {
            sqlSession.close();
        }

        return "user/delete";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasPermission(#id, 'User', 'delete')")
    public String postDeleteForm(Model model, @PathVariable Long id,
            RedirectAttributes redirects,
            @RequestParam(value = "next", required = false) String forwardUrl) {
        var sqlSession = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSession.startManagedSession(ExecutorType.BATCH,
                TransactionIsolationLevel.READ_COMMITTED);
        try {
            var userService = new UserService(sqlSession, appContext);

            var object = userService.findById(id);
            if (object != null) {
                userService.delete(object);

                sqlSession.commit();
            }
        } finally {
            sqlSession.close();
        }

        redirects.addFlashAttribute("message", "The user has been deleted");
        if (forwardUrl != null) {
            return "redirect:" + forwardUrl;
        }
        return "redirect:/dashboard";
    }
}
