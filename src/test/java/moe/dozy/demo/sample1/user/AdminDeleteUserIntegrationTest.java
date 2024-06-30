package moe.dozy.demo.sample1.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
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
public class AdminDeleteUserIntegrationTest {

    private String BASENAME = "AdminDeleteUserIntegration";

    private String user1Email;
    private String user1Url;

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private MockMvc client;

    @Test
    public void shouldDenyAnonymous() throws Exception {
        client.perform(
                MockMvcRequestBuilders.get(this.user1Url))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers
                    .redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    public void whenAdminVisitDeleteUser() throws Exception {
        var userService = new UserService(appContext);

        User user = userService.findByName("admin@ptxyz1.com");

        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var btnSubmit = dom.select("form.delete-user button[type='submit']");
        assertEquals(btnSubmit.size(), 1);
        assertEquals(btnSubmit.text(), "Delete");

        var lnkBack = dom.select("form.delete-user a.go-back");
        assertEquals(lnkBack.size(), 1);

        client.perform(
                MockMvcRequestBuilders.post(this.user1Url)
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user)))
                    .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers
                    .redirectedUrl("/dashboard"));

        assertNull(userService.findByEmail(user1Email));
    }

    @Test
    public void whenOtherAdminVisitDeleteUser() throws Exception {
        var userService = new UserService(appContext);

        User user = userService.findByName("admin@ptxyz2.com");

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
    public void whenManagerVisitDeleteUser() throws Exception {
        var userService = new UserService(appContext);

        User user = userService.findByName("manager1@ptxyz.com");

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
    public void whenSupervisorVisitDeleteUser() throws Exception {
        var userService = new UserService(appContext);

        User user = userService.findByName("supervisor1@ptxyz1.com");

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
    public void whenUserVisitDeleteUser() throws Exception {
        var userService = new UserService(appContext);

        User user = userService.findByName("user1@ptxyz1.com");

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
        var companyService = new CompanyService(appContext);
        var userService = new UserService(appContext);
        var roleService = new AuthRoleService(appContext);

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

        AuthRole roleUser = roleService.findByName("user", null);
        user1.setRoles(roleUser);

        this.user1Url = "/admin/user/" + user1.getId().toString() +
                "/delete?next=/dashboard";
    }
}
