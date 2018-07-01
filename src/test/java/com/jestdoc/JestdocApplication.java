package com.jestdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class JestdocApplication {

    public static void main(String[] args) {
        SpringApplication.run(JestdocApplication.class, args);
    }

}
