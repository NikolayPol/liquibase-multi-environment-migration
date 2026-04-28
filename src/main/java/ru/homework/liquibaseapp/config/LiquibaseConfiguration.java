package ru.homework.liquibaseapp.config;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiquibaseConfiguration {

    @Bean
    @ConfigurationProperties("app.datasource.dev")
    public DataSourceProperties devDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource devDataSource(
            @Qualifier("devDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties("app.datasource.integration")
    public DataSourceProperties integrationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource integrationDataSource(
            @Qualifier("integrationDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean
    public SpringLiquibase devLiquibase(@Qualifier("devDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/dev-master.yaml");
        liquibase.setDefaultSchema("DEV_APP");
        liquibase.setLiquibaseSchema("DEV_LIQUIBASE");
        liquibase.setDatabaseChangeLogTable("DEV_DATABASECHANGELOG");
        liquibase.setDatabaseChangeLogLockTable("DEV_DATABASECHANGELOGLOCK");
        return liquibase;
    }

    @Bean
    public SpringLiquibase integrationLiquibase(@Qualifier("integrationDataSource") DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/integration-master.yaml");
        liquibase.setDefaultSchema("INTERGRATION_APP");
        liquibase.setLiquibaseSchema("INTERGRATION_LIQUIBASE");
        liquibase.setDatabaseChangeLogTable("INTERGRATION_DATABASECHANGELOG");
        liquibase.setDatabaseChangeLogLockTable("INTERGRATION_DATABASECHANGELOGLOCK");
        return liquibase;
    }
}
