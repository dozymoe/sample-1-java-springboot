package moe.dozy.demo.sample1.repositories;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import moe.dozy.demo.sample1.models.Company;

@Mapper
public interface CompanyRepository {
    public List<Company> findAll();
    public Company findById(Long id);
    public Company findByName(String name);
    public Company upsert(Company company);
}
