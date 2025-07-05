package com.springjwt.sprignbootjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SprignBootJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprignBootJwtApplication.class, args);
    }

}
