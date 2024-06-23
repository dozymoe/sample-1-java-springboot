package moe.dozy.demo.sample1;

import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@ConfigurationProperties("moe.dozy.demo.sample1")
@Getter
@NoArgsConstructor
@Setter
public class Sample1Properties {

    private String timezone = "UTC";

    public ZoneId getZoneId() {
        return ZoneId.of(timezone);
    }
}
