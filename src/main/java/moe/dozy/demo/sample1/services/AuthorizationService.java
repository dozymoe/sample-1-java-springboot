package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import moe.dozy.demo.sample1.models.AuthPermission;
import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.User;

@Mapper
public interface AuthorizationService {
    public List<AuthRole> findAllUserRoles(
            @Param("user") User user,
            @Param("guard") String guard_name);
    public void setUserRoles(
            @Param("user") User user,
            @Param("guard") String guard_name,
            @Param("roles") AuthRole ...roles);
    public List<AuthPermission> findAllUserPermissions(
            @Param("user") User user,
            @Param("guard") String guard_name);
    public List<AuthPermission> findAllRolePermissions(
            @Param("role") AuthRole role,
            @Param("guard") String guard_name);
}
