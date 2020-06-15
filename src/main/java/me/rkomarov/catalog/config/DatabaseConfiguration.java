package me.rkomarov.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("me.rkomarov.catalog.db")
public class DatabaseConfiguration {
}
