package com.liquibase.mvnliquibasehibernatedemo;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("cats")
public class CatsController {
    @GetMapping
    public String getCats() {
        return "Cats";
    }

}
