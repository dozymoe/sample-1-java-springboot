package moe.dozy.demo.sample1.dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

public class DashboardIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void shouldDenyAnonymous() {
    }

}
