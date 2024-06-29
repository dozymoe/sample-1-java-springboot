package moe.dozy.demo.sample1.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.Company;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.AuthRoleService;
import moe.dozy.demo.sample1.services.CompanyService;
import moe.dozy.demo.sample1.services.UserService;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthRoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void run(String ...args) throws Exception {
        companyService.upsert(new Company() {{
            setName("PT. XYZ");
        }});
        Company companyRoot = companyService.findByName("PT. XYZ");

        List<Company> companies = new ArrayList<Company>();
        companies.add(companyRoot);
        companyService.upsert(new Company() {{
            setName("PT. XYZ-1");
            setParent(companyRoot);
        }});
        companies.add(companyService.findByName("PT. XYZ-1"));
        companyService.upsert(new Company() {{
            setName("PT. XYZ-2");
            setParent(companyRoot);
        }});
        companies.add(companyService.findByName("PT. XYZ-2"));

        AuthRole roleAdmin = roleService.findByName("admin", null);
        AuthRole roleManager = roleService.findByName("manager", null);
        AuthRole roleSupervisor = roleService.findByName("supervisor", null);
        AuthRole roleUser = roleService.findByName("user", null);

        String password = passwordEncoder.encode("pass");

        for (ListIterator<Company> iter = companies.listIterator(); iter.hasNext();) {
            Company company = iter.next();
            String cleanHost = company.getName()
                    .toLowerCase()
                    .replaceAll("[.\\s-]", "");
            String emailHost = "@" + cleanHost + ".com";

            String emailAdmin = "admin" + emailHost;
            userService.upsert(new User() {{
                setName(emailAdmin);
                setEmail(emailAdmin);
                setFullName("Admin " + company.getName());
                setCompany(company);
                setPassword(password);
            }});
            User userAdmin = userService.findByEmail(emailAdmin);
            beanFactory.autowireBean(userAdmin);
            userAdmin.setRoles(roleAdmin);

            if (company.getParentId() != null) {
                // This is sub-company
                for (int ii : IntStream.rangeClosed(1, 5).toArray()) {
                    String emailSupervisor = "supervisor" + Integer.toString(ii) + emailHost;
                    userService.upsert(new User() {{
                        setName(emailSupervisor);
                        setEmail(emailSupervisor);
                        setFullName("Supervisor" + Integer.toString(ii) + " " + company.getName());
                        setCompany(company);
                        setPassword(password);
                    }});
                    User userSupervisor = userService.findByEmail(emailSupervisor);
                    beanFactory.autowireBean(userSupervisor);
                    userSupervisor.setRoles(roleSupervisor);
                }
                for (int ii : IntStream.rangeClosed(1, 50).toArray()) {
                    String emailUser = "user" + Integer.toString(ii) + emailHost;
                    userService.upsert(new User() {{
                        setName(emailUser);
                        setEmail(emailUser);
                        setFullName("User" + Integer.toString(ii) + " " + company.getName());
                        setCompany(company);
                        setPassword(password);
                    }});
                    User userUser = userService.findByEmail(emailUser);
                    beanFactory.autowireBean(userUser);
                    userUser.setRoles(roleUser);
                }
            } else {
                // This is parent company
                for (int ii : IntStream.rangeClosed(1, 5).toArray()) {
                    String emailManager = "manager" + Integer.toString(ii) + emailHost;
                    userService.upsert(new User() {{
                        setName(emailManager);
                        setEmail(emailManager);
                        setFullName("Manager" + Integer.toString(ii) + " " + company.getName());
                        setCompany(company);
                        setPassword(password);
                    }});
                    User userManager = userService.findByEmail(emailManager);
                    beanFactory.autowireBean(userManager);
                    userManager.setRoles(roleManager);
                }
            }
        }
    }
}
