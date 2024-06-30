package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;

import moe.dozy.demo.sample1.filters.UserFilterBy;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.repositories.UserRepository;

public class UserService extends BaseService {

    private UserRepository repository;

    public UserService(ApplicationContext ctx) {
        super(ctx);
        this.repository = ctx.getBean(UserRepository.class);
    }

    public UserService(SqlSession sqlSession, ApplicationContext ctx) {
        super(ctx);
        this.repository = sqlSession.getMapper(UserRepository.class);
    }

    public List<User> findAll() {
        var objs = repository.findAll();
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public User findById(Long id) {
        var obj = repository.findById(id);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public User findByName(String name) {
        var obj = repository.findByName(name);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public User findByEmail(String email) {
        var obj = repository.findByEmail(email);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public void update(User user) {
        repository.update(user);
    }

    public User upsert(User user) {
        var obj = repository.upsert(user);
        if (obj == null) {
            obj = repository.findByName(user.getName());
        }
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public void delete(User user) {
        repository.delete(user);
    }

    public List<User> findAllWithAuth(UserDetails user) {
        var objs = repository.findAllWithAuth(user);
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public List<User> findAllBy(UserFilterBy filter) {
        var objs = repository.findAllBy(filter);
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    protected void applyUserTimeZone(User obj) {
        if (obj.getEmailVerifiedAt() != null) {
            obj.setEmailVerifiedAt(obj.getEmailVerifiedAt()
                    .withZoneSameInstant(properties.getZoneId()));
        }
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }
}
