# Maven Liquibase Hibernate Demo

This repository is created to provide with a sample maven spring boot 3 and hibernate 6 application to autogenerate database schema migrations.

The main purpose is to have a sample app that is properly integrated with the liquibase-hibernate6 plugin which enables automatic schema diff generation.

Liquibase Tutorial with Spring Boot - https://reflectoring.io/database-migration-spring-boot-liquibase/

## How to Configure?

The following section will explain the different connections but an easier way to use this is to just copy the configurations in the pom.xml, application.yaml, hibernate.properties, and liquibase.properties.


### Liquibase Maven Plugin Configuration

Setup liquibase by following the `liquibase-maven-plugin` in the plugin section in the pom.xml file

Liquibase configurations are placed under `plugin > liquibase > configuration`

- `configurations` - this is where all the liquibase configurations can be placed through maven but these configurations can be a mix of maven and a - `liquibase.properties`` file
- `<propertyFile>src/main/resources/liquibase.properties</propertyFile>` - describes the location of the **liquibase.properties** file which contains all the configs required by liquibase
- `<propertyFileWillOverride>true</propertyFileWillOverride>` - the name is self-explanatory
- `<diffChangeLogFile> src/main/resources/db/liquibase/changelog/${maven.build.timestamp}_changelog.yaml</diffChangeLogFile>` - creates a unique schema file in the format of `buildtime_changelog.yaml`. This is generated when running the `./mvmnw liquibase:diff` command which will generate a schema file by comparing local entites with the database
- `<username>${env.DB_USER}</username>` - the database username taken from an environmnent variable. The credentials are required for liquibase.
- `<password>${env.DB_PASS}</password>` - the database password taken from an environmnent variable. The credentials are required for liquibase.

### Liquibase Configuration File

The `liquibase.properties` file contains all the configs required by liquibase to function correctly. This can all be configured in a maven `pom.xml` file or a properties file. It's best to always have this properties file as the integration will cause errors in some cases.

File Name `liquibase.properties`
```
# database url for our postgresql to be used with liquibase and hibernate
url=jdbc:postgresql://localhost:5432/cats
# path to the master change log file which controls the migration execution sequence
changeLogFile=src/main/resources/db/liquibase/master.yaml

# override other configs with this one
propertyFileWillOverride=true
# path to a file where we want to output the current database schema based on the state of the database. This is used for pre-existing databases
outputChangeLogFile=db-change-log.sql

# the driver liquibase should use
driver=org.postgresql.Driver

# mandatory reference url for the database we want to compare our db with. In this case we used liquibase-hibernate to compare with hibernate rather than a db
# the starting part will use my models package path where all my hibernate entity classes are stored
referenceUrl=hibernate:spring:com.liquibase.mvnliquibasehibernatedemo.models?dialect=org.hibernate.dialect.PostgreSQLDialect
verbose=true
logging=debug
```

### Hibernate Properties

- `hibernate.properties` this file is required to remove certain liquibase errors that appear but including it will break spring boot so best to keep it commented out or under a spring profle for when you need it.

### Maven Liquibase Plugin Dependencies

liqubiase requires dependencies based on the use case to be functional. An example would be that liquibase doesn't understand which database driver you will use so the dependency list should include a database dependencies such as postgres.
Liquibase dependencies are placed under `plugin > liquibase > dependencies`

- `postgresql` - this is needed so liquibase can connect to postgres. It can be any database dependency being used by spring
- `liquibase-hibernate6` - this is a hibernate and liquibase integration package. Number6 means hibernate 6 compatible. This plugin is required because it generates schema diffs by using hibernate to compare the local entities with the database state.
- `jakarta.validation-api` - jakarta validation is required with hibernate6
- `spring-boot-starter-data-jpa` - required because it holds all the required dependencies when working with jpa


### How to change schema diff generation file format

- change the maven liquibase plugin configuration at  `<diffChangeLogFile> src/main/resources/db/liquibase/changelog/${maven.build.timestamp}_changelog.yaml</diffChangeLogFile>` so that the file name exetension is one of the following: `.sql`, `.yaml`, `xml`, or `json`

## Commands

- `./mvnw liquibase:diff` - generate automatic schema based on differences
- `./mvnw liquibase:update` - push updates to the database
- `./mvnw liquibase:rollback -Dliquibase.rollbackCount=1` - rollback one changeset back.

## Liquibase Flow 

1. Ensure all database configurations are in place
2. Make a change to one of the existing entities or create a new one
3. run `./mvnw liquibase:diff` - this will generate a the file under `src/main/resources/db/liquibase/changelog/${maven.build.timestamp}_changelog.yaml`
4. Validate the file generation is correct and add custom rollback query if needed, otherwise liquibase will assume. Example.
   ```yaml
    # src/main/resources/db/liquibase/changelog/${maven.build.timestamp}_changelog.yaml
    databaseChangeLog:
    - changeSet:
        id: 1705614041282-1
        author: suhailnooristani (generated)
        changes:
            - addColumn:
                columns:
                - column:
                    name: breed
                    type: varchar(20)
                tableName: cats
        # custom rollback. This can be any format, even sql if needed
        rollback:
            - dropColumn:
                columnName: breed
                tableName: cats
   ```
5. open `src/main/resources/db/liquibase/master.yaml` and add this new file to the include list so that liquibase is aware of the order of exection. Example
   ```yaml
    databaseChangeLog:
    - include:
        file: db/liquibase/changelog/2024_01_18T21_36_05Z_changelog.yaml
        # the new one generated
    - include:
        file: db/liquibase/changelog/2024_01_18T21_40_37Z_changelog.yaml
    ```
6. `./mvnw liquibase:update` - will push changes to the database and update the liquibase versioning table.
7. run `./mvnw liquibase:rollback -Dliquibase.rollbackCount=1` - rollback the last change. This will run the rollback and undo any changes made. It will also delete the change execution from the liquibase versioning table in your database. Now, the file that was just rollbacked is still in our code base and it can be deleted.