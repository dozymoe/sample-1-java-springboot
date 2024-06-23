package moe.dozy.demo.sample1;

import java.util.TimeZone;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(Sample1Properties.class)
public class Sample1Application {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(Sample1Application.class, args);
	}

    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        return new VendorDatabaseIdProvider();
    }
}
