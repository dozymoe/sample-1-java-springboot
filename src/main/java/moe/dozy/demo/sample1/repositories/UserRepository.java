package moe.dozy.demo.sample1.repositories;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.security.core.userdetails.UserDetails;

import moe.dozy.demo.sample1.filters.UserFilterBy;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.repositories.providers.UserSqlProvider;

@Mapper
public interface UserRepository {
    public List<User> findAll();
    public User findById(Long id);
    public User findByName(String name);
    public User findByEmail(String email);
    public void update(User user);
    public User upsert(User user);

    @DeleteProvider(type=UserSqlProvider.class, method="delete")
    public void delete(User user);

    @SelectProvider(type=UserSqlProvider.class, method="findAllWithAuth")
    public List<User> findAllWithAuth(UserDetails user);

    @SelectProvider(type=UserSqlProvider.class, method="findAllBy")
    public List<User> findAllBy(UserFilterBy filter);
}
