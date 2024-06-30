package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;

import moe.dozy.demo.sample1.models.AuthPermission;
import moe.dozy.demo.sample1.repositories.AuthPermissionRepository;

public class AuthPermissionService extends BaseService {

    private AuthPermissionRepository repository;

    public AuthPermissionService(ApplicationContext ctx) {
        super(ctx);
        this.repository = ctx.getBean(AuthPermissionRepository.class);
    }

    public AuthPermissionService(SqlSession sqlSession, ApplicationContext ctx) {
        super(ctx);
        this.repository = sqlSession.getMapper(AuthPermissionRepository.class);
    }

    public List<AuthPermission> findAll() {
        var objs = repository.findAll();
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public AuthPermission findById(Long id) {
        var obj = repository.findById(id);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public AuthPermission findByName(String name) {
        return findByName(name, null);
    }

    public AuthPermission findByName(String name, String guard_name) {
        var obj = repository.findByName(name, guard_name);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public AuthPermission upsert(AuthPermission perm) {
        var obj = repository.upsert(perm);
        if (obj == null) {
            obj = repository.findByName(perm.getName(), perm.getGuardName());
        }
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    protected void applyUserTimeZone(AuthPermission obj) {
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }
}
