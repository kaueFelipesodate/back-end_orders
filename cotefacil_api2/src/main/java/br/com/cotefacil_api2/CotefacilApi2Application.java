package br.com.cotefacil_api2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class CotefacilApi2Application {

    public static void main(String[] args) {
        SpringApplication.run(CotefacilApi2Application.class, args);
    }

}
