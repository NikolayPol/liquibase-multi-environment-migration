package ru.homework.liquibaseapp;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
class LiquibaseConfigurationTest {

    private final JdbcTemplate devJdbcTemplate;
    private final JdbcTemplate integrationJdbcTemplate;

    LiquibaseConfigurationTest(
            @Qualifier("devDataSource") DataSource devDataSource,
            @Qualifier("integrationDataSource") DataSource integrationDataSource) {
        this.devJdbcTemplate = new JdbcTemplate(devDataSource);
        this.integrationJdbcTemplate = new JdbcTemplate(integrationDataSource);
    }

    @Test
    void devLiquibaseUsesDedicatedSchemaAndChangelogTables() {
        assertThat(tableExists(devJdbcTemplate, "DEV_LIQUIBASE", "DEV_DATABASECHANGELOG")).isTrue();
        assertThat(tableExists(devJdbcTemplate, "DEV_LIQUIBASE", "DEV_DATABASECHANGELOGLOCK")).isTrue();
        assertThat(tableExists(devJdbcTemplate, "DEV_APP", "DEV_SAMPLE")).isTrue();
    }

    @Test
    void integrationLiquibaseUsesDedicatedSchemaAndChangelogTables() {
        assertThat(tableExists(integrationJdbcTemplate, "INTERGRATION_LIQUIBASE", "INTERGRATION_DATABASECHANGELOG")).isTrue();
        assertThat(tableExists(integrationJdbcTemplate, "INTERGRATION_LIQUIBASE", "INTERGRATION_DATABASECHANGELOGLOCK")).isTrue();
        assertThat(tableExists(integrationJdbcTemplate, "INTERGRATION_APP", "INTEGRATION_SAMPLE")).isTrue();
    }

    @Test
    void masterChangelogsOnlyIncludeMigrationFiles() throws IOException {
        assertMasterOnlyIncludesSql("db/changelog/dev-master.yaml");
        assertMasterOnlyIncludesSql("db/changelog/integration-master.yaml");
    }

    private boolean tableExists(JdbcTemplate jdbcTemplate, String schema, String tableName) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(*)
                from information_schema.tables
                where table_schema = ?
                  and table_name = ?
                """, Integer.class, schema, tableName);
        return count != null && count == 1;
    }

    private void assertMasterOnlyIncludesSql(String path) throws IOException {
        String content = StreamUtils.copyToString(
                new ClassPathResource(path).getInputStream(),
                StandardCharsets.UTF_8);

        assertThat(content).contains("databaseChangeLog:");
        assertThat(content).contains("- include:");
        assertThat(content).contains(".sql");
        assertThat(content).doesNotContain("- changeSet:");
    }
}
