package moe.dozy.demo.sample1.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import moe.dozy.demo.sample1.auth.WebUserDetails;
import moe.dozy.demo.sample1.models.AuthRole;
import moe.dozy.demo.sample1.models.Company;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.AuthRoleService;
import moe.dozy.demo.sample1.services.CompanyService;
import moe.dozy.demo.sample1.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminEditUserIntegrationTest {

    private String BASENAME = "AdminEditUserIntegration";

    private String user1Email;
    private String user1Url;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    @Autowired
    private MockMvc client;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private AuthRoleService roleService;
    @Autowired
    private UserService userService;

    @Test
    public void shouldDenyAnonymous() throws Exception {
        client.perform(
                MockMvcRequestBuilders.get(this.user1Url))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers
                    .redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    public void whenAdminVisitEditUser() throws Exception {
        User user = userService.findByName("admin@ptxyz1.com");
        beanFactory.autowireBean(user);

        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var btnSubmit = dom.select("form.edit-user button[type='submit']");
        assertEquals(btnSubmit.size(), 1);
        assertEquals(btnSubmit.text(), "Update");

        var lnkBack = dom.select("form.edit-user a.go-back");
        assertEquals(lnkBack.size(), 1);

        AuthRole roleSupervisor = roleService.findByName("supervisor", null);

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .formField("name", BASENAME)
                    .formField("email", user1Email)
                    .formField("role_id", roleSupervisor.getId().toString()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers
                    .redirectedUrl("/dashboard"));

        var stored = userService.findByEmail(user1Email);
        beanFactory.autowireBean(stored);
        assertNotNull(stored);
        assertEquals(stored.getName(), BASENAME);
        assertTrue(stored.getRoleNames().contains("supervisor"));
    }

    @Test
    public void whenOtherAdminVisitEditUser() throws Exception {
        User user = userService.findByName("admin@ptxyz2.com");
        beanFactory.autowireBean(user);

        client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void whenManagerVisitEditUser() throws Exception {
        User user = userService.findByName("manager1@ptxyz.com");
        beanFactory.autowireBean(user);

        client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void whenSupervisorVisitEditUser() throws Exception {
        User user = userService.findByName("supervisor1@ptxyz1.com");
        beanFactory.autowireBean(user);

        client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    public void whenUserVisitEditUser() throws Exception {
        User user = userService.findByName("user1@ptxyz1.com");
        beanFactory.autowireBean(user);

        client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @BeforeEach
    private void setup() {
        String user1Name = BASENAME + "User1";
        Company company = companyService.findByName("PT. XYZ-1");

        this.user1Email = user1Name + "@ptxyz1.com";
        userService.upsert(new User() {{
            setName(user1Name);
            setEmail(user1Email);
            setFullName(user1Name);
            setCompany(company);
            setPassword("pass");
        }});
        var user1 = userService.findByEmail(this.user1Email);
        beanFactory.autowireBean(user1);

        AuthRole roleUser = roleService.findByName("user", null);
        user1.setRoles(roleUser);

        this.user1Url = "/admin/user/" + user1.getId().toString() +
                "/edit?next=/dashboard";
    }
}
