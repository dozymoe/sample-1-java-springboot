package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;

import moe.dozy.demo.sample1.models.AuthPermission;
import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.repositories.AuthorizationRepository;

public class AuthorizationService extends BaseService {

    private AuthorizationRepository repository;

    public AuthorizationService(ApplicationContext ctx) {
        super(ctx);
        this.repository = ctx.getBean(AuthorizationRepository.class);
    }

    public AuthorizationService(SqlSession sqlSession, ApplicationContext ctx) {
        super(ctx);
        this.repository = sqlSession.getMapper(AuthorizationRepository.class);
    }

    public List<AuthRole> findAllUserRoles(User user) {
        return findAllUserRoles(user, null);
    }

    public List<AuthRole> findAllUserRoles(User user, String guard_name) {
        var objs = repository.findAllUserRoles(user, guard_name);
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public void setUserRoles(User user, AuthRole ...roles) {
        setUserRoles(user, null, roles);
    }

    public void setUserRoles(User user, String guard_name, AuthRole ...roles) {
        repository.setUserRoles(user, guard_name, roles);
    }

    public List<AuthPermission> findAllUserPermissions(User user) {
        return findAllUserPermissions(user, null);
    }

    public List<AuthPermission> findAllUserPermissions(User user,
            String guard_name) {
        var objs = repository.findAllUserPermissions(user, guard_name);
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public List<AuthPermission> findAllRolePermissions(AuthRole role) {
        return findAllRolePermissions(role, null);
    }

    public List<AuthPermission> findAllRolePermissions(AuthRole role,
            String guard_name) {
        var objs = repository.findAllRolePermissions(role, guard_name);
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    protected void applyUserTimeZone(AuthPermission obj) {
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }

    protected void applyUserTimeZone(AuthRole obj) {
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }
}
