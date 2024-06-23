package moe.dozy.demo.sample1.models;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;

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
    private CompanyService companyService;

    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;
    @ToString.Include
    private String name;
    private Long parent_id;
    private ZonedDateTime created_at;
    private ZonedDateTime updated_at;

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
        return companyService.findById(parent_id);
    }

    public void setParent(Company company) {
        if (company == null) {
            this.parent_id = null;
        } else {
            this.parent_id = company.id;
        }
    }
}
