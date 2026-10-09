package com.kubsei.editor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class EditorApplication {
    public static void main(String[] args) {
        SpringApplication.run(EditorApplication.class, args);
    }
}
