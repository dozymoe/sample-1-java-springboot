package moe.dozy.demo.sample1.services;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.ApplicationContext;

import moe.dozy.demo.sample1.models.Company;
import moe.dozy.demo.sample1.repositories.CompanyRepository;

public class CompanyService extends BaseService {

    private CompanyRepository repository;

    public CompanyService(ApplicationContext ctx) {
        super(ctx);
        this.repository = ctx.getBean(CompanyRepository.class);
    }

    public CompanyService(SqlSession sqlSession, ApplicationContext ctx) {
        super(ctx);
        this.repository = sqlSession.getMapper(CompanyRepository.class);
    }

    public List<Company> findAll() {
        var objs = repository.findAll();
        objs.forEach(obj -> {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        });
        return objs;
    }

    public Company findById(Long id) {
        var obj = repository.findById(id);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public Company findByName(String name) {
        var obj = repository.findByName(name);
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    public Company upsert(Company company) {
        var obj = repository.upsert(company);
        if (obj == null) {
            obj = repository.findByName(company.getName());
        }
        if (obj != null) {
            beanFactory.autowireBean(obj);
            applyUserTimeZone(obj);
        }
        return obj;
    }

    protected void applyUserTimeZone(Company obj) {
        obj.setCreatedAt(obj.getCreatedAt()
                .withZoneSameInstant(properties.getZoneId()));
        obj.setUpdatedAt(obj.getUpdatedAt()
                .withZoneSameInstant(properties.getZoneId()));
    }
}
