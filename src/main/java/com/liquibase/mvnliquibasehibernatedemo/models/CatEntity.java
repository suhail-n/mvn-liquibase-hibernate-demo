package com.liquibase.mvnliquibasehibernatedemo.models;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cats")
public class CatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column
    private int age;
    @Column(length = 20)
    private String breed;

    // @Column(length = 20)
    // private String color;
}
