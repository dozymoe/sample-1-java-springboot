package moe.dozy.demo.sample1.repositories;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import moe.dozy.demo.sample1.models.AuthPermission;

@Mapper
public interface AuthPermissionRepository {
    public List<AuthPermission> findAll();
    public AuthPermission findById(Long id);
    public AuthPermission findByName(
        @Param("name") String name,
        @Param("guard") String guard_name);
    public AuthPermission upsert(AuthPermission perm);
}
