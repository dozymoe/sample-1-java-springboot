package moe.dozy.demo.sample1.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.Company;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.AuthRoleService;
import moe.dozy.demo.sample1.services.CompanyService;
import moe.dozy.demo.sample1.services.UserService;

@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(String ...args) throws Exception {
        var sqlSession = SqlSessionManager.newInstance(sqlSessionFactory);
        sqlSession.startManagedSession(ExecutorType.BATCH,
                TransactionIsolationLevel.READ_COMMITTED);
        try {
            var companyService = new CompanyService(sqlSession, appContext);
            var roleService = new AuthRoleService(sqlSession, appContext);
            var userService = new UserService(sqlSession, appContext);

            Company companyRoot = companyService.upsert(new Company() {{
                setName("PT. XYZ");
            }});

            List<Company> companies = new ArrayList<Company>();
            companies.add(companyRoot);

            companies.add(companyService.upsert(new Company() {{
                setName("PT. XYZ-1");
                setParent(companyRoot);
            }}));
            companies.add(companyService.upsert(new Company() {{
                setName("PT. XYZ-2");
                setParent(companyRoot);
            }}));

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

                User userAdmin = userService.upsert(new User() {{
                    setName(emailAdmin);
                    setEmail(emailAdmin);
                    setFullName("Admin " + company.getName());
                    setCompany(company);
                    setPassword(password);
                    setSqlSession(sqlSession);
                }});
                userAdmin.setRoles(roleAdmin);

                if (company.getParentId() != null) {
                    // This is sub-company
                    for (int ii : IntStream.rangeClosed(1, 5).toArray()) {
                        String emailSupervisor = "supervisor" + Integer.toString(ii) + emailHost;
                        User userSupervisor = userService.upsert(new User() {{
                            setName(emailSupervisor);
                            setEmail(emailSupervisor);
                            setFullName("Supervisor" + Integer.toString(ii) + " " + company.getName());
                            setCompany(company);
                            setPassword(password);
                            setSqlSession(sqlSession);
                        }});
                        userSupervisor.setRoles(roleSupervisor);
                    }
                    for (int ii : IntStream.rangeClosed(1, 50).toArray()) {
                        String emailUser = "user" + Integer.toString(ii) + emailHost;
                        User userUser = userService.upsert(new User() {{
                            setName(emailUser);
                            setEmail(emailUser);
                            setFullName("User" + Integer.toString(ii) + " " + company.getName());
                            setCompany(company);
                            setPassword(password);
                            setSqlSession(sqlSession);
                        }});
                        userUser.setRoles(roleUser);
                    }
                } else {
                    // This is parent company
                    for (int ii : IntStream.rangeClosed(1, 5).toArray()) {
                        String emailManager = "manager" + Integer.toString(ii) + emailHost;
                        User userManager = userService.upsert(new User() {{
                            setName(emailManager);
                            setEmail(emailManager);
                            setFullName("Manager" + Integer.toString(ii) + " " + company.getName());
                            setCompany(company);
                            setPassword(password);
                            setSqlSession(sqlSession);
                        }});
                        userManager.setRoles(roleManager);
                    }
                }
            }

            sqlSession.commit();
        } catch (Throwable t) {
            sqlSession.rollback();
            throw t;
        } finally {
            sqlSession.close();
        }

    }
}
