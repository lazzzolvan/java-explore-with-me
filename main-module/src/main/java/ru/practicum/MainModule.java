package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.practicum", "ru.practicum"})
public class MainModule {
    public static void main(String[] args) {
        SpringApplication.run(MainModule.class, args);
    }
}
