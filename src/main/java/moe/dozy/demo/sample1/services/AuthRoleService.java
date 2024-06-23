package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import moe.dozy.demo.sample1.models.AuthRole;

@Mapper
public interface AuthRoleService {
    public List<AuthRole> findAll();
    public AuthRole findById(Long id);
    public AuthRole findByName(
        @Param("name") String name,
        @Param("guard") String guard_name);
    public AuthRole upsert(AuthRole role);
}
