package moe.dozy.demo.sample1.services.providers;

import java.util.Arrays;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.security.core.userdetails.UserDetails;

import moe.dozy.demo.sample1.models.User;

public class UserSqlProvider {

    public static String findAllWithAuth(UserDetails user) {
        return new SQL() {{
            SELECT("u.*");
            FROM("users AS u");

            if (hasRole(user, "manager")) {
                INNER_JOIN("companies as c ON u.company_id = c.id OR u.parent_id = c.id");
            } else {
                INNER_JOIN("companies as c ON u.company_id = c.id");
            }

            WHERE("1 = 0");

            if (hasAnyPermission(user, "view users", "view supervisors",
                    "view managers", "view admins")) {
                INNER_JOIN("auth_user_has_roles AS uhr ON uhr.user_id = u.id");
                INNER_JOIN("auth_roles AS r ON uhr.role_id = r.id");
            }
            if (hasPermission(user, "view users")) {
                OR();
                WHERE("r.name = 'user'");
            }
            if (hasPermission(user, "view supervisors")) {
                OR();
                WHERE("r.name = 'supervisor'");
            }
            if (hasPermission(user, "view managers")) {
                OR();
                WHERE("r.name = 'manager'");
            }
            if (hasPermission(user, "view admins")) {
                OR();
                WHERE("r.name = 'admin'");
            }
        }}.toString();
    }

    public static String delete(User user) {
        return new SQL() {{
                DELETE_FROM("auth_user_has_roles");
                WHERE("user_id = #{id}");
            }}.toString() +
                ";" +
                 new SQL() {{
                DELETE_FROM("auth_user_has_permissions");
                WHERE("user_id = #{id}");
            }}.toString() +
                ";" + new SQL() {{
                DELETE_FROM("users");
                WHERE("id = #{id}");
            }}.toString() + ";";
    }

    protected static Boolean hasRole(UserDetails user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    protected static Boolean hasPermission(UserDetails user, String perm) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(perm));
    }
 
    protected static Boolean hasAnyPermission(UserDetails user, String ...perms) {
        var allPerms = Arrays.asList(perms);
        return user.getAuthorities().stream()
                .anyMatch(a -> allPerms.contains(a.getAuthority()));
    }
}
