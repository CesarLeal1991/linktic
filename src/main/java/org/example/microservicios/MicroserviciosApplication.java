package org.example.microservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MicroserviciosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviciosApplication.class, args);
    }

    @GetMapping("/hola")
    public String hola() {
        return "¡Microservicio funcionando correctamente!";
    }
}
