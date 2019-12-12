package com.computefx.example.wallet;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainerProvider;

@ContextConfiguration(initializers = com.computefx.example.wallet.WithMysqlContainer.Initializer.class)
public abstract class WithMysqlContainer {

    private static final JdbcDatabaseContainer CONTAINER;

    static {
        CONTAINER = new MySQLContainerProvider().newInstance();
        CONTAINER.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + CONTAINER.getJdbcUrl(),
                    "spring.datasource.username=" + CONTAINER.getUsername(),
                    "spring.datasource.password=" + CONTAINER.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
