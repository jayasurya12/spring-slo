package com.slo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SloApplication {
    public static void main(String[] args) {
        SpringApplication.run(SloApplication.class, args);
        System.out.println("✅ Spring SLO App started — http://localhost:" +
                System.getProperty("server.port", "8080"));
    }
}
