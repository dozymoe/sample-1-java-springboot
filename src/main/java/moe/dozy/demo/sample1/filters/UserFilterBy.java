package moe.dozy.demo.sample1.filters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import moe.dozy.demo.sample1.models.Company;

@Getter
@Setter
public class UserFilterBy implements Serializable {
    private Company company;
    private Boolean includeSubCompany;
    private List<String> anyRoles;

    public UserFilterBy() {
        this.includeSubCompany = false;
        this.anyRoles = new ArrayList<String>();
    }

    public UserFilterBy(Company company) {
        this.company = company;
        this.includeSubCompany = false;
        this.anyRoles = new ArrayList<String>();
    }

    public void setAnyRoles(String ...roles) {
        this.anyRoles.clear();
        for (var role : roles) {
            this.anyRoles.add(role);
        }
    }
}
