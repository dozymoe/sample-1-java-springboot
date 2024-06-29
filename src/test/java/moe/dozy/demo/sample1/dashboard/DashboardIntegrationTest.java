package moe.dozy.demo.sample1.dashboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Disabled;
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

import moe.dozy.demo.sample1.Sample1Properties;
import moe.dozy.demo.sample1.auth.WebUserDetails;
import moe.dozy.demo.sample1.filters.UserFilterBy;
import moe.dozy.demo.sample1.models.User;
import moe.dozy.demo.sample1.services.UserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DashboardIntegrationTest {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    @Autowired
    private MockMvc client;
    @Autowired
    private Sample1Properties properties;
    @Autowired
    private UserService userService;

    @Test
    public void shouldDenyAnonymous() throws Exception {
        client.perform(
                MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers
                    .redirectedUrl("http://localhost/auth/login"));
    }

    @Test
    public void whenAdminParentCompanyVisitDashboard() throws Exception {
        User user = userService.findByName("admin@ptxyz.com");
        beanFactory.autowireBean(user);
        List<User> objects = userService.findAllBy(
                new UserFilterBy(user.getCompany()));
        adminVisitDashboard(user, objects);
    }

    @Test
    public void whenManagerParentCompanyVisitDashboard() throws Exception {
        User user = userService.findByName("manager1@ptxyz.com");
        beanFactory.autowireBean(user);
        var filter = new UserFilterBy(user.getCompany());
        filter.setIncludeSubCompany(true);
        filter.setAnyRoles("supervisor");
        List<User> objects = userService.findAllBy(filter);
        managerVisitDashboard(user, objects);
    }

    @Test
    public void whenAdminCompanyVisitDashboard() throws Exception {
        User user;
        List<User> objects;

        user = userService.findByName("admin@ptxyz1.com");
        beanFactory.autowireBean(user);
        objects = userService.findAllBy(new UserFilterBy(user.getCompany()));
        adminVisitDashboard(user, objects);

        user = userService.findByName("admin@ptxyz2.com");
        beanFactory.autowireBean(user);
        objects = userService.findAllBy(new UserFilterBy(user.getCompany()));
        adminVisitDashboard(user, objects);
    }

    @Test
    public void whenSupervisorCompanyVisitDashboard() throws Exception {
        User user;
        List<User> objects;
        UserFilterBy filter;

        user = userService.findByName("supervisor1@ptxyz1.com");
        beanFactory.autowireBean(user);
        filter = new UserFilterBy(user.getCompany());
        filter.setAnyRoles("user");
        objects = userService.findAllBy(filter);
        supervisorVisitDashboard(user, objects);

        user = userService.findByName("supervisor1@ptxyz2.com");
        beanFactory.autowireBean(user);
        filter = new UserFilterBy(user.getCompany());
        filter.setAnyRoles("user");
        objects = userService.findAllBy(filter);
        supervisorVisitDashboard(user, objects);
    }

    @Test
    public void whenUserCompanyVisitDashboard() throws Exception {
        User user;

        user = userService.findByName("user1@ptxyz1.com");
        beanFactory.autowireBean(user);
        userVisitDashboard(user);

        user = userService.findByName("user1@ptxyz2.com");
        beanFactory.autowireBean(user);
        userVisitDashboard(user);
    }

    private void adminVisitDashboard(User user, List<User> objects) throws Exception {
        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get("/dashboard")
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var trNodes = dom.select("table.table tbody tr");
        assertEquals(trNodes.size(), objects.size());

        var objectRoles = new String[] { "admin", "manager", "supervisor", "user" };
        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (var row : trNodes) {
            var firstCol = row.select("td:nth-child(1)");

            // find user in our list
            var object = objects.stream()
                    .filter(x -> x.getFullName().equals(firstCol.text()))
                    .findFirst().get();
            assertNotNull(object);
            beanFactory.autowireBean(object);

            var secondCol = row.select("td:nth-child(2)");
            assertEquals(object.getCompany().getName(), secondCol.text());
            // must be from same company
            assertEquals(user.getCompany().getName(), secondCol.text());

            var thirdCol = row.select("td:nth-child(3)");
            assertTrue(Arrays.asList(objectRoles).contains(thirdCol.text()));

            var fourthCol = row.select("td:nth-child(4)");
            assertEquals(fourthCol.text(), object.getCreatedAt()
                    .withZoneSameInstant(properties.getZoneId())
                    .format(dateFormatter));

            var fifthCol = row.select("td:nth-child(5)");

            var editLink = fifthCol.select("a:nth-child(1)");
            assertEquals(editLink.text(), "Edit");
            assertEquals(editLink.attr("href"),
                    "/admin/user/" + object.getId().toString() +
                    "/edit?next=/dashboard");

            var deleteLink = fifthCol.select("a:nth-child(2)");
            assertEquals(deleteLink.text(), "Delete");
            assertEquals(deleteLink.attr("href"),
                    "/admin/user/" + object.getId().toString() +
                    "/delete?next=/dashboard");
        }
    }

    private void managerVisitDashboard(User user, List<User> objects) throws Exception {
        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get("/dashboard")
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var trNodes = dom.select("table.table tbody tr");
        assertEquals(trNodes.size(), objects.size());

        var objectRoles = new String[] { "supervisor" };
        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (var row : trNodes) {
            var firstCol = row.select("td:nth-child(1)");

            // find user in our list
            var object = objects.stream()
                    .filter(x -> x.getFullName().equals(firstCol.text()))
                    .findFirst().get();
            assertNotNull(object);
            beanFactory.autowireBean(object);

            var secondCol = row.select("td:nth-child(2)");
            assertEquals(object.getCompany().getName(), secondCol.text());

            var thirdCol = row.select("td:nth-child(3)");
            assertTrue(Arrays.asList(objectRoles).contains(thirdCol.text()));

            var fourthCol = row.select("td:nth-child(4)");
            assertEquals(fourthCol.text(), object.getCreatedAt()
                    .withZoneSameInstant(properties.getZoneId())
                    .format(dateFormatter));

            var fifthCol = row.select("td:nth-child(5)");
            assertEquals(fifthCol.text(), "");
        }
    }

    private void supervisorVisitDashboard(User user, List<User> objects) throws Exception {
        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get("/dashboard")
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var trNodes = dom.select("table.table tbody tr");
        assertEquals(trNodes.size(), objects.size());

        var objectRoles = new String[] { "user" };
        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (var row : trNodes) {
            var firstCol = row.select("td:nth-child(1)");

            // find user in our list
            var object = objects.stream()
                    .filter(x -> x.getFullName().equals(firstCol.text()))
                    .findFirst().get();
            assertNotNull(object);
            beanFactory.autowireBean(object);

            var secondCol = row.select("td:nth-child(2)");
            assertEquals(object.getCompany().getName(), secondCol.text());
            // must be from same company
            assertEquals(user.getCompany().getName(), secondCol.text());

            var thirdCol = row.select("td:nth-child(3)");
            assertTrue(Arrays.asList(objectRoles).contains(thirdCol.text()));

            var fourthCol = row.select("td:nth-child(4)");
            assertEquals(fourthCol.text(), object.getCreatedAt()
                    .withZoneSameInstant(properties.getZoneId())
                    .format(dateFormatter));

            var fifthCol = row.select("td:nth-child(5)");
            assertEquals(fifthCol.text(), "");
        }
    }

    private void userVisitDashboard(User user) throws Exception {
        var dom = Jsoup.parse(client.perform(
                MockMvcRequestBuilders.get("/dashboard")
                    .with(SecurityMockMvcRequestPostProcessors.user(
                        new WebUserDetails(user))))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse().getContentAsString());

        var trNodes = dom.select("table.table tbody tr");
        assertEquals(trNodes.size(), 0);
    }
}
