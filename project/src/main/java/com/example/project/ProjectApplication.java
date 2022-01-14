package com.example.project;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Documentatie Pet Shop")
                        .description("Aplicația constă într-un sistem de monitorizare a unui magazin pentru animale, în cadrul căruia se comercializează mâncare, medicamente și jucării. Un client al magazinului, care poate deține sau nu animale, poate efectua comenzi către magazin. În cadrul unei comenzi, acesta poate adaugă diferite produse, din oricare cele 3 categorii existente. La final, se calculează prețul comenzii, adunându-se prețul pentru fiecare produs (nu sunt percepute taxe de livrare). ")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));

    }

}
