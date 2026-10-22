package com.mygroceries.backend;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionPoolConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withInitializer(new ConfigDataApplicationContextInitializer())
            .withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
            .withPropertyValues(
                    "spring.datasource.url=jdbc:postgresql://localhost:5432/config_test",
                    "spring.datasource.username=config_test",
                    "spring.datasource.password=unused"
            );

    @Test
    void poolStartsSmallWithoutReducingCapacityOrDisablingValidation() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            HikariDataSource pool = context.getBean(HikariDataSource.class);
            pool.validate();
            assertThat(pool.getMinimumIdle()).isEqualTo(1);
            assertThat(pool.getMaximumPoolSize()).isEqualTo(10);
            assertThat(pool.getInitializationFailTimeout()).isPositive();
            assertThat(pool.isRunning()).isFalse();
            assertThat(context.getEnvironment().getProperty("spring.jpa.hibernate.ddl-auto"))
                    .isEqualTo("validate");
            assertThat(context.getEnvironment().getProperty("spring.jpa.show-sql"))
                    .isEqualTo("false");
        });
    }

    @Test
    void poolSizesCanBeOverriddenThroughEnvironmentPlaceholders() {
        contextRunner.withPropertyValues(
                "SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=2",
                "SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=4"
        ).run(context -> {
            assertThat(context).hasNotFailed();
            HikariDataSource pool = context.getBean(HikariDataSource.class);
            pool.validate();
            assertThat(pool.getMinimumIdle()).isEqualTo(2);
            assertThat(pool.getMaximumPoolSize()).isEqualTo(4);
        });
    }
}
