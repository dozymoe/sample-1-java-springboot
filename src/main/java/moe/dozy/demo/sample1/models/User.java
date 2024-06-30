package moe.dozy.demo.sample1.models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import moe.dozy.demo.sample1.services.AuthorizationService;
import moe.dozy.demo.sample1.services.CompanyService;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class User implements Serializable {

    @Autowired
    private ApplicationContext appContext;

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    @ToString.Include
    private String name;
    private String email;
    private String fullname;
    private String password;
    private Long company_id;
    private ZonedDateTime email_verified_at;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;

    private Optional<List<AuthRole>> _roles = Optional.empty();
    private Optional<List<AuthPermission>> _permissions = Optional.empty();
    private Optional<Company> _company = Optional.empty();

    private SqlSession sqlSession;

    public String getFullName() { return fullname; }
    public void setFullName(String name) { this.fullname = name; }
    public Long getCompanyId() { return company_id; }
    public void setCompanyId(Long id) { this.company_id = id; }
    public ZonedDateTime getEmailVerifiedAt() { return email_verified_at; }
    public void setEmailVerifiedAt(ZonedDateTime date) { this.email_verified_at = date; }
    public ZonedDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt(ZonedDateTime date) { this.created_at = date; }
    public ZonedDateTime getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(ZonedDateTime date) { this.updated_at = date; }

    public Company getCompany() {
        return getCompany(false);
    }

    public Company getCompany(Boolean ignoreCache) {
        if (company_id == null) {
            return null;
        }
        if (_company.isEmpty() || ignoreCache) {
            var companyService = getCompanyService();
            this._company = Optional.of(companyService.findById(company_id));
        }
        return _company.get();
    }

    public void setCompany(Company company) {
        if (company == null) {
            this.company_id = null;
        } else {
            this.company_id = company.getId();
        }
        // clear cache
        this._company = Optional.empty();
    }

    public List<AuthRole> getRoles() {
        return getRoles(null, false);
    }

    public List<AuthRole> getRoles(Boolean ignoreCache) {
        return getRoles(null, ignoreCache);
    }

    public List<AuthRole> getRoles(String guard_name) {
        return getRoles(guard_name, false);
    }

    public List<AuthRole> getRoles(String guard_name, Boolean ignoreCache) {
        if (_roles.isEmpty() || ignoreCache) {
            var authService = getAuthorizationService();
            this._roles = Optional.of(authService.findAllUserRoles(this,
                    guard_name));
        }
        return _roles.get();
    }

    public List<String> getRoleNames() {
        return getRoleNames(null, false);
    }

    public List<String> getRoleNames(Boolean ignoreCache) {
        return getRoleNames(null, ignoreCache);
    }

    public List<String> getRoleNames(String guard_name) {
        return getRoleNames(guard_name, false);
    }

    public List<String> getRoleNames(String guard_name, Boolean ignoreCache) {
        var roles = getRoles(guard_name, ignoreCache);
        return roles.stream().map(AuthRole::getName)
                .collect(Collectors.toList());
    }

    public void setRoles(AuthRole ...roles) {
        setRoles(null, roles);
    }

    public void setRoles(String guard_name, AuthRole ...roles) {
        var authService = getAuthorizationService();
        authService.setUserRoles(this, guard_name, roles);
        // clear cache
        this._roles = Optional.empty();
        this._permissions = Optional.empty();
    }

    public List<AuthPermission> getPermissions() {
        return getPermissions(null, false);
    }

    public List<AuthPermission> getPermissions(Boolean ignoreCache) {
        return getPermissions(null, ignoreCache);
    }

    public List<AuthPermission> getPermissions(String guard_name) {
        return getPermissions(guard_name, false);
    }

    public List<AuthPermission> getPermissions(String guard_name, Boolean ignoreCache) {
        if (_permissions.isEmpty() || ignoreCache) {
            var authService = getAuthorizationService();
            this._permissions = Optional.of(authService.findAllUserPermissions(
                    this, guard_name));
        }
        return _permissions.get();
    }

    public Boolean hasRole(String role) {
        return hasRole(role, null, false);
    }

    public Boolean hasRole(String role, Boolean ignoreCache) {
        return hasRole(role, null, ignoreCache);
    }

    public Boolean hasRole(String role, String guard_name) {
        return hasRole(role, guard_name, false);
    }

    public Boolean hasRole(String role, String guard_name,
            Boolean ignoreCache) {
        var roles = getRoles(guard_name, ignoreCache);
        return roles.stream().filter(o -> o.getName().equals(role))
                .findFirst().isPresent();
    }

    public Boolean hasPermission(String permission) {
        return hasPermission(permission, null, false);
    }

    public Boolean hasPermission(String permission, Boolean ignoreCache) {
        return hasPermission(permission, null, ignoreCache);
    }

    public Boolean hasPermission(String permission, String guard_name) {
        return hasPermission(permission, guard_name, false);
    }

    public Boolean hasPermission(String permission, String guard_name,
            Boolean ignoreCache) {
        var perms = getPermissions(guard_name, ignoreCache);
        return perms.stream().filter(o -> o.getName().equals(permission))
                .findFirst().isPresent();
    }

    public Boolean hasAnyPermission(List<String> permissions) {
        return hasAnyPermission(permissions, null, false);
    }

    public Boolean hasAnyPermission(List<String> permissions, Boolean ignoreCache) {
        return hasAnyPermission(permissions, null, ignoreCache);
    }

    public Boolean hasAnyPermission(List<String> permissions, String guard_name) {
        return hasAnyPermission(permissions, guard_name, false);
    }

    public Boolean hasAnyPermission(List<String> permissions, String guard_name,
            Boolean ignoreCache) {
        var perms = getPermissions(guard_name, ignoreCache);
        return perms.stream().filter(o -> permissions.contains(o.getName()))
                .findFirst().isPresent();
    }

    private CompanyService getCompanyService() {
        if (sqlSession != null) {
            return new CompanyService(sqlSession, appContext);
        }
        return new CompanyService(appContext);
    }

    private AuthorizationService getAuthorizationService() {
        if (sqlSession != null) {
            return new AuthorizationService(sqlSession, appContext);
        }
        return new AuthorizationService(appContext);
    }
}
