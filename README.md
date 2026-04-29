# Liquibase App

Сервис демонстрирует инфраструктурную настройку Liquibase для нескольких баз данных: `dev` и `integration`.
Для каждой базы данных используется отдельный datasource, master changelog, application schema и набор таблиц Liquibase metadata. Такой подход позволяет независимо управлять миграциями, хранить историю изменений отдельно для каждой базы данных и проверять корректность настройки автоматическими тестами.

## Технологии

- Java 21
- Spring Boot 3.5
- Spring JDBC
- Liquibase
- H2 in-memory database
- Maven
- JUnit 5

## Главное по Liquibase

Автоматический запуск Liquibase от Spring Boot отключен:

```properties
spring.liquibase.enabled=false
```

Вместо этого в `LiquibaseConfiguration` созданы два `SpringLiquibase` bean:

- `devLiquibase` для `devdb`
- `integrationLiquibase` для `integrationdb`

Changelog-и:

- `src/main/resources/db/changelog/dev-master.yaml`
- `src/main/resources/db/changelog/integration-master.yaml`

SQL changeset-ы:

- `dev/changeset-001-dev-sample.sql`
- `integration/changeset-001-integration-sample.sql`

## Как запустить

Запустить приложение:

```bash
mvn spring-boot:run
```

Запустить тесты:

```bash
mvn test
```

## GitHub Actions

CI workflow запускается на `push` и `pull_request` в `main`.

Он:

- поднимает `ubuntu-latest`;
- делает checkout репозитория;
- ставит Java 21 через `actions/setup-java@v4`;
- кеширует Maven зависимости;
- запускает `mvn test`.
