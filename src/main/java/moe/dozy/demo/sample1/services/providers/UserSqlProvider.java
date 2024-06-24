package moe.dozy.demo.sample1.services.providers;

import java.util.Arrays;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.security.core.userdetails.UserDetails;

import moe.dozy.demo.sample1.models.User;

public class UserSqlProvider {

    public String findAllWithAuth(UserDetails user) {
        return new SQL() {{
            SELECT("u.*");
            FROM("users AS u");

            if (hasRole(user, "manager")) {
                INNER_JOIN("companies as c ON u.company_id = c.id");
                WHERE("(u.company_id = #{user.company_id} OR c.parent_id = #{user.company_id})");
            } else {
                WHERE("u.company_id = #{user.company_id}");
            }

            if (hasAnyPermission(user, "view users", "view supervisors",
                    "view managers", "view admins")) {
                INNER_JOIN("auth_user_has_roles AS uhr ON uhr.user_id = u.id");
                INNER_JOIN("auth_roles AS r ON uhr.role_id = r.id");
            }

            var where = new StringBuilder();
            where.append("1 = 0");
            if (hasPermission(user, "view users")) {
                where.append(" OR r.name = 'user'");
            }
            if (hasPermission(user, "view supervisors")) {
                where.append(" OR r.name = 'supervisor'");
            }
            if (hasPermission(user, "view managers")) {
                where.append(" OR r.name = 'manager'");
            }
            if (hasPermission(user, "view admins")) {
                where.append(" OR r.name = 'admin'");
            }
            WHERE("(" + where.toString() + ")");

            ORDER_BY("created_at DESC");
        }}.toString();
    }

    public String delete(User user) {
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

    protected Boolean hasRole(UserDetails user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    protected Boolean hasPermission(UserDetails user, String perm) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(perm));
    }
 
    protected Boolean hasAnyPermission(UserDetails user, String ...perms) {
        var allPerms = Arrays.asList(perms);
        return user.getAuthorities().stream()
                .anyMatch(a -> allPerms.contains(a.getAuthority()));
    }
}
