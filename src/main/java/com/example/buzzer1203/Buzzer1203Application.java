package com.example.buzzer1203;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Buzzer1203Application {

    public static void main(String[] args) {
        SpringApplication.run(Buzzer1203Application.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "Hello Buzzer1203!";
    }

}