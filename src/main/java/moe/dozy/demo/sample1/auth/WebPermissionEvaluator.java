package moe.dozy.demo.sample1.auth;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.UserService;

@Slf4j
@Component
public class WebPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserService userService;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
	public boolean hasPermission(Authentication authentication,
            Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null
                || !(permission instanceof String)) {
            return false;
        }

        if (targetDomainObject instanceof User) {
            return checkPrivilegeForUser(authentication,
                    (User) targetDomainObject, (String) permission);
        }

        return false;
    }

    @Override
	public boolean hasPermission(Authentication authentication,
            Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetType == null
                || !(permission instanceof String)) {
            return false;
        }

        if (targetType.equals(User.class.getSimpleName())) {
            if (targetId == null) {
                return checkPrivilegeForUser(authentication,
                        (String) permission);
            }
            var user = userService.findById((Long) targetId);
            beanFactory.autowireBean(user);
            return checkPrivilegeForUser(authentication, user,
                    (String) permission);
        }

        return false;
    }

    private boolean checkPrivilegeForUser(Authentication auth, String permission) {
        var authUser = ((WebUserDetails) auth.getPrincipal()).getUser();
        if (permission.equals("viewAny")) {
            return authUser.hasRole("admin");
        } else if (permission.equals("create")) {
            return authUser.hasRole("admin");
        }
        return false;
    }

    private boolean checkPrivilegeForUser(Authentication auth, User object,
            String permission) {
        var authUser = ((WebUserDetails) auth.getPrincipal()).getUser();
        if (permission.equals("view")) {
            for (AuthRole objectRole : object.getRoles()) {
                var role = objectRole.getName();
                if (! authUser.hasPermission("view " + role + "s")) {
                    continue;
                }

                // User has permission but we want to further limit by company
                // they were in

                if (authUser.hasRole("manager")) {
                    // Managers can see sub-company users
                    if (authUser.getCompanyId() == object.getCompanyId()
                            || (object.getCompany() != null
                            && object.getCompany().getParentId()
                            == authUser.getCompanyId())) {
                        return true;
                    }
                } else {
                    // Limit results to their own company
                    if (authUser.getCompanyId() == object.getCompanyId()) {
                        return true;
                    }
                }
            }
        } else if (permission.equals("update") || permission.equals("delete")) {
            for (AuthRole objectRole : object.getRoles()) {
                var role = objectRole.getName();
                if (! authUser.hasPermission("edit " + role + "s")) {
                    continue;
                }

                // User has permission but we want to further limit by company
                // they were in

                if (authUser.hasRole("manager")) {
                    // Managers can see sub-company users
                    if (authUser.getCompanyId() == object.getCompanyId()
                            || (object.getCompany() != null
                            && object.getCompany().getParentId()
                            == authUser.getCompanyId())) {
                        return true;
                    }
                } else {
                    // Limit results to their own company
                    if (authUser.getCompanyId() == object.getCompanyId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
