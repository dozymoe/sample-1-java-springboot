package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;

import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.repositories.AuthRoleRepository;

public class AuthRoleService extends BaseService {

    private AuthRoleRepository repository;

    public AuthRoleService(ApplicationContext ctx) {
        super(ctx);
        this.repository = ctx.getBean(AuthRoleRepository.class);
    }

    public AuthRoleService(SqlSession sqlSession, ApplicationContext ctx) {
        super(ctx);
        this.repository = sqlSession.getMapper(AuthRoleRepository.class);
    }

    public List<AuthRole> findAll() {
        var objs = repository.findAll();
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public AuthRole findById(Long id) {
        var obj = repository.findById(id);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public AuthRole findByName(String name, String guard_name) {
        var obj = repository.findByName(name, guard_name);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public AuthRole findByName(String name) {
        return findByName(name, null);
    }

    public AuthRole upsert(AuthRole role) {
        var obj = repository.upsert(role);
        if (obj == null) {
            obj = repository.findByName(role.getName(), role.getGuardName());
        }
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    protected void applyUserTimeZone(AuthRole obj) {
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }
}
