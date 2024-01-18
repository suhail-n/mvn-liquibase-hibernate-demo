package com.liquibase.mvnliquibasehibernatedemo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquibase.mvnliquibasehibernatedemo.models.CatEntity;

public interface CatRepository extends JpaRepository<CatEntity, Long> {

}
