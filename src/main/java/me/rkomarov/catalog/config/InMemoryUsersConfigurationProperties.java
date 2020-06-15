package me.rkomarov.catalog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("catalog.users")
public class InMemoryUsersConfigurationProperties {
    private String adminName;
    private String adminPassword;
    private String userName;
    private String userPassword;
}
