package moe.dozy.demo.sample1.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import moe.dozy.demo.sample1.services.CompanyService;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Company implements Serializable {

    @Autowired
    private ApplicationContext appContext;

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    @ToString.Include
    private String name;
    private Long parent_id;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;

    private SqlSession sqlSession;

    public Long getParentId() { return parent_id; }
    public void setParentId(Long id) { this.parent_id = id; }
    public ZonedDateTime getCreatedAt() { return created_at; }
    public void setCreatedAt(ZonedDateTime date) { this.created_at = date; }
    public ZonedDateTime getUpdatedAt() { return updated_at; }
    public void setUpdatedAt(ZonedDateTime date) { this.updated_at = date; }

    public Company getParent() {
        if (parent_id == null) {
            return null;
        }
        var companyService = getCompanyService();
        return companyService.findById(parent_id);
    }

    public void setParent(Company company) {
        if (company == null) {
            this.parent_id = null;
        } else {
            this.parent_id = company.id;
        }
    }

    private CompanyService getCompanyService() {
        if (sqlSession != null) {
            return new CompanyService(sqlSession, appContext);
        }
        return new CompanyService(appContext);
    }
}
